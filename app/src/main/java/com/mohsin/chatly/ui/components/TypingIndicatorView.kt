package com.mohsin.chatly.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.mohsin.chatly.R

class TypingIndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private val textView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_typing_indicator, this, true)
        textView = findViewById(R.id.tvTyping)
    }

    fun showTyping(user: String) {
        textView.text = "$user is typing..."
        visibility = VISIBLE
    }

    fun hideTyping() {
        visibility = GONE
    }
}