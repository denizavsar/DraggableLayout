package com.deniz.draggable

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.frame).setOnClickListener {
            val intent = Intent(this, FrameActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.scroll).setOnClickListener {
            val intent = Intent(this, ScrollActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.bottom_sheet).setOnClickListener {
            val intent = Intent(this, BottomSheetActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.image_display).setOnClickListener {
            val intent = Intent(this, ImageDisplayActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.popup).setOnClickListener {
            val intent = Intent(this, PopupActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.info).setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }


        findViewById<TextView>(R.id.java).setOnClickListener {
            val intent = Intent(this, JavaActivity::class.java)
            startActivity(intent)
        }
    }
}
