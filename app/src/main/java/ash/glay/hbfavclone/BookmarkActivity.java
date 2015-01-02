package ash.glay.hbfavclone;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import ash.glay.hbfavclone.model.BookmarkInfo;
import ash.glay.hbfavclone.model.CommentedUser;
import ash.glay.hbfavclone.net.HBCountRequest;
import ash.glay.hbfavclone.util.Constants;
import ash.glay.hbfavclone.util.Utility;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class BookmarkActivity extends Activity implements ObservableScrollViewCallbacks {

    @InjectView(R.id.webView)
    ObservableWebView mWebView;
    ProgressBar mProgressBar;

    ActionBar mActionBar;

    ImageView mFavicon;
    TextView mPageTitle;

    @InjectView(R.id.action_previous)
    Button mPreviousButton;
    @InjectView(R.id.action_reload)
    Button mReloadButton;
    @InjectView(R.id.action_users)
    Button mUsersButton;
    @InjectView(R.id.user_counts)
    TextView mUserCounts;

    private Animation HIDE_ANIMATION;
    private Animation SHOW_ANIMATION;

    private JsonObjectRequest mHBCountRequest;
    private RequestQueue mQueue;

    private BookmarkInfo mBookmarkInfo;
    private UserCommentListView mCommentListView;

    /**
     * 情報取得時のローカルキャストレシーバ処理
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_RECEIVE_BOOKMARK_INFO)) {
                mBookmarkInfo = (BookmarkInfo) intent.getSerializableExtra("data");

                // ブコメのあるコメント件数取得
                int comment = 0;
                for (CommentedUser user : mBookmarkInfo.commentedUserList) {
                    if (user.comment.length() != 0) {
                        comment++;
                    }
                }

                // コメント件数が1件でもあればボタン活性化
                if (comment > 0) {
                    mUsersButton.setEnabled(true);
                    mUserCounts.setVisibility(View.VISIBLE);
                    mUserCounts.setText("" + comment);
                    mUserCounts.setElevation(10.f);
                    mCommentListView = new UserCommentListView(context, mBookmarkInfo);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_bookmark);

        mActionBar = getActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setCustomView(R.layout.custom_webview_header);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        ButterKnife.inject(this);
        mFavicon = ButterKnife.findById(mActionBar.getCustomView(), R.id.favicon);
        mPageTitle = ButterKnife.findById(mActionBar.getCustomView(), R.id.pageTitle);

        initializeAnimations();

        final String initialUrl = getIntent().getStringExtra(Constants.BUNDLE_KEY_URL);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.VISIBLE);
                mPreviousButton.setEnabled(view.canGoBack());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
                mPageTitle.setText(view.getTitle());
                if (view.getFavicon() != null) {
                    mFavicon.setImageBitmap(view.getFavicon());
                }
                mPreviousButton.setEnabled(view.canGoBack());
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
        mWebView.loadUrl(initialUrl);

        mPreviousButton.setEnabled(false);
        mUsersButton.setEnabled(false);
        mUserCounts.setVisibility(View.INVISIBLE);

        mQueue = ((Application) getApplication()).getRequestQueue();
        requestBookmark(initialUrl);

        IntentFilter filter = new IntentFilter(Constants.ACTION_RECEIVE_BOOKMARK_INFO);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        mHBCountRequest.cancel();
        if (mCommentListView != null) {
            mCommentListView.destroy();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        //WebViewの解放
        ViewGroup parent = (ViewGroup) mWebView.getParent();
        parent.removeView(mWebView);
        mWebView.stopLoading();
        mWebView.setWebChromeClient(null);
        mWebView.setWebViewClient(null);
        mWebView.destroy();
        mWebView = null;
        super.onDestroy();
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
        HIDE_ANIMATION.setStartOffset(50);
        HIDE_ANIMATION.setDuration(200);
        HIDE_ANIMATION.setInterpolator(new AccelerateInterpolator());
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

    /**
     * ブックマーク数を取得します
     *
     * @param url
     */
    private void requestBookmark(String url) {
        // クエリ実行
        mHBCountRequest = HBCountRequest.getHBCountRequest(BookmarkActivity.this, url);
        if (mHBCountRequest != null) {
            mQueue.add(mHBCountRequest);
            mQueue.start();
        }
    }

    @OnClick(R.id.action_reload)
    public void onActionReload() {
        mWebView.reload();
    }


    @OnClick(R.id.action_previous)
    public void onActionPrevious() {
        mWebView.goBack();
    }

    @OnClick(R.id.action_users)
    public synchronized void onActionUsers() {
        if (mUsersButton.isSelected()) {
            mUsersButton.setSelected(false);
            hideUserCommentList();

        } else {
            mUsersButton.setSelected(true);
            showUserCommentList();
        }
    }

    private void showUserCommentList() {
        onUpOrCancelMotionEvent(ScrollState.UP);

        final FrameLayout container = ButterKnife.findById(this, R.id.container);
        final LinearLayout toolBar = ButterKnife.findById(this, R.id.tool_bar);
        final View listView = mCommentListView.getView();
        listView.setAlpha(0.f);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.topMargin = 0;
        params.bottomMargin = toolBar.getHeight();
        listView.setLayoutParams(params);
        container.addView(listView);
        ((View) toolBar.getParent()).bringToFront();

        // リストのアニメーション
        PropertyValuesHolder holderAlpha = PropertyValuesHolder.ofFloat("alpha", 0.f, 1.f);
        PropertyValuesHolder holderTranslate = PropertyValuesHolder.ofFloat("translationY", container.getHeight() - toolBar.getHeight(), 0);
        ObjectAnimator listAnimator = ObjectAnimator.ofPropertyValuesHolder(listView, holderAlpha, holderTranslate);

        // コメント数のアニメーション
        PropertyValuesHolder commentAlpha = PropertyValuesHolder.ofFloat("alpha", 1.f, 0.f);
        PropertyValuesHolder hideCountX = PropertyValuesHolder.ofFloat("scaleX", 1.f, 0.4f);
        PropertyValuesHolder hideCountY = PropertyValuesHolder.ofFloat("scaleY", 1.f, 0.4f);
        ObjectAnimator commentAnimation = ObjectAnimator.ofPropertyValuesHolder(mUserCounts, commentAlpha, hideCountX, hideCountY);

        // ツールバーの色アニメーション
        TransitionDrawable toolbarBG = (TransitionDrawable) toolBar.getBackground();
        toolbarBG.startTransition(300);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(listAnimator, commentAnimation);
        set.setDuration(300);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

        mPreviousButton.setEnabled(false);
        mReloadButton.setEnabled(false);
    }

    private void hideUserCommentList() {
        onUpOrCancelMotionEvent(ScrollState.DOWN);

        final FrameLayout container = ButterKnife.findById(this, R.id.container);
        final View listView = mCommentListView.getView();

        // リストのアニメーション
        PropertyValuesHolder holderAlpha = PropertyValuesHolder.ofFloat("alpha", 1.f, 0.f);
        PropertyValuesHolder holderTranslate = PropertyValuesHolder.ofFloat("translationY", 0, container.getHeight());
        ObjectAnimator listAnimator = ObjectAnimator.ofPropertyValuesHolder(listView, holderAlpha, holderTranslate);

        // コメント数のアニメーション
        PropertyValuesHolder commentAlpha = PropertyValuesHolder.ofFloat("alpha", 0.f, 1.f);
        PropertyValuesHolder hideCountX = PropertyValuesHolder.ofFloat("scaleX", 0.4f, 1.0f);
        PropertyValuesHolder hideCountY = PropertyValuesHolder.ofFloat("scaleY", 0.4f, 1.0f);
        ObjectAnimator commentAnimation = ObjectAnimator.ofPropertyValuesHolder(mUserCounts, commentAlpha, hideCountX, hideCountY);

        // ツールバーの色アニメーション
        final LinearLayout toolBar = ButterKnife.findById(this, R.id.tool_bar);
        TransitionDrawable toolbarBG = (TransitionDrawable) toolBar.getBackground();
        toolbarBG.reverseTransition(200);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(listAnimator, commentAnimation);
        set.setDuration(200);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                container.removeView(listView);
                mPreviousButton.setEnabled(mWebView.canGoBack());
                mReloadButton.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }
}
