package cn.guomin0999.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2016-03-21.
 */
public class FastBlurView extends RelativeLayout {

    ImageView src, blur;

    public FastBlurView(Context context) {
        super(context);
        init(context);
    }


    public FastBlurView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FastBlurView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setBitmap(Bitmap loadedImage) {
        Bitmap bitmap = imageCrop(loadedImage, getHeight(), getWidth(), true);
        src.setImageBitmap(bitmap);
        blur.setImageBitmap(FastBlurUtil.doBlur(bitmap, 10, false));
    }

    public void setBlur(float value) {
        blur.setAlpha(value);
    }

    private void init(Context context) {
        src = new ImageView(context);
        src.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        src.setScaleType(ImageView.ScaleType.CENTER_CROP);
        blur = new ImageView(context);
        blur.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        blur.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(src);
        addView(blur);
        setBlur(0);
    }

    /**
     * 按照一定的宽高比例裁剪图片
     *
     * @param bitmap
     * @param num1   长边的比例
     * @param num2   短边的比例
     * @return
     */
    public static Bitmap imageCrop(Bitmap bitmap, int num1, int num2,
                                   boolean isRecycled) {

        Log.v("view", " num1:" + num1 + " v:" + num2);
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int retX, retY;
        int nw, nh;
        if (w > h) {
            if (h > w * num2 / num1) {
                nw = w;
                nh = w * num2 / num1;
                retX = 0;
                retY = (h - nh) / 2;
            } else {
                nw = h * num1 / num2;
                nh = h;
                retX = (w - nw) / 2;
                retY = 0;
            }
        } else {
            if (w > h * num2 / num1) {
                nh = h;
                nw = h * num2 / num1;
                retY = 0;
                retX = (w - nw) / 2;
            } else {
                nh = w * num1 / num2;
                nw = w;
                retY = (h - nh) / 2;
                retX = 0;
            }
        }
//        Log.w("Me", "crop  x = " + retX + ", y = " + retY  + ", width =" + nw + ", height = " + nh);
        Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
                false);
        if (isRecycled && bitmap != null && !bitmap.equals(bmp)
                && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return bmp;// Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
        // false);
    }
}
