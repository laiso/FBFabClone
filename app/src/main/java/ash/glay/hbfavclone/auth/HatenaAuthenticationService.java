package ash.glay.hbfavclone.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Authenticatorを提供するサービス
 */
public class HatenaAuthenticationService extends Service {

    private HatenaAuthenticator mStubAuth;

    @Override
    public void onCreate() {
        super.onCreate();
        mStubAuth = new HatenaAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mStubAuth.getIBinder();
    }
}
