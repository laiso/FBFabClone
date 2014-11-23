package ash.glay.hbfavclone.net;


import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ash.glay.hbfavclone.model.RDF;

/**
 * Created by quesera2 on 2014/11/23.
 */
public class FeedManager {

    public interface OnCompleteFeedListener{
        void onFeedReaded(RDF rdf);
    }

    final URI mUri;
    final ExecutorService mExecutor;
    final OnCompleteFeedListener mListener;

    final static int CONNECTION_TIME_OUT = 25;

    public FeedManager(Context context, String userName){
        super();

        try{
            mListener = (OnCompleteFeedListener)context;
        }catch(ClassCastException e){
            throw new IllegalArgumentException("OnCompleteFeedListenerに準拠してね。");
        }

        mExecutor = Executors.newSingleThreadExecutor();

        try{
            mUri = new URIBuilder().setScheme("http")
                    .setHost("b.hatena.ne.jp")
                    .setPath("/" + userName + "/favorite.rss")
                    .build();
        }catch(URISyntaxException e){
            throw new IllegalArgumentException("URIとして成立しない不正なユーザー名です。");
        }
    }

    public void fetchFavorite(){
        mExecutor.execute(new GetFeedConnection(mUri, mListener));
    }

    static class GetFeedConnection implements Runnable{
        final URI mUri;
        final OnCompleteFeedListener mListener;

        GetFeedConnection(URI uri, OnCompleteFeedListener listener){
            mUri = uri;
            mListener = listener;
        }

        @Override
        public void run() {
            RDF result = executeConnection();
            if(result != null){
                mListener.onFeedReaded(result);
            }
            return;
        }

        private RDF executeConnection(){
            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpClient.addRequestInterceptor(new RequestAcceptEncoding());
            httpClient.addResponseInterceptor(new ResponseContentEncoding());

            HttpParams httpParams = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIME_OUT * 1000);
            HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIME_OUT * 1000);

            HttpGet request = new HttpGet(mUri);
            RDF result = null;

            try {
                result = httpClient.execute(request, new ResponseHandler<RDF>() {
                    @Override
                    public RDF handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                        if(httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                            return null;
                        }

                        return FeedParser.parse(new InputStreamReader(httpResponse.getEntity().getContent()));
                    }
                });

            }catch (ClientProtocolException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                httpClient.getConnectionManager().shutdown();
            }

            return result;
        }
    }
}