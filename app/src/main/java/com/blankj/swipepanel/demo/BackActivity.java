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
        swipePanel.setLeftEdgeSize(SizeUtils.dp2px(100));// 设置左侧触发阈值 100dp
        swipePanel.setLeftDrawable(R.drawable.base_back);// 设置左侧 icon
        swipePanel.wrapView(findViewById(R.id.rootLayout));// 设置嵌套在 rootLayout 外层
        swipePanel.setOnFullSwipeListener(new SwipePanel.OnFullSwipeListener() {// 设置完全划开松手后的监听
            @Override
            public void onFullSwipe(int direction) {
                finish();
                swipePanel.close(direction);// 关闭
            }
        });
    }
}
