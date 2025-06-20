package com.mohsin.chatly.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mohsin.chatly.R
import com.mohsin.chatly.data.model.Message
import com.mohsin.chatly.util.DateUtils

class ChatAdapter(
    private var messages: List<Message>,
    private val currentUserId: String,
    private val onMessageLongClick: (Message) -> Unit
) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val tvTimestamp: TextView = view.findViewById(R.id.tvTimestamp)
        val readIndicator: View? = view.findViewById(R.id.readIndicator)
    }




    override fun getItemViewType(position: Int): Int {
        // Right side (sent by me): 1, Left side (others): 0
        return if (messages[position].senderId == currentUserId) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutId = if (viewType == 1) {
            // Your messages: show on the right
            R.layout.item_message_sent
        } else {
            // Other messages: show on the left
            R.layout.item_message_received
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.tvMessage.text = message.content
        holder.tvTimestamp.text = DateUtils.formatTimestamp(message.timestamp)
        holder.readIndicator?.visibility = if (message.senderId == currentUserId) View.VISIBLE else View.GONE

        // Only show read indicator if present in the layout
        holder.readIndicator?.let {
            if (message.senderId == currentUserId && message.isMine) {
                it.visibility = View.VISIBLE
            } else {
                it.visibility = View.GONE
            }
        }

        // Handle long press for delete
        holder.itemView.setOnLongClickListener {
            onMessageLongClick(message)
            true

    }
}
    override fun getItemCount(): Int = messages.size

    fun submitList(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}
