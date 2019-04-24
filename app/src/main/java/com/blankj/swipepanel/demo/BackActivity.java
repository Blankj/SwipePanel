package com.blankj.swipepanel.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.blankj.swipepanel.SwipePanel;
import com.blankj.utilcode.util.SizeUtils;

public class BackActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, BackActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back);

        final SwipePanel swipePanel = new SwipePanel(this);
        swipePanel.setLeftEdgeSize(SizeUtils.dp2px(100));
        swipePanel.setLeftDrawable(R.drawable.base_back);
        swipePanel.wrapView(findViewById(R.id.rootLayout));
        swipePanel.setOnFullSwipeListener(new SwipePanel.OnFullSwipeListener() {
            @Override
            public void onFullSwipe(int direction) {
                finish();
                swipePanel.close(direction);
            }
        });
    }
}
