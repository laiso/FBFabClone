package ash.glay.hbfavclone.sync;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtilsHC4;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import ash.glay.hbfavclone.model.FeedItem;
import ash.glay.hbfavclone.util.Constants;

/**
 * フィードの取得を行うクラス
 */
public class HBFavFeedConnection {
    private final static String SCHEME = "http";
    private final static String HOST = "feed.hbfav.com";

    /**
     * feed.hbfav.comへ接続します。
     *
     * @param userName ユーザー名
     * @return フィードをリスト形式で取得、接続に失敗した場合はnull
     */
    public static List<FeedItem> execute(String userName) throws IOException , URISyntaxException{
        // URLをビルド
        final URI uri = new URIBuilder().setScheme(SCHEME)
                .setHost(HOST)
                .setPath("/" + userName + "/")
                .build();

        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.addRequestInterceptor(new RequestAcceptEncoding());
        httpClient.addResponseInterceptor(new ResponseContentEncoding());

        // 本家にあわせてタイムアウトを25秒で設定。
        // ただしiOSのバックグラウンドフェッチの制約にあわせた秒数なので、特に必要はない。
        HttpParams httpParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, Constants.CONNECTION_TIME_OUT * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, Constants.CONNECTION_TIME_OUT * 1000);

        HttpGet request = new HttpGet(uri);

        try {
            return httpClient.execute(request, new ResponseHandler<List<FeedItem>>() {
                @Override
                public List<FeedItem> handleResponse(HttpResponse httpResponse) throws IOException {
                    // 200 OKでなければIOException投げる
                    if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                        throw new IOException();
                    }
                    return FeedParser.parse(EntityUtilsHC4.toString(httpResponse.getEntity()));
                }
            });
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }
}
