package ash.glay.hbfavclone;

import android.accounts.Account;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;

import ash.glay.hbfavclone.contentprovider.FeedDAO;
import ash.glay.hbfavclone.contentprovider.HBFavFeedContentProvider;
import ash.glay.hbfavclone.model.DatabaseHelper;
import ash.glay.hbfavclone.model.FeedItem;
import ash.glay.hbfavclone.util.Constants;
import ash.glay.hbfavclone.util.FeedAdapter;
import ash.glay.hbfavclone.util.Utility;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * フィードを表示するActivity
 */
public class FeedActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.content)
    ListView mListView;
    @InjectView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    FeedAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        getLoaderManager().initLoader(0, null, this);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(R.color.primary, R.color.primary_dark, R.color.accent);

        initAccount();
    }

    private void initAccount() {
        Account account = ((Application) getApplication()).getUser();
        if (account == null) {
            // ユーザーがいないのでログインする必要あり
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, Constants.REQUEST_LOGIN);
        } else {
            ContentResolver.setIsSyncable(account, HBFavFeedContentProvider.AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, HBFavFeedContentProvider.AUTHORITY, true);
            ContentResolver.addPeriodicSync(account, HBFavFeedContentProvider.AUTHORITY, new Bundle(), 60 * 60);
            executeManualSync();
        }
    }

    @SuppressWarnings("unused")
    @OnItemClick(R.id.content)
    void itemClick(AdapterView<?> adapter, View view, int pos, long id) {
        Cursor cursor = (Cursor) mListView.getItemAtPosition(pos);
        FeedItem item = FeedDAO.getInstance().feedFromCursor(cursor);

        Intent intent = new Intent(this, BookmarkActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_URL, item.getLink().toString());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            executeForceRefresh();
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
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_LOGIN && resultCode != RESULT_OK) {
            finish();
        } else if ((requestCode == Constants.REQUEST_LOGIN)) {
            initAccount();
        }
    }

    /**
     * 手動での同期を実行<br />
     * ただしEXPEDITEDフラグはtrueにしない
     */
    private void executeManualSync() {
        Account account = ((Application) getApplication()).getUser();
        if (account == null) {
            return;
        }

        mSwipeRefresh.setRefreshing(true);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, HBFavFeedContentProvider.AUTHORITY, bundle);
    }

    /**
     * 強制的に同期を実行
     */
    private void executeForceRefresh() {
        mSwipeRefresh.setRefreshing(true);
        HBFavFeedContentProvider.forceRefresh(((Application) getApplication()).getUser());
    }

    @Override
    public void onRefresh() {
        executeForceRefresh();
    }
}
