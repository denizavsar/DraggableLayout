package com.deniz.draggable

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("ClickableViewAccessibility")
class FrameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.draggable_enter_animation, 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)
    }
}
