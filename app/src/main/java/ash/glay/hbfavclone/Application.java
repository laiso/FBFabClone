package ash.glay.hbfavclone;

import android.accounts.Account;
import android.accounts.AccountManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import ash.glay.hbfavclone.util.BitmapCache;
import ash.glay.hbfavclone.util.Constants;

/**
 * Applicationクラス<br/>
 * Volleyのリクエストキューとアカウントを管理
 */
public class Application extends android.app.Application {

    private static RequestQueue mQueue;
    private static BitmapCache mBitmapCache;
    private Account mCurrentUser;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public synchronized BitmapCache getBitmapCache() {
        if (mBitmapCache == null) {
            mBitmapCache = new BitmapCache();
        }
        return mBitmapCache;
    }

    public synchronized RequestQueue getRequestQueue() {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }
        return mQueue;
    }

    /**
     * ユーザーを取得します
     *
     * @return
     */
    public Account getUser() {
        if (mCurrentUser == null) {
            Account[] account = AccountManager.get(this).getAccountsByType(Constants.ACCOUNT_TYPE);
            if (account.length == 0) {
                return null;
            }
            mCurrentUser = account[0];
        }
        return mCurrentUser;
    }
}
