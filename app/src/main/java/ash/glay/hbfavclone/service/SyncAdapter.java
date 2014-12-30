package ash.glay.hbfavclone.service;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.DateFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import ash.glay.hbfavclone.auth.StubAuthenticationService;
import ash.glay.hbfavclone.contentprovider.FeedDAO;
import ash.glay.hbfavclone.contentprovider.HBFavFeedContentProvider;
import ash.glay.hbfavclone.model.FeedItem;
import ash.glay.hbfavclone.net.HBFavFeedConnection;

/**
 * SyncAdapter
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // フェッチを実行する、結果が取得できなければリターン
        List<FeedItem> result = HBFavFeedConnection.execute("quesera2");
        if (result == null) {
            syncResult.stats.numIoExceptions++;
            return;
        }

        try {
            // バルクインサートで保存
            ContentValues[] values =  FeedDAO.getInstance().convertFromFeedList(result);
            int count = provider.bulkInsert(HBFavFeedContentProvider.CONTENT_URI, values);
            syncResult.stats.numInserts += count;
        } catch (RemoteException e) {
            syncResult.databaseError = true;
            e.printStackTrace();
        }
        writeLog();
    }

    /**
     * 同期した時刻をファイルに書き出します。<br />
     * 後で統計取る用
     */
    private void writeLog() {
        final String fileName = getContext().getFilesDir() + "/log.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(fileName), true))) {
            final Date now = new Date();
            writer.println("同期時間:" + DateFormat.getLongDateFormat(getContext()).format(now) + " " + DateFormat.getTimeFormat(getContext()).format(now));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * SyncAdapterの更新を即座に実行します。
     */
    public static void forceRefresh() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(StubAuthenticationService.getAccount(), HBFavFeedContentProvider.AUTHORITY, bundle);
    }
}
