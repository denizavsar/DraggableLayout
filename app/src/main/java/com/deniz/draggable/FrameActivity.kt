package com.deniz.draggable

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.deniz.draggablelibrary.DraggableFrameLayout

@SuppressLint("ClickableViewAccessibility")
class FrameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.draggable_enter_animation, 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)

        val root = findViewById<DraggableFrameLayout>(R.id.root)

        root.setDragListener(object : DraggableFrameLayout.DragListener {
            override fun onDragStarted(rawX: Float, rawY: Float) {
                Log.d(
                    "StoryActivity",
                    "On Drag Started -- rawX: $rawX, rawY: $rawY"
                )
            }

            override fun onDrag(rawX: Float, rawY: Float, touchDeltaX: Float, touchDeltaY: Float) {
                Log.d(
                    "StoryActivity",
                    "On Drag --  rawX: $rawX, rawY: $rawY, deltaX: $touchDeltaX, deltaY: $touchDeltaY"
                )
            }

            override fun onDragFinishing(distance: Float) {
                Log.d(
                    "StoryActivity",
                    "On Drag Finishing -- Distance: $distance"
                )
            }

            override fun onDragFinished() {
                Log.d(
                    "StoryActivity",
                    "On Drag Finished"
                )
            }
        })
    }
}
