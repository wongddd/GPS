package com.yyt.trackcar.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class TransformImageAppearance {

    /**
     * 将Bitmap转换成圆形
     * @param bitmap
     * @return
     */
    public static Bitmap transformBitmapToRound(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int left = 0, top = 0, right = width, bottom = height;

        float roundPx = height / 2f;   //角度
        if (width > height) {
            left = (width - height) / 2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width) / 2;
            right = width;
            bottom = top + width;
            roundPx = width / 2;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);   //填充背景
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));///两图交互显示的模式  （显示相交的）
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 调整Bitmap的大小
     * @param bitmap
     * @param targetSize 目标大小
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bitmap,int targetSize){

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) targetSize)/width;
        float scaleHeight = ((float) targetSize)/height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        bitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        return bitmap;
    }

    public static Bitmap getDefaultMethodForBitmap(Bitmap bitmap){
        return resizeBitmap(transformBitmapToRound(bitmap),64);
    }
}
