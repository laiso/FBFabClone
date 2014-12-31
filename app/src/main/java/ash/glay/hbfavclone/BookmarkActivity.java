package ash.glay.hbfavclone;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.TypedValue;
import android.view.MenuItem;
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
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import ash.glay.hbfavclone.util.Constants;
import butterknife.ButterKnife;
import butterknife.InjectView;


public class BookmarkActivity extends Activity implements ObservableScrollViewCallbacks {

    @InjectView(R.id.webView)
    ObservableWebView mWebView;

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
        mWebView.setWebViewClient(new WebViewClient());
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
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollViewCallbacks(this);
        mWebView.loadUrl(url);
    }

    private void initializeAnimations() {
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        final int ACTIONBAR_HEIGHT = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
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
                params.topMargin = ACTIONBAR_HEIGHT - (int)(ACTIONBAR_HEIGHT * interpolatedTime);
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
