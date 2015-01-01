package ash.glay.hbfavclone.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * ユーティリティメソッドを定義するクラス
 */
public class Utility {

    private Utility() {
    }

    /**
     * ファイルをコピーします
     *
     * @param src
     * @param dst
     */
    public static void copyFile(File src, File dst) {

        try (FileInputStream inStream = new FileInputStream(src);
             FileOutputStream outStream = new FileOutputStream(dst)) {
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * テーマが設定しているActionBarの高さを取得します
     *
     * @param context
     * @return
     */
    public static int getActionBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
    }

    /**
     * KitKat以降のAndroidで透過Gifを扱えない問題のワークアラウンド<br />
     * <a href="http://stackoverflow.com/questions/20056050/transparent-gif-in-android-imageview">Transparent GIF in Android ImageView</a>
     *
     * @param src
     * @param color
     * @return
     */
    public static Bitmap eraseBG(Bitmap src, int color) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap b = src.copy(Bitmap.Config.ARGB_8888, true);
        b.setHasAlpha(true);

        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < width * height; i++) {
            if (pixels[i] == color) {
                pixels[i] = 0;
            }
        }

        b.setPixels(pixels, 0, width, 0, 0, width, height);

        return b;
    }

    /**
     * Bitmapを円に切り抜きます
     *
     * @param src
     * @return
     */
    public static Bitmap clipCircle(Bitmap src) {
        final int size = Math.max(src.getWidth(), src.getHeight());
        final float scale = (float) size / Math.min(src.getWidth(), src.getHeight());
        Bitmap dst = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dst);
        canvas.drawARGB(0, 0, 0, 0);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xff424242);
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        canvas.drawBitmap(src, matrix, paint);
        return dst;
    }

    /**
     * ユーザーアイコンを円で切り抜いて返します
     *
     * @param cache      キャッシュ
     * @param requestUrl ユーザーアイコンのリクエストURL
     * @return
     */
    public static Bitmap getClippedUszerIcon(BitmapCache cache, String requestUrl) {
        final String convertedUrl = requestUrl + "/converted";
        if (cache.getBitmap(convertedUrl) != null) {
            return cache.getBitmap(convertedUrl);
        }

        Bitmap src = cache.getBitmap(requestUrl);
        src = eraseBG(src, -1);         // use for white background
        src = eraseBG(src, -16777216);  // use for black background
        Bitmap dst = Utility.clipCircle(src);
        cache.putBitmap(convertedUrl, dst);

        return dst;
    }
}
