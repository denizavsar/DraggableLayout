package com.deniz.draggable

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PopupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.alpha_in_animation, 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup)

        findViewById<FrameLayout>(R.id.root).setOnClickListener {
            finish()
            overridePendingTransition(0, R.anim.alpha_out_animation)
        }

        findViewById<TextView>(R.id.ok_button).setOnClickListener {
            Toast.makeText(applicationContext, "OK", Toast.LENGTH_SHORT).show()

            finish()
            overridePendingTransition(0, R.anim.alpha_out_animation)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.alpha_out_animation)
    }
}
