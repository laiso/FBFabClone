package ash.glay.hbfavclone;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.File;

import ash.glay.hbfavclone.auth.StubAuthenticationService;
import ash.glay.hbfavclone.contentprovider.FeedDAO;
import ash.glay.hbfavclone.contentprovider.HBFavFeedContentProvider;
import ash.glay.hbfavclone.model.DatabaseHelper;
import ash.glay.hbfavclone.util.Constants;
import ash.glay.hbfavclone.util.FeedAdapter;
import ash.glay.hbfavclone.util.Utility;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * フィードを表示するActivity
 */
public class FeedActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    @InjectView(R.id.listView)
    ListView mListView;
    FeedAdapter mAdapter;

    final String PREF_SETUP_COMPLETE = "complete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        getLoaderManager().initLoader(0, null, this);

        // アカウント生成周り作り直す
        boolean newAccount = false;
        boolean setupComplete = getSharedPreferences("save", Context.MODE_PRIVATE).getBoolean(PREF_SETUP_COMPLETE, false);

        Account account = StubAuthenticationService.getAccount();
        AccountManager accountManager = (AccountManager) getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            ContentResolver.setIsSyncable(account, HBFavFeedContentProvider.AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, HBFavFeedContentProvider.AUTHORITY, true);
            ContentResolver.addPeriodicSync(account, HBFavFeedContentProvider.AUTHORITY, new Bundle(), 60 * 60);
            newAccount = true;
        }
        if (newAccount || !setupComplete) {
            HBFavFeedContentProvider.forceRefresh();
            getSharedPreferences("save", Context.MODE_PRIVATE).edit().putBoolean(PREF_SETUP_COMPLETE, true).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        // 即断でSyncAdapterに同期させる
        if (id == R.id.action_execute) {
            HBFavFeedContentProvider.forceRefresh();
            return true;
        }
        // データベースの内容と同期ログを削除する
        else if (id == R.id.action_remove) {
            FeedDAO.getInstance().removeAllFeeds(getContentResolver());
            getFileStreamPath(Constants.LOG_FILE_NAME).delete();
        }
        // 最近同期された時間をリスト表示する
        else if (id == R.id.action_recent) {
            Intent i = new Intent(this, RecentActivity.class);
            startActivity(i);
            return true;
        }
        // データベースの内容をSDカードにダンプ（デバッグ用）
        else if (id == R.id.action_dump) {
            File db = getDatabasePath(DatabaseHelper.DATABASE_NAME);
            File dst = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + (DatabaseHelper.DATABASE_NAME));
            Utility.copyFile(db, dst);
            return true;
        }
        // ログイン起動
        else if (id == R.id.action_login) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(),
                HBFavFeedContentProvider.CONTENT_URI,
                null,
                null,
                null,
                HBFavFeedContentProvider.DATETIME_COLUMN + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mAdapter == null) {
            mAdapter = new FeedAdapter(this, data);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
