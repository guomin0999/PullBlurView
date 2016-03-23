package cn.guomin0999.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * ImageView拉动放大RecyclerView
 * Created by xzh on 2016/2/18 0018.
 */
public class ZoomImageListView extends ListView {

    /**
     * 动画还原速度,越小速度越快
     */
    private final int SPEED_ZOOM = 10;
    /**
     * 拉动除数，越大拉动距离越小
     */
    private final int DRAG_GRAVITY = 8;
    /**
     * 允许拉动最大距离
     */
    private final int ALLOW_DRAG = 300;

    private View mZoomView;
    private ViewGroup.LayoutParams mLayoutParams;
    private float mDownY = -1;
    private int mHeight, mDownHeight;
    private boolean isTouch;
    private boolean canDrag;

    public ZoomImageListView(Context context) {
        super(context);
        init();
    }

    public ZoomImageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public void setZoomView(View zoomView) {
        mZoomView = zoomView;
    }

    public void setOnPullingListener(OnPullingListener listener) {
        this.listener = listener;
    }

    private OnPullingListener listener;

    public interface OnPullingListener {
        void onPulling(float value);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mZoomView == null)
            return super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //每次滑动初始化参数
                if (mDownY == -1) {
                    mDownY = event.getRawY();
                    mDownHeight = mZoomView.getHeight();
                    isTouch = true;
                    canDrag = getChildCount() == 0;
                    int firstPosition = getFirstVisiblePosition();

                    canDrag = firstPosition == 0 && mZoomView.getTop() == 0;
                    if (mLayoutParams == null) {
                        mHeight = mZoomView.getHeight();
                        mLayoutParams = mZoomView.getLayoutParams();
                    }
                }
                if (!canDrag) //按下时不可拖动
                    break;
                //新的高度
                float newHeight = mDownHeight + (event.getRawY() - mDownY) / DRAG_GRAVITY;
                if (newHeight > mZoomView.getHeight()) { //新的高度大于当前高度,下拉
                    if (mZoomView.getHeight() - mHeight < ALLOW_DRAG) //没有超过允许拖动距离
                        setZoomHeight((int) newHeight);
                    return true;
                } else if (newHeight < mZoomView.getHeight()) { //新的高度小于当前高度上拉
                    if (mZoomView.getHeight() == mHeight) { //高度一致了，不需要上拉
                        break;
                    } else if (newHeight > mHeight) { //未弹回去
                        setZoomHeight((int) newHeight);
                        return true;
                    } else if (newHeight < mHeight) { //弹超了
                        setZoomHeight(mHeight);
                        scrollTo(0, 0);
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                isTouch = false;
                mDownY = -1;
                zoom();
                break;
        }
        return super.onTouchEvent(event);
    }

    private float mZoomDistance;

    private void zoom() {
        float curHeight = mZoomView.getHeight();
        if (curHeight == mHeight) //初始距离
            return;
        //计算每毫秒缩放的距离
        mZoomDistance = (curHeight - mHeight) / SPEED_ZOOM;
        post(mZoomRunnable);
    }

    private Runnable mZoomRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTouch)
                return;
            int newHeight = (int) (mZoomView.getHeight() - mZoomDistance);
            if (newHeight <= mHeight) //高度超出，还原默认高度
                setZoomHeight(mHeight);
            else
                setZoomHeight(newHeight);
            if (mZoomView.getHeight() != mHeight)
                postDelayed(this, 1);
        }
    };

    private void setZoomHeight(int height) {
        if (mLayoutParams == null)
            return;
        mLayoutParams.height = height;
        mZoomView.requestLayout();
        if (listener != null) {
            float value = (float) (height - mHeight) / (ALLOW_DRAG / 3);
            listener.onPulling(value);
        }
    }

}
