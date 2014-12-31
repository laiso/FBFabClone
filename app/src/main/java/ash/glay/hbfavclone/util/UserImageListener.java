package ash.glay.hbfavclone.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

/**
 * ユーザー画像を表示するImageLoaderListener
 */
public class UserImageListener implements ImageListener {

    private final ImageView mImageView;
    private final BitmapCache mCache;

    public UserImageListener(ImageView view, BitmapCache cache) {
        mImageView = view;
        mCache = cache;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mImageView.setImageResource(android.R.color.transparent);
    }

    @Override
    public void onResponse(ImageContainer response, boolean isImmediate) {
        if (response.getBitmap() != null) {
            // リクエスト成功時はキャッシュから取り出した画像を円で切り抜いてセット
            mCache.putBitmap(response.getRequestUrl(), response.getBitmap());
            mImageView.setImageBitmap(clipCircle(response.getRequestUrl()));
        } else {
            mImageView.setImageResource(android.R.color.transparent);
        }
    }

    Bitmap clipCircle(String requestUrl) {
        final String convertedUrl = requestUrl + "/converted";
        if (mCache.getBitmap(convertedUrl) != null) {
            return mCache.getBitmap(convertedUrl);
        }

        Bitmap src = mCache.getBitmap(requestUrl);
        src = Utility.eraseBG(src, -1);         // use for white background
        src = Utility.eraseBG(src, -16777216);  // use for black background
        Bitmap dst = Utility.clipCircle(src);
        mCache.putBitmap(convertedUrl, dst);

        return dst;
    }
}
