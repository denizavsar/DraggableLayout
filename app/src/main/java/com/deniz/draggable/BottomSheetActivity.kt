package com.deniz.draggable

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BottomSheetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.draggable_enter_animation, 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_sheet)
    }
}
