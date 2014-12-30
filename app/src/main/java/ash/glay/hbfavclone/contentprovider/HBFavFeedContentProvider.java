package ash.glay.hbfavclone.contentprovider;

import android.accounts.Account;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;

import java.util.HashMap;

import ash.glay.hbfavclone.model.DatabaseHelper;

import static ash.glay.hbfavclone.model.DatabaseHelper.*;

public class HBFavFeedContentProvider extends ContentProvider {

    /**
     * コンテンツタイプ
     */
    public static final String CONTENT_TYPE = "glay.ash/fbfab.feed";
    public static final int FEED = 1;

    public static final String AUTHORITY = HBFavFeedContentProvider.class.getCanonicalName();

    /**
     * コンテンツURL
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME_FEED);

    /* ------------------
      TABLEの列名を定義
     --------------------*/
    public static final String TITLE_COLUMN = "title";
    public static final String LINK_COLUMN = "link";
    public static final String FAVICON_URL_COLUMN = "favicon_url";
    public static final String COMMENT_COLUMN = "comment";
    public static final String COUNT_COLUMN = "count";
    public static final String DATETIME_COLUMN = "datetime";
    public static final String CREATE_AT_COLUMN = "create_at";
    public static final String PERMALINK_COLUMN = "permalink";
    public static final String DESCRIPTION_COLUMN = "description";
    public static final String THUMBNAIL_URL_COLUMN = "thumbnail_url";
    public static final String HASH_COLUMN = "hash";
    public static final String USER_NAME_COLUMN = "name";
    public static final String USER_PROFILE_IMAGE_URL_COLUMN = "profile_image_url";

    private DatabaseHelper mDatabaseHelper;

    public static final HashMap<String, String> projectionMap;

    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, DatabaseHelper.TABLE_NAME_FEED, FEED);

        projectionMap = new HashMap<>();
        projectionMap.put(COLUMN_ID, COLUMN_ID);
        projectionMap.put(TITLE_COLUMN, TITLE_COLUMN);
        projectionMap.put(LINK_COLUMN, LINK_COLUMN);
        projectionMap.put(FAVICON_URL_COLUMN, FAVICON_URL_COLUMN);
        projectionMap.put(COMMENT_COLUMN, COMMENT_COLUMN);
        projectionMap.put(COUNT_COLUMN, COUNT_COLUMN);
        projectionMap.put(DATETIME_COLUMN, DATETIME_COLUMN);
        projectionMap.put(CREATE_AT_COLUMN, CREATE_AT_COLUMN);
        projectionMap.put(PERMALINK_COLUMN, PERMALINK_COLUMN);
        projectionMap.put(DESCRIPTION_COLUMN, DESCRIPTION_COLUMN);
        projectionMap.put(THUMBNAIL_URL_COLUMN, THUMBNAIL_URL_COLUMN);
        projectionMap.put(USER_NAME_COLUMN, USER_NAME_COLUMN);
        projectionMap.put(USER_PROFILE_IMAGE_URL_COLUMN, USER_PROFILE_IMAGE_URL_COLUMN);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return CONTENT_TYPE;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DatabaseHelper.TABLE_NAME_FEED);
        qb.setProjectionMap(projectionMap);

        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final long rowId = db.insertWithOnConflict(uri.getPathSegments().get(0), null, values, SQLiteDatabase.CONFLICT_REPLACE);
        if (rowId > 0) {
            Uri returnUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(returnUri, null);
            return returnUri;
        } else {
            throw new IllegalArgumentException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String id = uri.getPathSegments().get(1);
        final int count = db.update(TABLE_NAME_FEED, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int count = db.delete(TABLE_NAME_FEED, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * SyncAdapterの更新を即座に実行します。
     */
    public static void forceRefresh(Account account) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(account, AUTHORITY, bundle);
    }
}
