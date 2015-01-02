package ash.glay.hbfavclone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ash.glay.hbfavclone.model.BookmarkInfo;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * ユーザーコメントを表示するListView
 */
public class UserCommentListView extends Object {

    @InjectView(R.id.list_view)
    ListView mListView;

    private static final String BOOKMARK_INFO = "bookmark-info";
    private BookmarkInfo mBookmarkInfo;

    public UserCommentListView(Context context, ViewGroup container, BookmarkInfo bookmarkInfo) {
        mBookmarkInfo = bookmarkInfo;
        View view = LayoutInflater.from(context).inflate(R.layout.listview_usercomment, container, false);
        ButterKnife.inject(this, view);
    }

    public void destroy() {
        ButterKnife.reset(this);
    }

    /**
     * リストビューを取得します<br />
     * このタイミングまでアダプタの生成を遅延
     *
     * @return
     */
    public ListView getView() {
        return mListView;
    }
}
