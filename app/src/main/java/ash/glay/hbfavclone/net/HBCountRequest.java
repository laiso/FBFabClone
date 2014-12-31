package ash.glay.hbfavclone.net;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ash.glay.hbfavclone.model.BookmarkInfo;
import ash.glay.hbfavclone.model.CommentedUser;
import ash.glay.hbfavclone.util.Constants;


/**
 * はてなブックマークエントリー情報取得APIより、<br />
 * /entry/jsonlite/ APIを叩いて結果を受け取るJSON Requestを生成
 */
public class HBCountRequest {

    final static private SimpleDateFormat sSdf = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss");

    public static JsonObjectRequest getHBCountRequest(final Context context, String requestUrl) {
        final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        final String jsonLiteRequestUrl;
        try {
            jsonLiteRequestUrl = "http://b.hatena.ne.jp/entry/jsonlite/?url=" + URLEncoder.encode(requestUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        return new JsonObjectRequest(jsonLiteRequestUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String url = response.getString("url");
                    int count = response.getInt("count");
                    JSONArray bookmarks = response.getJSONArray("bookmarks");
                    List<CommentedUser> commentedUserList = new ArrayList<>(bookmarks.length());
                    for (int i = 0; i < bookmarks.length(); i++) {
                        JSONObject aUser = bookmarks.getJSONObject(i);
                        try {
                            commentedUserList.add(new CommentedUser(
                                    aUser.getString("user"),
                                    sSdf.parse(aUser.getString("timestamp")),
                                    aUser.getString("comment")
                            ));
                        } catch (ParseException e) {
                            return;
                        }
                    }
                    BookmarkInfo info = new BookmarkInfo(url, count, commentedUserList);
                    Intent intent = new Intent(Constants.ACTION_RECEIVE_BOOKMARK_INFO);
                    intent.putExtra("data", info);
                    localBroadcastManager.sendBroadcast(intent);
                } catch (JSONException e) {
                    return;
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        return;
                    }
                }
        );
    }
}
