package ash.glay.hbfavclone;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Applicationクラス<br/>
 * Volleyのリクエストキューとアカウントを管理
 */
public class Application extends android.app.Application {

    private static RequestQueue mQueue;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public synchronized RequestQueue getRequestQueue() {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }
        return mQueue;
    }
}
