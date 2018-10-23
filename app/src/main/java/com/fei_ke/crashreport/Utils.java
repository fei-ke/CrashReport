package com.fei_ke.crashreport;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Utils {
    public static String getExceptionDetail(Throwable t) {
        if (t == null) return "";

        StringBuilder err = new StringBuilder();
        err.append(t.toString());
        err.append("\n");

        StackTraceElement[] stack = t.getStackTrace();
        if (stack != null) {
            for (StackTraceElement aStack : stack) {
                err.append("\tat ");
                err.append(aStack.toString());
                err.append("\n");
            }

        }
        Throwable cause = t.getCause();
        if (cause != null) {
            err.append("Caused by: ");
            String causeString = getExceptionDetail(cause);
            err.append(causeString);
        }
        return err.toString();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            // Single color bitmap will be created of 1x1 pixel
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
