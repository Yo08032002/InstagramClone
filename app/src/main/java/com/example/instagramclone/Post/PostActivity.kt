package com.example.instagramclone.Post

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.instagramclone.HomeActivity
import com.example.instagramclone.Models.Post
import com.example.instagramclone.Models.User
import com.example.instagramclone.Models.utils.POST
import com.example.instagramclone.Models.utils.POST_FOLDER
import com.example.instagramclone.Models.utils.USER_NODE
import com.example.instagramclone.Models.utils.uploadPhoto
import com.example.instagramclone.databinding.ActivityPostBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


class PostActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }
    var imageUrl: String? = null
    lateinit var progressDialog: ProgressDialog

    //function to add photo from gallery
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadPhoto(uri, POST_FOLDER, progressDialog) { url ->
                if (url != null) {
                    binding.selectImg.setImageURI(uri)
                    imageUrl = url
                } else
                    Toast.makeText(this@PostActivity, "Select Image First", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)

        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.materialToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.selectImg.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.postButton.setOnClickListener {

            if (imageUrl != null) {
                Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                        var user = it.toObject<User>()!!
                        val post: Post = Post(
                            postUrl = imageUrl!!,
                            caption = binding.caption.editText?.text.toString(),
                            uid = Firebase.auth.currentUser!!.uid,
                            time = System.currentTimeMillis().toString()
                        )

                        Firebase.firestore.collection(POST).document().set(post).addOnSuccessListener {
                            Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document()
                                .set(post)
                                .addOnSuccessListener {
                                    startActivity(Intent(this@PostActivity, HomeActivity::class.java))
                                    finish()
                                }
                        }
                    }
            } else {
                Toast.makeText(this@PostActivity, "Select Image First", Toast.LENGTH_LONG).show()
            }
        }
        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }
    }
}