package com.yyt.trackcar.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapBlobUtils {

    /**
     * byte[] → Bitmap
     * 
     * @param b 数据
     * @return 结果
     */
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0)
            return BitmapFactory.decodeByteArray(b, 0, b.length);
         else
            return null;
    }

    /**
     * Bitmap → byte[]
     * 
     * @param bm 图像
     * @return 结果
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * Bitmap缩放
     * 
     * @param bitmap 图像
     * @param width 宽
     * @param height 高
     * @return 结果
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * 将Drawable转化为Bitmap
     * 
     * @param drawable 图像
     * @return 结果
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Config config = drawable.getOpacity() != PixelFormat.OPAQUE
                ? Config.ARGB_8888 : Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 获得圆角图片
     *
     * @param bitmap 图像
     * @param roundPx 圆角半径
     * @return 结果
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 获得带倒影的图片
     *
     * @param bitmap 图像
     * @return 结果
     */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w, h / 2,
                matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
                Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, h, w,
                bitmapWithReflection.getHeight() + reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * Drawable缩放
     *
     * @param drawable 图像
     * @param w 宽
     * @param h 高
     * @return 结果
     */
    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // drawable转换成bitmap
        Bitmap oldbmp = drawableToBitmap(drawable);
        // 创建操作图片用的Matrix对象
        // 计算缩放比例
        float sx = ((float) w / width);
        float sy = ((float) h / height);
        // 设置缩放比例
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix,
                true);
        return new BitmapDrawable(newbmp);
    }

    /**
     * Get bitmap from specified image path
     *
     * @param imgPath 图片路径
     * @return 结果
     */
    public static Bitmap getBitmap(String imgPath) {
        // Get bitmap through image path
        File file = new File(imgPath);
        Bitmap bitmap;
        if (1024 * 1024 >= file.length()) {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = false;
            newOpts.inPurgeable = true;
            newOpts.inInputShareable = true;
            // Do not compress
            newOpts.inSampleSize = 1;
            newOpts.inPreferredConfig = Config.RGB_565;
            bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        } else {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imgPath, newOpts);
            newOpts.inJustDecodeBounds = false;
            // 传过来图片分辨率的宽度和高度
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            // 这里设置高度为800f
            // 这里设置宽度为480f
            float hh = 1920.0F;
            float ww = 1080.0F;
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            // be=1表示不缩放
            int be = 1;
            // 计算出取样率
            // 如果宽度大的话根据宽度固定大小缩放
            if ((w > h) && (w > ww))
                be = (int) Math.rint(newOpts.outWidth / ww);
            // 如果高度高的话根据宽度固定大小缩放
            else if ((w < h) && (h > hh)) {
                be = (int) Math.rint(newOpts.outHeight / hh);
            }
            if (be <= 0)
                be = 1;
            // 设置缩放比例
            newOpts.inSampleSize = be;
            // newOpts.inSampleSize = (int) (file.length() / (4 * 1024 * 1024));
            bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        }
        return bitmap;
    }

    /**
     * 旋转图像
     * @param bitmap 图像
     * @param rotate 旋转角度
     * @return 结果
     */
    private static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (bitmap == null)
            return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    /**
     * Store bitmap into specified image path
     *
     * @param bitmap 图像
     * @param outPath 输出路径
     * @throws FileNotFoundException 文件异常
     */
    public void storeImage(Bitmap bitmap, String outPath)
            throws FileNotFoundException {
        FileOutputStream os = new FileOutputStream(outPath);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
    }

    /**
     * Compress image by pixel, this will modify image width/height. Used to get
     * thumbnail
     *
     * @param imgPath
     *            image path
     * @param pixelW
     *            target pixel of width
     * @param pixelH
     *            target pixel of height
     * @return 结果
     */
    public Bitmap ratio(String imgPath, float pixelW, float pixelH) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        // pixelH设置高度为240f时，可以明显看到图片缩小了
        // pixelW设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > pixelW) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / pixelW);
        } else if (w < h && h > pixelH) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / pixelH);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
        // return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    /**
     * Compress image by size, this will modify image width/height. Used to get
     * thumbnail
     *
     * @param image 图像
     * @param pixelW
     *            target pixel of width
     * @param pixelH
     *            target pixel of height
     * @return 结果
     */
    public Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if (os.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, os);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // pixelH设置高度为240f时，可以明显看到图片缩小了
        // pixelW设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > pixelW) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / pixelW);
        } else if (w < h && h > pixelH) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / pixelH);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        // 压缩好比例大小后再进行质量压缩
        // return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    /**
     * Compress by quality, and generate image to the path specified
     *
     * @param image 图像
     * @param outPath 输出路径
     * @param maxSize
     *            target will be compressed to be smaller than this size.(kb)
     * @throws IOException 异常
     */
    public static void compressAndGenImage(Bitmap image, String outPath,
            int maxSize) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 100;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        while (os.toByteArray().length / 1024 > maxSize) {
            // Clean up os
            os.reset();
            // interval 10
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, os);
        }

        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(os.toByteArray());
        fos.flush();
        fos.close();
    }

    /**
     * Compress by quality, and generate image to the path specified
     *
     * @param imgPath 图片路径
     * @param outPath 输出路径
     * @param maxSize
     *            target will be compressed to be smaller than this size.(kb)
     * @param needsDelete
     *            Whether delete original file after compress
     * @throws IOException 异常
     */
    public static void compressAndGenImage(String imgPath, String outPath,
            int maxSize, boolean needsDelete) throws IOException {
        compressAndGenImage(getBitmap(imgPath), outPath, maxSize);

        // Delete original file
        if (needsDelete) {
            File file = new File(imgPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * Ratio and generate thumb to the path specified
     *
     * @param image 图片路径
     * @param outPath 输出路径
     * @param pixelW
     *            target pixel of width
     * @param pixelH
     *            target pixel of height
     * @throws FileNotFoundException 异常
     */
    public void ratioAndGenThumb(Bitmap image, String outPath, float pixelW,
            float pixelH) throws FileNotFoundException {
        Bitmap bitmap = ratio(image, pixelW, pixelH);
        storeImage(bitmap, outPath);
    }

    /**
     * Ratio and generate thumb to the path specified
     *
     * @param imgPath 图片路径
     * @param outPath 输出路径
     * @param pixelW
     *            target pixel of width
     * @param pixelH
     *            target pixel of height
     * @param needsDelete
     *            Whether delete original file after compress
     * @throws FileNotFoundException 异常
     */
    public void ratioAndGenThumb(String imgPath, String outPath, float pixelW,
            float pixelH, boolean needsDelete) throws FileNotFoundException {
        Bitmap bitmap = ratio(imgPath, pixelW, pixelH);
        storeImage(bitmap, outPath);

        // Delete original file
        if (needsDelete) {
            File file = new File(imgPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 保存图像
     * @param bm 图像
     * @param fileName 文件名
     * @param path 文件路径
     * @throws IOException 异常
     */
    public static void saveFile(Bitmap bm, String fileName, String path)
            throws IOException {
        File foder = new File(path);
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File mCaptureFile = new File(path, fileName);
        if (!mCaptureFile.exists()) {
            mCaptureFile.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(mCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    /**
     * 绘制成微信聊天效果
     *
     * @param bitmapimg 图像
     * @param direct 方向
     * @return 结果
     */
    public static Bitmap canvasTriangle(Bitmap bitmapimg, int direct) {
        Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(),
                bitmapimg.getHeight(), Config.ARGB_4444);
        Canvas canvas = new Canvas(output);
        // 设置默认背景颜色
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmapimg.getWidth(),
                bitmapimg.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // 右边
        if (direct == 0) {
            RectF oval = new RectF(0, 0,
                    bitmapimg.getWidth() - ScreenUtils.dp2px(10),
                    bitmapimg.getHeight());// 设置个新的长方形
            canvas.drawRoundRect(oval, ScreenUtils.dp2px(8),
                    ScreenUtils.dp2px(8), paint);
            Path path = new Path();
            path.moveTo(bitmapimg.getWidth() - ScreenUtils.dp2px(10),
                    ScreenUtils.dp2px(24));
            path.lineTo(bitmapimg.getWidth(), ScreenUtils.dp2px(32));
            path.lineTo(bitmapimg.getWidth() - ScreenUtils.dp2px(10),
                    ScreenUtils.dp2px(40));
            path.lineTo(bitmapimg.getWidth() - ScreenUtils.dp2px(10),
                    ScreenUtils.dp2px(24));
            canvas.drawPath(path, paint);
        }
        // 左边
        if (direct == 1) {
            RectF oval = new RectF(ScreenUtils.dp2px(10), 0,
                    bitmapimg.getWidth(), bitmapimg.getHeight());// 设置个新的长方形
            canvas.drawRoundRect(oval, ScreenUtils.dp2px(8),
                    ScreenUtils.dp2px(8), paint);
            Path path = new Path();
            path.moveTo(ScreenUtils.dp2px(10), ScreenUtils.dp2px(24));
            path.lineTo(0, ScreenUtils.dp2px(32));
            path.lineTo(ScreenUtils.dp2px(10), ScreenUtils.dp2px(40));
            path.lineTo(ScreenUtils.dp2px(10), ScreenUtils.dp2px(24));
            canvas.drawPath(path, paint);
        }
        // 两层绘制交集。显示上层
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmapimg, rect, rect, paint);
        return output;
    }

    /**
     * view转换bitmap
     * @param view 转换的view
     * @return bitmap
     */
    public static Bitmap convertViewToBitmap(View view){
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View
                .MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    /**
     * //将Bitmap转换成字符串
     * @param bitmap 需要转换的bitmap
     * @return 返回的String
     */
    public static String bitmapToString(Bitmap bitmap) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

}
