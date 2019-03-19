package com.dada.marsframework.utils;

/**
 * Created by laidayuan on 2018/3/23.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import junit.framework.Assert;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Build.VERSION;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.TypedValue;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class BitmapUtils {

    /*
    内容	             原图	                 Luban	        Wechat
    截屏 720P	     720*1280,390k	         720*1280,87k	720*1280,56k
    截屏 1080P	     1080*1920,2.21M	     1080*1920,104k	1080*1920,112k
    拍照 13M(4:3)	 3096*4128,3.12M	     1548*2064,141k	1548*2064,147k
    拍照 9.6M(16:9)	 4128*2322,4.64M	     1032*581,97k	1032*581,74k
    滚动截屏	         1080*6433,1.56M	     1080*6433,351k	1080*6433,482k
    */
    public static void loadImage(Context ctx, File file, OnCompressListener listener) {
        Luban.get(ctx)
                .load(file)                     //传人要压缩的图片
                .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
                .setCompressListener(listener).launch();

    }

    public static void loadImage(Context ctx, String filePath, OnCompressListener listener) {
        if (!StringUtils.isNotEmpty(filePath)) {
            return;
        }

        loadImage(ctx, new File(filePath), listener);
    }

    public static Bitmap getRoundBitmap(Bitmap bitmap, int roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        Paint paint  = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int x = bitmap.getWidth();

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;

    }

    public static Bitmap createRoundConerImage(Bitmap source, int radius) {
        if (source == null) {
            return null;
        }

        try {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            Bitmap target = Bitmap.createBitmap(source.getWidth(),
                    source.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(target);
            RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rect, radius, radius, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(source, 0, 0, paint);
            return target;
        } catch (java.lang.OutOfMemoryError oom) {

        }

        return source;
    }

    public static Bitmap downSizeBitmap(Bitmap bitmap, int reqSize) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) reqSize) / width;
        float scaleHeight = ((float) reqSize) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;

		/*
		 * if(bitmap.getWidth() < reqSize) { return bitmap; } else { return
		 * Bitmap.createScaledBitmap(bitmap, reqSize, reqSize, false); }
		 */
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static byte[] convertBitmapToBytes(Bitmap bitmap) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                // TODO 此处会出现oom,先try住
                ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
                bitmap.copyPixelsToBuffer(buffer);
                return buffer.array();
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int readImageDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return degree;
        }

        return degree;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }

        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        if (null != bitmap) {
            bitmap.recycle();
        }

        return bmp;
    }

    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {

            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    public static Bitmap getViewBitmapEx(View v) {
        v.setDrawingCacheEnabled(true);
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {

            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        v.setDrawingCacheEnabled(false);

        return bitmap;
    }

    public static String adjustRotateImage(String filePath) {
        String reString = filePath;
        if (filePath.contains(".jpeg") || filePath.contains(".jpg")
                || filePath.contains(".JPEG") || filePath.contains(".JPG")) {
            int degree = readImageDegree(filePath);
            // LogUtils.e("degree = " + degree);
            if ((degree % 360) != 0) {
                reString = resetRotateBitmap(filePath, degree);
            }
        }

        return reString;
    }

    private static int IMAGE_MAX_WIDTH = 720;// 960;
    private static int IMAGE_MAX_HEIGHT = 1080;// 1280;

    public static String resetRotateBitmap(String dfullName, int degree) {
        // Logger.e("myutils", "loadBitmapByPath()----dfullName = " + dfullName
        // + ", url = " + url);
        String newPath = null;
        if (dfullName == null) {
            return newPath;
        }

        Bitmap retBmp = null;
        int nTry = 0;
        FileInputStream fis = null;
        FileDescriptor fd = null;
        try {
            fis = new FileInputStream(dfullName);
            fd = fis.getFD();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // BitmapFactory.decodeFile(imgFile, options);
            BitmapFactory.decodeFileDescriptor(fd, null, options);

            int w = options.outWidth;
            int h = options.outHeight;
            options.inSampleSize = 1;

            if (w > IMAGE_MAX_WIDTH || h > IMAGE_MAX_HEIGHT) {
                int rw = w / IMAGE_MAX_WIDTH;
                int rh = h / IMAGE_MAX_HEIGHT;
                options.inSampleSize = rw > rh ? rw : rh;
                options.inSampleSize += 1;
            }

            do {

                try {
                    // 这里一定要将其设置回false，因为之前我们将其设置成了true
                    // 设置inJustDecodeBounds为true后，decodeFile并不分配空间，即，BitmapFactory解码出来的Bitmap为Null,但可计算出原始图片的长度和宽度
                    options.inJustDecodeBounds = false;

                    retBmp = BitmapFactory.decodeFile(dfullName, options);

                    if (retBmp != null) {
                        break;
                    }
                } catch (OutOfMemoryError err) {
                    LogUtils.e("", "resetRotateBitmap OOM err = " + err);

                    options.inSampleSize -= 1;
                }
            } while (nTry++ < 2);

        } catch (Exception e) {
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                fis = null;
            }

            if (retBmp != null) {
                retBmp = rotateBitmap(retBmp, degree);
                if (retBmp != null) {
                    // String fileName = FileUtils.getFileName(dfullName);
                    if (FileUtils.saveBitmapToDisk(retBmp, dfullName)) {
                        newPath = dfullName;// getCachePath(fileName);
                    }

                    retBmp.recycle();
                    retBmp = null;
                }

            }
        }

        return newPath;
    }

    private static final int DEFAULT_DENSITY = 240;
    private static final float SCALE_FACTOR = 0.75f;
    private static final Bitmap.Config DEFAULT_BITMAP_CONFIG = Bitmap.Config.RGB_565;

    private static BitmapFactory.Options getBitmapOptions(Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inPreferredConfig = DEFAULT_BITMAP_CONFIG;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = false;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            Field field = null;
            try {
                field = BitmapFactory.Options.class
                        .getDeclaredField("inNativeAlloc");
                field.setAccessible(true);
                field.setBoolean(options, true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        int displayDensityDpi = context.getResources().getDisplayMetrics().densityDpi;
        float displayDensity = context.getResources().getDisplayMetrics().density;
        if (displayDensityDpi > DEFAULT_DENSITY && displayDensity > 1.5f) {
            int density = (int) (displayDensityDpi * SCALE_FACTOR);
            options.inDensity = density;
            options.inTargetDensity = density;
        }
        return options;
    }

    public static Bitmap decodeBitmap(Context context, int resId) {
        // checkParam(context);
        return BitmapFactory.decodeResource(context.getResources(), resId,
                getBitmapOptions(context));
    }

    public static Bitmap decodeBitmap(Context context, String pathName) {
        // checkParam(context);
        return BitmapFactory.decodeFile(pathName, getBitmapOptions(context));
    }

    public static Bitmap decodeBitmap(Context context, InputStream is) {
        // checkParam(context);
        // checkParam(is);
        return BitmapFactory.decodeStream(is, null, getBitmapOptions(context));
    }

    public static Bitmap compressBitmap(Context context, int resId,
                                        int maxWidth, int maxHeight) {
        // checkParam(context);
        final TypedValue value = new TypedValue();
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(resId, value);
            return compressBitmap(context, is, maxWidth, maxHeight);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Bitmap compressBitmap(Context context, String pathName,
                                        int maxWidth, int maxHeight) {
        // checkParam(context);
        InputStream is = null;
        try {
            is = new FileInputStream(pathName);
            return compressBitmap(context, is, maxWidth, maxHeight);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Bitmap compressBitmap(Context context, Bitmap bmp,
                                        int maxWidth, int maxHeight) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        // int options = 100;
        // while (baos.toByteArray().length / 1024 > 100) { //
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        // baos.reset();// 重置baos即清空baos
        // bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);//
        // 这里压缩options%，把压缩后的数据存放到baos中
        // options -= 10;// 每次都减少10
        // }
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        try {

            return compressBitmap(context, is, maxWidth, maxHeight);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Bitmap compressBitmap(Context context, InputStream is,
                                        int maxWidth, int maxHeight) {
        // checkParam(context);
        // checkParam(is);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, opt);
        int height = opt.outHeight;
        int width = opt.outWidth;
        int sampleSize = computeSampleSize(width, height, maxWidth, maxHeight);
        BitmapFactory.Options options = getBitmapOptions(context);
        options.inSampleSize = sampleSize;
        return BitmapFactory.decodeStream(is, null, options);
    }

    private static int computeSampleSize(int width, int height, int maxWidth,
                                         int maxHeight) {
        int inSampleSize = 1;
        if (height > maxHeight || width > maxWidth) {
            final int heightRate = Math.round((float) height
                    / (float) maxHeight);
            final int widthRate = Math.round((float) width / (float) maxWidth);
            inSampleSize = heightRate < widthRate ? heightRate : widthRate;
        }
        if (inSampleSize % 2 != 0) {
            inSampleSize -= 1;
        }
        return inSampleSize <= 1 ? 1 : inSampleSize;
    }

    public static void gaussianBlur(Context ctx, Bitmap bkg, View view) {
        if (ctx == null || bkg == null || view == null) {
            return;
        }

        long startMs = System.currentTimeMillis();
        float radius = 2;
        float scaleFactor = 8;

        Bitmap overlay = Bitmap.createBitmap(
                (int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop()
                / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        overlay = doBlur(overlay, (int) radius, true);
        view.setBackground(new BitmapDrawable(ctx.getResources(), overlay));
        // statusText.setText("cost " + (System.currentTimeMillis() - startMs) +
        // "ms");
    }

    public static Bitmap doBlur(Bitmap sentBitmap, int radius,
                                boolean canReuseInBitmap) {

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    public static Bitmap blurView(View view, int radius) {

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bitmap = view.getDrawingCache();

        float scaleFactor = 8;//
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap overlay = Bitmap.createBitmap((int) (width / scaleFactor),
                (int) (height / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        overlay = BitmapUtils.doBlur(overlay, (int) radius, true);

        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);

        return overlay;
    }

    public static Bitmap blurActivity(Activity activity, int radius) {

        return blurView(activity.getWindow().getDecorView(), radius);
    }

	/*
	 * BitmapDrawable bd = new BitmapDrawable(activity.getResources(), bitmap);
	 * bd.setAlpha(mAlpha); if (mFilterColor != -1) {
	 * bd.setColorFilter(mFilterColor, PorterDuff.Mode.DST_ATOP); }
	 */

    public static Bitmap blurActivityEx(Activity activity, int radius) {

        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bitmap = view.getDrawingCache();

        float scaleFactor = 8;//
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap overlay = Bitmap.createBitmap((int) (width / scaleFactor),
                (int) (height / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        overlay = blurBitmap(activity, overlay, (int) radius);

        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);

        return overlay;
    }

    @SuppressLint("NewApi")
    public static Bitmap blurBitmap(Context context, Bitmap sentBitmap,
                                    int radius) {

        Bitmap bitmap = Bitmap.createScaledBitmap(sentBitmap,
                sentBitmap.getWidth() / 2, sentBitmap.getHeight() / 2, false);

        if (VERSION.SDK_INT > 16) {
            final RenderScript rs = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(rs, bitmap,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs,
                    input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs,
                    Element.U8_4(rs));
            script.setRadius(radius);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);

            // After finishing everything, we destroy the Renderscript.
            rs.destroy();

            return bitmap;
        }

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    public static Bitmap resizeImage(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 不加载bitmap到内存中
        BitmapFactory.decodeFile(path, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 1;

        if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
            int sampleSize = (outWidth / width + outHeight / height) / 2;
            options.inSampleSize = sampleSize;
        }

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    public static boolean inNativeAllocAccessError = false;
    public static void setInNativeAlloc(BitmapFactory.Options options) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && !inNativeAllocAccessError) {
            try {
                BitmapFactory.Options.class.getField("inNativeAlloc")
                        .setBoolean(options, true);
                return;
            } catch (Exception e) {
                inNativeAllocAccessError = true;
            }
        }
    }

    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Bitmap extractThumbNail(final String path, final int width,
                                          final int height, final boolean crop) {
        Assert.assertTrue(path != null && !path.equals("") && height > 0
                && width > 0);

        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(path, options);
            if (tmp != null) {
                tmp.recycle();
                tmp = null;
            }

            LogUtils.d("extractThumbNail: round=" + width + "x" + height
                    + ", crop=" + crop);
            final double beY = options.outHeight * 1.0 / height;
            final double beX = options.outWidth * 1.0 / width;
            LogUtils.d("extractThumbNail: extract beX = " + beX
                    + ", beY = " + beY);
            options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY)
                    : (beY < beX ? beX : beY));
            if (options.inSampleSize <= 1) {
                options.inSampleSize = 1;
            }

            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++;
            }

            int newHeight = height;
            int newWidth = width;
            if (crop) {
                if (beY > beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            } else {
                if (beY < beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            }

            options.inJustDecodeBounds = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                options.inMutable = true;
            }
            LogUtils.i("bitmap required size=" + newWidth + "x" + newHeight
                    + ", orig=" + options.outWidth + "x" + options.outHeight
                    + ", sample=" + options.inSampleSize);
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            setInNativeAlloc(options);
            if (bm == null) {
                LogUtils.e("bitmap decode failed");
                return null;
            }

            LogUtils.i("bitmap decoded size=" + bm.getWidth() + "x"
                    + bm.getHeight());
            final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth,
                    newHeight, true);
            if (scale != null) {
                bm.recycle();
                bm = scale;
            }

            if (crop) {
                final Bitmap cropped = Bitmap.createBitmap(bm,
                        (bm.getWidth() - width) >> 1,
                        (bm.getHeight() - height) >> 1, width, height);
                if (cropped == null) {
                    return bm;
                }

                bm.recycle();
                bm = cropped;
                LogUtils.i("bitmap croped size=" + bm.getWidth() + "x"
                        + bm.getHeight());
            }
            return bm;

        } catch (final OutOfMemoryError e) {
            LogUtils.e("decode bitmap failed: " + e.getMessage());
            options = null;
        }

        return null;
    }

    public static Bitmap degreeBitmap(Bitmap src, float degree) {
        if (degree == 0.0F) {
            return src;
        }
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(degree, src.getWidth() / 2, src.getHeight() / 2);
        Bitmap resultBitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
        boolean filter = true;
        if (resultBitmap == null) {
            LogUtils.e("resultBmp is null: ");
            filter = true;
        } else {
            filter = false;
        }

        if (resultBitmap != src) {
            src.recycle();
        }
        LogUtils.d("filter: " + filter + "  degree:" + degree);

        return resultBitmap;
    }

    public static Bitmap getSuitableBitmap(String filePath) {
        if (!StringUtils.isNotEmpty(filePath)) {
            LogUtils.e("filepath is null or nil");
            return null;
        }

        if (!new File(filePath).exists()) {
            LogUtils.e("getSuitableBmp fail, file does not exist, filePath = "
                    + filePath);
            return null;
        }
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap decodeFile = BitmapFactory.decodeFile(filePath, options);
            if (decodeFile != null) {
                decodeFile.recycle();
            }

            if ((options.outWidth <= 0) || (options.outHeight <= 0)) {
                LogUtils.e("", "get bitmap fail, file is not a image file = "
                        + filePath);
                return null;
            }

            int maxWidth = 960;
            int width = 0;
            int height = 0;
            if ((options.outWidth <= options.outHeight * 2.0D)
                    && options.outWidth > 480) {
                height = maxWidth;
                width = options.outWidth;
            }
            if ((options.outHeight <= options.outWidth * 2.0D)
                    || options.outHeight <= 480) {
                width = maxWidth;
                height = options.outHeight;
            }

            Bitmap bitmap = extractThumbNail(filePath, width, height, false);
            if (bitmap == null) {
                LogUtils.e("",
                        "getSuitableBmp fail, temBmp is null, filePath = "
                                + filePath);
                return null;
            }
            int degree = readImageDegree(filePath);
            if (degree != 0) {
                bitmap = degreeBitmap(bitmap, degree);
            }
            return bitmap;
        } catch (Exception e) {
            LogUtils.e("decode bitmap err: " + e.getMessage());
            return null;
        }
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);

        return baos.toByteArray();
    }
}
