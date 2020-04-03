package com.deniz.draggable;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.deniz.draggablelibrary.DraggableScrollViewLayout;

public class JavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.draggable_enter_animation, 0);
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }

        setContentView(R.layout.activity_third);

        DraggableScrollViewLayout layout = findViewById(R.id.root);
        layout.setDragListener(new DraggableScrollViewLayout.DragListener() {
            @Override
            public void onDragFinished() {

            }

            @Override
            public void onDrag(float rawX, float rawY, float touchDeltaX, float touchDeltaY) {

            }

            @Override
            public void onDragStarted(float rawX, float rawY) {

            }
        });

        layout.setConfig().setScaleEnabled(false).apply();
    }
}
