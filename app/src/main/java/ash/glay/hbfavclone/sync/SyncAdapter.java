package ash.glay.hbfavclone.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.DateFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

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
        try {
            List<FeedItem> result = HBFavFeedConnection.execute(account.name);
            ContentValues[] values = FeedDAO.getInstance().convertFromFeedList(result);
            int count = 0;
            for (ContentValues value : values) {
                if (provider.insert(HBFavFeedContentProvider.CONTENT_URI, value) != null) {
                    count++;
                }
            }
            writeLog(String.format("同期成功%d件", count));

            // 更新0件時に通知を行う
            if (count == 0) {
                getContext().getContentResolver().notifyChange(HBFavFeedContentProvider.CONTENT_URI, null);
            }

        } catch (IOException e) {
            syncResult.stats.numIoExceptions++;
            writeLog(String.format("同期失敗"));
            return;
        } catch (URISyntaxException e) {
            syncResult.stats.numAuthExceptions++;
            writeLog(String.format("ユーザー名不正"));
            return;
        } catch (RemoteException e) {
            writeLog(String.format("DB挿入失敗"));
            syncResult.databaseError = true;
        }
    }

    /**
     * 同期した時刻をファイルに書き出します。<br />
     * 後で統計取る用
     */
    private void writeLog(String status) {
        final String fileName = getContext().getFilesDir() + "/log.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(fileName), true))) {
            final Date now = new Date();
            writer.println(status + " : " + DateFormat.getLongDateFormat(getContext()).format(now) + " " + DateFormat.getTimeFormat(getContext()).format(now));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
