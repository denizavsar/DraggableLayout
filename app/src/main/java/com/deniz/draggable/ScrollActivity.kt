package com.deniz.draggable

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.deniz.draggablelibrary.DraggableScrollViewLayout

@SuppressLint("ClickableViewAccessibility")
class ScrollActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.draggable_enter_animation, 0)
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }

        setContentView(R.layout.activity_scroll)

        findViewById<DraggableScrollViewLayout>(R.id.root).setDragListener(object :
            DraggableScrollViewLayout.DragListener {
            override fun onDragStarted(rawX: Float, rawY: Float) {
                log("Drag Start")
            }

            override fun onDrag(touchDeltaX: Float, touchDeltaY: Float) {
                log("ON Drag")
            }

            override fun onDragFinished() {
                log("Drag Finish")
            }

        })

        findViewById<DraggableScrollViewLayout>(R.id.root).setScrollListener(object :
            DraggableScrollViewLayout.ScrollListener {
            override fun onScrollStarted(rawX: Float, rawY: Float) {
                log("Scroll Start")
            }

            override fun onScroll(scrollX: Int, scrollY: Int) {
                log("On Scroll")
            }

            override fun onScrollFinished() {
                log("Scroll Finish")
            }

        })

    }

    private fun log(s: String) {
        Log.d("Draggable", s)
    }
}
