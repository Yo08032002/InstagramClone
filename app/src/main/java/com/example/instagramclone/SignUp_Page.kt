package com.example.instagramclone

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.instagramclone.Models.User
import com.example.instagramclone.Models.utils.USER_NODE
import com.example.instagramclone.Models.utils.USER_PROFILE_FOLDER
import com.example.instagramclone.Models.utils.uploadImage
import com.example.instagramclone.databinding.ActivitySignupPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso


class SignUp_Page : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignupPageBinding.inflate(layoutInflater)
    }
    lateinit var user: User

    //function to add photo from gallery
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()){
        uri->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER){
                if(it==null){
                        Toast.makeText(this@SignUp_Page,"Select Image First",Toast.LENGTH_SHORT).show()
                }
                else{
                    user.image=it
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val text = "<font color=#FF000000>Already have Account? </font> <font color=#438AD5>Login</font>"
        binding.already.setText(Html.fromHtml(text))

        user=User()

        if(intent.hasExtra("MODE")){
            if(intent.getIntExtra("MODE",-1)==1){
                binding.loginButton.text="Update Profile"
                Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener {

                            user = it.toObject<User>()!!
                            if (!user.image.isNullOrEmpty()) {
                                Picasso.get().load(user.image).into(binding.profileImage)
                            }
                            binding.name.editText?.setText(user.name)
                            binding.email.editText?.setText(user.email)
                            binding.password.editText?.setText(user.password)
                    }
            }
        }
        binding.loginButton.setOnClickListener {
            if (intent.hasExtra("MODE")) {
                if (intent.getIntExtra("MODE", -1) == 1) {

                    Firebase.firestore.collection(USER_NODE)
                        .document(Firebase.auth.currentUser!!.uid).set(user)
                        .addOnSuccessListener {
                            startActivity(
                                Intent(
                                    this@SignUp_Page,
                                    HomeActivity::class.java
                                )
                            )
                            finish()
                        }

                }
            } else {
                if ((binding.name.editText?.text.toString() == "") or
                    (binding.email.editText?.text.toString() == "") or
                    (binding.password.editText?.text.toString() == "")
                ) {
                    Toast.makeText(this@SignUp_Page, "Fill Details First", Toast.LENGTH_LONG).show()
                } else {

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        binding.email.editText?.text.toString(),
                        binding.password.editText?.text.toString()
                    ).addOnCompleteListener { result ->

                        if (result.isSuccessful) {
//                        Toast.makeText(this@SignUp_Page, "Login Successfully", Toast.LENGTH_SHORT).show()
                            user.name = binding.name.editText?.text.toString()
                            user.email = binding.email.editText?.text.toString()
                            user.password = binding.password.editText?.text.toString()

                            Firebase.firestore.collection(USER_NODE)
                                .document(Firebase.auth.currentUser!!.uid).set(user)
                                .addOnSuccessListener {
//                            Toast.makeText(this@SignUp_Page,"login",Toast.LENGTH_LONG).show()
                                    startActivity(
                                        Intent(
                                            this@SignUp_Page,
                                            HomeActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                        } else
                            Toast.makeText(
                                this@SignUp_Page,
                                result.exception?.localizedMessage,
                                Toast.LENGTH_LONG
                            ).show()
                    }
                }
            }
        }
        binding.plus.setOnClickListener{
            launcher.launch("image/*")
        }

        binding.already.setOnClickListener {
            startActivity(Intent(this@SignUp_Page,LoginActivity::class.java))
        }

    }
}