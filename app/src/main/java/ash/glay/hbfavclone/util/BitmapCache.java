package ash.glay.hbfavclone.util;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * ビットマップをキャッシュするよくある実装
 * <p/>
 * <a href="http://dev.classmethod.jp/smartphone/android/android-tips-51-volley/">ネットワーク通信・キャッシュ処理をより速く、簡単に実装できるライブラリ “Volley” を使ってみた</a>
 */
public class BitmapCache implements ImageLoader.ImageCache {
    private LruCache<String, Bitmap> mCache;

    public BitmapCache() {
        int maxSize = 20 * 1024 * 1024;
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        mCache.put(url, bitmap);
    }


}
