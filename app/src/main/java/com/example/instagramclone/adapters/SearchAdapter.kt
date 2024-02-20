package com.example.instagramclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramclone.Models.User
import com.example.instagramclone.Models.utils.Follow
import com.example.instagramclone.R
import com.example.instagramclone.databinding.SearchRvBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class SearchAdapter(var context: Context,var userList:ArrayList<User>):RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(var binding:SearchRvBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding=SearchRvBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var isfollow=false
        Glide.with(context).load(userList[position].image).placeholder(R.drawable.user).into(holder.binding.profileImage)
        holder.binding.name.text=userList.get(position).name

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ Follow)
            .whereEqualTo("email",userList.get(position).email).get().addOnSuccessListener {
                if (it.documents.size==0){
                    isfollow=false
                }
                else{
                    holder.binding.follow.text="Unfollow"
                    isfollow=true
                }
            }

        holder.binding.follow.setOnClickListener {
            if(isfollow){
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ Follow)
                    .whereEqualTo("email",userList.get(position).email).get().addOnSuccessListener {

                        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ Follow)
                            .document(it.documents.get(0).id).delete()
                        holder.binding.follow.text="Follow"
                        isfollow=false
                    }
            }
            else {
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + Follow).document()
                    .set(userList.get(position))
                holder.binding.follow.text = "Unfollow"
                isfollow=true
            }
        }
    }

}