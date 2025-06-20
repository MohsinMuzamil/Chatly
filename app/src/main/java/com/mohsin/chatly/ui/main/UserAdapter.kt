package com.mohsin.chatly.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mohsin.chatly.R
import com.mohsin.chatly.data.model.User
import android.widget.ImageView
import com.bumptech.glide.Glide

class UserAdapter(
    private val users: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tvUserName)
        private val avatarImageView: ImageView = itemView.findViewById(R.id.ivUserAvatar)

        fun bind(user: User) {
            val nameToShow = if (user.displayName.isNullOrBlank() || user.displayName == "Anonymous") {
                user.email.substringBefore("@")
            } else {
                user.displayName
            }
            nameTextView.text = nameToShow

            // Load Cloudinary image if available, else use placeholder
            if (!user.photoUrl.isNullOrBlank()) {
                Glide.with(itemView.context)
                    .load(user.photoUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .circleCrop()
                    .into(avatarImageView)
            } else {
                avatarImageView.setImageResource(R.drawable.ic_profile_placeholder)
            }

            itemView.setOnClickListener {
                onUserClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size
}