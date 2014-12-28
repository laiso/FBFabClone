package ash.glay.hbfavclone.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * SyncAdapterを生成するサービス
 */
public class SyncService extends Service {

    /**
     * SyncAdapterを同時に生成しないためのロック（ApplicationContextじゃダメなんだろうか）
     */
    private static final Object sSyncAdapterLock = new Object();

    /**
     * SyncAdapter
     */
    private static SyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
