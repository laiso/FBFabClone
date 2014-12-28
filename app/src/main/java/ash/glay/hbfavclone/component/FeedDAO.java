package ash.glay.hbfavclone.component;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import ash.glay.hbfavclone.contentprovider.HBFavFeedContentProvider;
import ash.glay.hbfavclone.model.FeedItem;

/**
 * FeedのデータベースへアクセスするためのSingletonクラス
 */
public class FeedDAO {
    private static final FeedDAO sInstance = new FeedDAO();

    private FeedDAO() {
    }

    /**
     * インスタンスを取得します
     *
     * @return
     */
    public static FeedDAO getInstance() {
        return sInstance;
    }

    /**
     * ContentProviderへフィードを追加します<br />
     * バルクインサート実装に伴って廃止
     *
     * @param contentResolver
     * @param feed            挿入するフィードアイテム
     */
    @Deprecated
    public void addFeed(ContentResolver contentResolver, FeedItem feed) {
        ContentValues cv = convertFeedItem(feed);
        contentResolver.insert(HBFavFeedContentProvider.CONTENT_URI, cv);
    }

    /**
     * フィードアイテムのリストをContentValueの配列へ変換します
     *
     * @param feeds
     * @return
     */
    public ContentValues[] convertFromFeedList(List<FeedItem> feeds) {
        ContentValues[] result = new ContentValues[feeds.size()];
        int i = 0;
        for (FeedItem feed : feeds) {
            result[i] = convertFeedItem(feed);
            i++;
        }
        return result;
    }

    /**
     * フィードを全て消し去ります。<br />
     * デバッグ用です
     *
     * @param contentResolver
     */
    public void removeAllFeeds(ContentResolver contentResolver) {
        contentResolver.delete(HBFavFeedContentProvider.CONTENT_URI, null, null);
    }

    /**
     * カーソル位置にあるフィードアイテムオブジェクトを返します
     *
     * @param cursor
     * @return
     */
    public FeedItem feedFromCursor(Cursor cursor) {
        FeedItem feed = new FeedItem();

        try {
            feed.title = cursor.getString(cursor.getColumnIndex(HBFavFeedContentProvider.TITLE_COLUMN));
            feed.link = new URI(cursor.getString(cursor.getColumnIndex(HBFavFeedContentProvider.LINK_COLUMN)));
            feed.favicon_url = new URI(cursor.getString(cursor.getColumnIndex(HBFavFeedContentProvider.FAVICON_URL_COLUMN)));
            feed.comment = cursor.getString(cursor.getColumnIndex(HBFavFeedContentProvider.COMMENT_COLUMN));
            feed.count = cursor.getInt(cursor.getColumnIndex(HBFavFeedContentProvider.COUNT_COLUMN));
            feed.datetime = new Date(cursor.getInt(cursor.getColumnIndex(HBFavFeedContentProvider.DATETIME_COLUMN)));
            feed.created_at = cursor.getString(cursor.getColumnIndex(HBFavFeedContentProvider.CREATE_AT_COLUMN));
            feed.permalink = new URI(cursor.getString(cursor.getColumnIndex(HBFavFeedContentProvider.PERMALINK_COLUMN)));
            feed.description = cursor.getString(cursor.getColumnIndex(HBFavFeedContentProvider.DESCRIPTION_COLUMN));
            final String thumbnailUrl = cursor.getString(cursor.getColumnIndex(HBFavFeedContentProvider.THUMBNAIL_URL_COLUMN));
            if (!TextUtils.isEmpty(thumbnailUrl)) {
                feed.thumbnail_url = new URI(thumbnailUrl);
            }
            final String userName = cursor.getString(cursor.getColumnIndex(HBFavFeedContentProvider.USER_NAME_COLUMN));
            final URI userImage = new URI(cursor.getString(cursor.getColumnIndex(HBFavFeedContentProvider.USER_PROFILE_IMAGE_URL_COLUMN)));
            feed.user = new FeedItem.User(userName, userImage);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return feed;
    }

    /**
     * TimeLineFeedをContentValuesに変換します
     *
     * @param feed
     * @return
     */
    private ContentValues convertFeedItem(FeedItem feed) {
        ContentValues cv = new ContentValues();
        cv.put(HBFavFeedContentProvider.TITLE_COLUMN, feed.title);
        cv.put(HBFavFeedContentProvider.LINK_COLUMN, feed.link.toString());
        cv.put(HBFavFeedContentProvider.FAVICON_URL_COLUMN, feed.favicon_url.toString());
        cv.put(HBFavFeedContentProvider.COMMENT_COLUMN, feed.comment);
        cv.put(HBFavFeedContentProvider.COUNT_COLUMN, feed.count);
        cv.put(HBFavFeedContentProvider.DATETIME_COLUMN, feed.datetime.getTime());
        cv.put(HBFavFeedContentProvider.CREATE_AT_COLUMN, feed.created_at);
        cv.put(HBFavFeedContentProvider.PERMALINK_COLUMN, feed.permalink.toString());
        cv.put(HBFavFeedContentProvider.DESCRIPTION_COLUMN, feed.description);
        if (feed.thumbnail_url != null) {
            cv.put(HBFavFeedContentProvider.THUMBNAIL_URL_COLUMN, feed.thumbnail_url.toString());
        }
        cv.put(HBFavFeedContentProvider.USER_NAME_COLUMN, feed.user.name);
        cv.put(HBFavFeedContentProvider.USER_PROFILE_IMAGE_URL_COLUMN, feed.user.profile_image_url.toString());
        cv.put(HBFavFeedContentProvider.HASH_COLUMN, feed.hashCode());
        return cv;
    }
}
