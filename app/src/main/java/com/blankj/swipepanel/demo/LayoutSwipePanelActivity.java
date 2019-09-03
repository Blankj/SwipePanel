package com.blankj.swipepanel.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blankj.swipepanel.SwipePanel;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

public class LayoutSwipePanelActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, LayoutSwipePanelActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_swipe_panel);

        final SwipePanel swipePanel = findViewById(R.id.swipePanel);
        swipePanel.setOnFullSwipeListener(new SwipePanel.OnFullSwipeListener() {
            @Override
            public void onFullSwipe(int direction) {
                ToastUtils.showLong(direction);
                if (direction == SwipePanel.TOP) {
                    swipePanel.close(true);
                }
            }
        });
        swipePanel.setOnProgressChangedListener(new SwipePanel.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int direction, float progress, boolean isTouch) {
                if (direction == SwipePanel.TOP) {
                    LogUtils.e(progress);
                    RotateDrawable drawable = (RotateDrawable) swipePanel.getTopDrawable();
                    drawable.setLevel((int) (progress * 20000));
                }
            }
        });
    }
}
