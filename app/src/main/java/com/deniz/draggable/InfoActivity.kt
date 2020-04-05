package com.deniz.draggable

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.alpha_in_animation, 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        findViewById<ConstraintLayout>(R.id.root).setOnClickListener {
            finish()
            overridePendingTransition(0, R.anim.alpha_out_animation)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.alpha_out_animation)
    }
}
