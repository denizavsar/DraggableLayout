package com.deniz.draggable

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("ClickableViewAccessibility")
class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.draggable_enter_animation, 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        /*
        findViewById<TextView>(R.id.zxc).setOnClickListener {
            Toast.makeText(applicationContext, "Disable", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.qwe).setOnClickListener {
            Toast.makeText(applicationContext, "Enable", Toast.LENGTH_SHORT).show()
        }

         */
        findViewById<ImageView>(R.id.qwe).setOnClickListener {
            Toast.makeText(applicationContext, "Enable", Toast.LENGTH_SHORT).show()
        }
    }
}
