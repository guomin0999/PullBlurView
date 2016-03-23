# PullBlurView

拉动列表图片会放大并且模糊

![PullBlurView](https://raw.githubusercontent.com/guomin0999/PullBlurView/master/pullblur.gif)

##使用方法
0.引用

maven { url "https://jitpack.io" }

compile 'com.github.guomin0999:PullBlurView:0.1'

1.使用ZoomImageRecyclerView替换RecyclerView

    <cn.guomin0999.library.ZoomImageRecyclerView
        android:id="@+id/recycler"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
        
2.设置列表第一项为可放大的View

    recyclerView.setZoomView(xxx);

3.设置图片

    <cn.guomin0999.library.FastBlurView
        android:id="@+id/blurView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    blurView.setBitmap(loadedImage);
    
4.设置拉动时改变模糊数值

    recyclerView.setOnPullinglistener(new ZoomImageRecyclerView.OnPullinglistener() {
        public void onPulling(float value) {
            blurView.setBlur(value);
        }
    });


    
