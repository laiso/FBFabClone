package ash.glay.hbfavclone.sync;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ash.glay.hbfavclone.model.FeedItem;

/**
 * FeedParser<br />
 * HBFavのフィードをパースする
 */
public class FeedParser {
    /**
     * HBFabの日付を変換するためのSimpleDateFormat
     */
    final static SimpleDateFormat sDf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static List<FeedItem> parse(String input) {

        final List<FeedItem> feeds = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(input);
            JSONArray bookmarks = root.getJSONArray("bookmarks");
            JSONObject bookmark;
            FeedItem feed;
            for (int i = 0, length = bookmarks.length(); i < length; i++) {
                bookmark = bookmarks.getJSONObject(i);
                feed = new FeedItem();
                feed.title = bookmark.getString("title");
                feed.link = URI.create(bookmark.getString("link"));
                feed.favicon_url = URI.create(bookmark.getString("favicon_url"));
                feed.comment = bookmark.getString("comment");
                feed.count = bookmark.getInt("count");
                feed.datetime = sDf.parse(bookmark.getString("datetime"));
                feed.created_at = bookmark.getString("created_at");
                JSONObject user = bookmark.getJSONObject("user");
                feed.user = new FeedItem.User(
                        user.getString("name"),
                        URI.create(user.getString("profile_image_url"))
                );
                feed.permalink = URI.create(bookmark.getString("permalink"));
                feed.description = bookmark.getString("description");
                feed.thumbnail_url = bookmark.has("thumbnail_url") ? URI.create(bookmark.getString("thumbnail_url")) : null;
                feeds.add(feed);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        return feeds;
    }
}
