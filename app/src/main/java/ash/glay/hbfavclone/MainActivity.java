package ash.glay.hbfavclone;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ash.glay.hbfavclone.model.Item;
import ash.glay.hbfavclone.model.RDF;
import ash.glay.hbfavclone.net.FeedManager;
import ash.glay.hbfavclone.net.FeedManager.OnCompleteFeedListener;
import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends Activity implements OnCompleteFeedListener {

    FeedManager mFeedManager;
    @InjectView(R.id.listView)
    ListView mListView;
    RDF mRdf;
    RDFAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFeedManager = new FeedManager(this, "quesera2");
        ButterKnife.inject(this);
        mAdapter = new RDFAdapter();
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_execute) {
            mFeedManager.fetchFavorite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFeedReaded(RDF rdf) {
        mRdf = rdf;
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private class RDFAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if(mRdf == null) return 0;
            return mRdf.items.size();
        }

        @Override
        public Item getItem(int position) {
            return mRdf.items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WebView contents;
            TextView title, count;

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                convertView = inflater.inflate(R.layout.cell_bookmark, parent, false);
            }
            contents = (WebView)convertView.findViewById(R.id.webView);
            title = (TextView)convertView.findViewById(R.id.title);
            count = (TextView)convertView.findViewById(R.id.count);

            Item item = getItem(position);
            contents.loadData(item.contents, "text/html; charset=UTF-8", null);
            title.setText(item.title);
            count.setText(String.valueOf(item.bookmarkcount));

            return convertView;
        }
    }
}
