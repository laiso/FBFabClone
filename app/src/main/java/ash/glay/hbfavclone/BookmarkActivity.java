package ash.glay.hbfavclone;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ash.glay.hbfavclone.util.Constants;
import butterknife.ButterKnife;
import butterknife.InjectView;


public class BookmarkActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra(Constants.BUNDLE_KEY_URL);
        setContentView(R.layout.activity_bookmark);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, BrowserFragment.newInstance(url))
                    .commit();
        }
    }

    public static class BrowserFragment extends Fragment {

        @InjectView(R.id.webView)
        WebView mWebView;

        public static BrowserFragment newInstance(String url) {
            BrowserFragment newInstance = new BrowserFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BUNDLE_KEY_URL, url);
            newInstance.setArguments(bundle);
            return newInstance;
        }

        public BrowserFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_bookmark_body, container, false);
            ButterKnife.inject(this, rootView);
            final String url = getArguments().getString(Constants.BUNDLE_KEY_URL);
            mWebView.setWebViewClient(new WebViewClient());
            mWebView.loadUrl(url);
            return rootView;
        }

        @Override
        public void onDestroyView() {
            ButterKnife.reset(this);
            super.onDestroyView();
        }
    }
}
