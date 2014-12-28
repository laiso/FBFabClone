package ash.glay.hbfavclone.service;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import ash.glay.hbfavclone.auth.StubAuthenticator;
import ash.glay.hbfavclone.constant.Constants;

/**
 * Authenticatorを提供するサービス
 */
public class StubAuthenticationService extends Service {

    private StubAuthenticator mStubAuth;

    public static Account getAccount() {
        return new Account("sync", Constants.ACCOUNT_TYPE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mStubAuth = new StubAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mStubAuth.getIBinder();
    }
}
