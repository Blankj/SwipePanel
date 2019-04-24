# SwipePanel（侧划控件）


## Preview

![layout](https://raw.githubusercontent.com/Blankj/SwipePanel/master/art/layout.png) ![back](https://raw.githubusercontent.com/Blankj/SwipePanel/master/art/back.gif)


## Download

Gradle:
```groovy
implementation 'com.blankj:swipe-panel:1.0'
```


## How to use

### 动态

```java
final SwipePanel swipePanel = new SwipePanel(this);
swipePanel.setLeftEdgeSize(SizeUtils.dp2px(100));// 设置左划触发阈值 100dp
swipePanel.setLeftDrawable(R.drawable.base_back);// 设置左划 icon
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
        app:leftDrawable="@drawable/base_back"
        app:isTopCenter="true"
        app:topEdgeSize="100dp"
        app:topSwipeColor="@color/colorAccent"
        app:topDrawable="@mipmap/ic_launcher_round"
        app:isRightCenter="false"
        app:rightEdgeSize="100dp"
        app:rightSwipeColor="@color/colorPrimary"
        app:rightDrawable="@drawable/base_back"
        app:isBottomCenter="true"
        app:bottomEdgeSize="100dp"
        app:bottomSwipeColor="@color/colorAccent"
        app:bottomDrawable="@mipmap/ic_launcher_round">

    ...

</com.blankj.swipepanel.SwipePanel>
```


## API

|方法名                                |属性名                                 |说明|
|:---:                                |:---:                                 |:---:|
|setLeft(Top, Right, Bottom)SwipeColor|app:left(top, right, bottom)SwipeColor|设置左（上、右、下）测颜色|
|setLeft(Top, Right, Bottom)EdgeSize  |app:left(top, right, bottom)EdgeSize  |设置左（上、右、下）测触发阈值|
|setLeft(Top, Right, Bottom)Drawable  |app:left(top, right, bottom)Drawable  |设置左（上、右、下）测 icon|
|setLeft(Top, Right, Bottom)Center    |app:isLeft(Top, Right, Bottom)Center  |设置左（上、右、下）测是否居中|
|setLeft(Top, Right, Bottom)Enabled   |app:isLeft(Top, Right, Bottom)Enabled |设置左（上、右、下）测是否可用|
|wrapView                             |---                                   |设置嵌套在该 view 的外层|
|setOnFullSwipeListener               |---                                   |设置完全划开松手后的监听|
|isOpen                               |---                                   |判断是否被划开|
|close                                |---                                   |关闭|


## [Change Log](https://github.com/Blankj/SwipePanel/blob/master/CHANGELOG.md)
