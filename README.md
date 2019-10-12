# SwipePanel（侧划控件）

## Background

对市面上实现的侧划返回不是很满意（仿微信，QQ 通过修改窗口透明坑太多），最终决定还是亲手写一个高实用性的吧，效果如下所示，换个图标，更多划动功能可以由你自己解锁，总共一个 600 多行代码的类，推荐通过阅读源码，你肯定会收获很多哈。


## Preview

![layout](https://raw.githubusercontent.com/Blankj/SwipePanel/master/art/layout.png) ![back](https://raw.githubusercontent.com/Blankj/SwipePanel/master/art/back.gif)


## Download

Gradle:
```groovy
implementation 'com.blankj:swipe-panel:1.2'
```


## How to use

### 动态

```java
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
```

### 静态

```xml
<com.blankj.swipepanel.SwipePanel
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/swipePanel"
        android:background="@color/mediumGray"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".LayoutSwipePanelActivity"
        app:isLeftCenter="false"
        app:leftEdgeSize="100dp"
        app:leftSwipeColor="@color/colorPrimary"
        app:leftDrawable="@drawable/base_back">

    ...

</com.blankj.swipepanel.SwipePanel>
```


## API

|方法名                                |属性名                                 |说明|
|:---:                                |:---:                                 |:---:|
|setLeft(Top, Right, Bottom)SwipeColor|app:left(top, right, bottom)SwipeColor|设置左（上、右、下）侧颜色|
|setLeft(Top, Right, Bottom)EdgeSize  |app:left(top, right, bottom)EdgeSize  |设置左（上、右、下）侧触发阈值|
|setLeft(Top, Right, Bottom)Drawable  |app:left(top, right, bottom)Drawable  |设置左（上、右、下）侧 icon|
|setLeft(Top, Right, Bottom)Center    |app:isLeft(Top, Right, Bottom)Center  |设置左（上、右、下）侧是否居中|
|setLeft(Top, Right, Bottom)Enabled   |app:isLeft(Top, Right, Bottom)Enabled |设置左（上、右、下）侧是否可用|
|wrapView                             |---                                   |设置嵌套在该 view 的外层|
|setOnFullSwipeListener               |---                                   |设置完全划开松手后的监听|
|setOnProgressChangedListener         |---                                   |设置进度变化的监听|
|isOpen                               |---                                   |判断是否被划开|
|close                                |---                                   |关闭|


## [Change Log](https://github.com/Blankj/SwipePanel/blob/master/CHANGELOG.md)


## 打个小广告

欢迎加入我的知识星球「**[基你太美](https://t.zsxq.com/FmeqfYF)**」，我会在星球中分享 [AucFrame](https://blankj.com/2019/07/22/auc-frame/) 框架、大厂面经、[AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode) 更详尽的说明...一切我所了解的知识，你可以通过支付进入我的星球「**[基你太美](https://t.zsxq.com/FmeqfYF)**」进行体验，加入后优先观看星球中精华的部分，如果觉得星球的内容对自身没有收益，你可以自行申请退款退出星球，也没必要加我好友；**如果你已确定要留在我的星球，可以通过扫描如下二维码（备注：基你太美+你的星球昵称）加我个人微信，方便我后续拉你进群(PS：进得越早价格越便宜)。**

![我的二维码](https://raw.githubusercontent.com/Blankj/AndroidUtilCode/master/art/wechat.png)
