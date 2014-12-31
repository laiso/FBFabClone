package ash.glay.hbfavclone;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import ash.glay.hbfavclone.util.Constants;
import ash.glay.hbfavclone.util.Utility;
import butterknife.ButterKnife;
import butterknife.InjectView;


public class BookmarkActivity extends Activity implements ObservableScrollViewCallbacks {

    @InjectView(R.id.webView)
    ObservableWebView mWebView;
    ProgressBar mProgressBar;

    ActionBar mActionBar;

    ImageView mFavicon;
    TextView mPageTitle;

    private Animation HIDE_ANIMATION;
    private Animation SHOW_ANIMATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        mActionBar = getActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setCustomView(R.layout.custom_webview_header);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        ButterKnife.inject(this);
        mFavicon = ButterKnife.findById(mActionBar.getCustomView(), R.id.favicon);
        mPageTitle = ButterKnife.findById(mActionBar.getCustomView(), R.id.pageTitle);

        initializeAnimations();

        String url = getIntent().getStringExtra(Constants.BUNDLE_KEY_URL);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
                mPageTitle.setText(view.getTitle());
                if (view.getFavicon() != null) {
                    mFavicon.setImageBitmap(view.getFavicon());
                }
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                mFavicon.setImageBitmap(icon);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mPageTitle.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                mProgressBar.setProgress(progress);
                if (progress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
        // Lollipopのバグのために独自にプログレスバーを設定
        ViewGroup.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.setMax(100);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addView(mProgressBar, lp);
        mWebView.setScrollViewCallbacks(this);
        mWebView.loadUrl(url);
    }

    private void initializeAnimations() {
        final int ACTIONBAR_HEIGHT = Utility.getActionBarHeight(this);
        SHOW_ANIMATION = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                LayoutParams params = (FrameLayout.LayoutParams) mWebView.getLayoutParams();
                params.topMargin = (int) (ACTIONBAR_HEIGHT * interpolatedTime);
                mWebView.setLayoutParams(params);
            }
        };
        SHOW_ANIMATION.setDuration(200);
        SHOW_ANIMATION.setInterpolator(new DecelerateInterpolator());

        HIDE_ANIMATION = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                LayoutParams params = (FrameLayout.LayoutParams) mWebView.getLayoutParams();
                params.topMargin = ACTIONBAR_HEIGHT - (int) (ACTIONBAR_HEIGHT * interpolatedTime);
                mWebView.setLayoutParams(params);
            }
        };
        HIDE_ANIMATION.setDuration(200);
        HIDE_ANIMATION.setInterpolator(new DecelerateInterpolator());
    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b2) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.UP) {
            if (mActionBar.isShowing()) {
                mActionBar.hide();
                mWebView.startAnimation(HIDE_ANIMATION);
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!mActionBar.isShowing()) {
                mActionBar.show();
                mWebView.startAnimation(SHOW_ANIMATION);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
