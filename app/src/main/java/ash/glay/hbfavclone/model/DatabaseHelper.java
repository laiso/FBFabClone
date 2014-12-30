package ash.glay.hbfavclone.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static ash.glay.hbfavclone.contentprovider.HBFavFeedContentProvider.*;

/**
 * データベースヘルパ
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "feed.db";
    public static final int DATABASE_VERSION = 3;

    public static final String TABLE_NAME_FEED = "feed";

    public static final String COLUMN_ID = "_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS  " + TABLE_NAME_FEED + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TITLE_COLUMN + " TEXT," +
                LINK_COLUMN + " TEXT," +
                FAVICON_URL_COLUMN + " TEXT," +
                COMMENT_COLUMN + " TEXT," +
                COUNT_COLUMN + " INTEGER," +
                DATETIME_COLUMN + " INTEGER," +
                CREATE_AT_COLUMN + " TEXT," +
                USER_NAME_COLUMN + " TEXT," +
                USER_PROFILE_IMAGE_URL_COLUMN + " TEXT," +
                PERMALINK_COLUMN + " TEXT," +
                DESCRIPTION_COLUMN + " TEXT," +
                THUMBNAIL_URL_COLUMN + " TEXT," +
                HASH_COLUMN + " INTEGER UNIQUE);");    // FeedItemのハッシュで一意性を担保
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // どうせ短命データなのでマイグレーションはしない
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FEED);
        }
        onCreate(db);
    }
}
