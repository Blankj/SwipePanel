# SwipePanel（侧划控件）


## Preview

![layout](https://raw.githubusercontent.com/Blankj/SwipePanel/master/art/layout.png) ![back](https://raw.githubusercontent.com/Blankj/SwipePanel/master/art/back.gif)


## Download

Gradle:
```groovy
implementation 'com.blankj:swipe-panel:1.0'
```


## How to use

```java
final SwipePanel swipePanel = new SwipePanel(this);
swipePanel.setLeftEdgeSize(SizeUtils.dp2px(100));// 设置左划触发阈值 100dp
swipePanel.setLeftDrawable(R.drawable.base_back);// 设置左划 icon
swipePanel.wrapView(findViewById(R.id.rootLayout));// 设置嵌套在 rootLayout 外层
swipePanel.setOnFullSwipeListener(new SwipePanel.OnFullSwipeListener() {// 设置完全划开松手后的监听
    @Override
    public void onFullSwipe(int direction) {
        finish();
        swipePanel.close(direction);
    }
});
```


## API

|方法名                                |属性名                                 |说明|
|:---:                                |:---:                                 |:---:|
|setLeft(Top, Right, Bottom)SwipeColor|app:left(top, right, bottom)SwipeColor|设置左（上、右、下）划颜色|
|setLeft(Top, Right, Bottom)EdgeSize  |app:left(top, right, bottom)EdgeSize  |设置左（上、右、下）划触发阈值|
|setLeft(Top, Right, Bottom)Drawable  |app:left(top, right, bottom)Drawable  |设置左（上、右、下）划 icon|
|setLeft(Top, Right, Bottom)Center    |app:isLeft(Top, Right, Bottom)Center  |设置左（上、右、下）划是否居中|
|setLeft(Top, Right, Bottom)Enabled   |app:isLeft(Top, Right, Bottom)Enabled |设置左（上、右、下）划是否可用|
|wrapView                             |---                                   |设置嵌套在该 view 的外层|
|setOnFullSwipeListener               |---                                   |设置完全划开松手后的监听|
|isOpen                               |---                                   |判断是否被划开|
|close                                |---                                   |关闭|



