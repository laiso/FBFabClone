package ash.glay.hbfavclone.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import ash.glay.hbfavclone.Application;
import ash.glay.hbfavclone.R;
import ash.glay.hbfavclone.model.BookmarkInfo;
import ash.glay.hbfavclone.model.CommentedUser;
import ash.glay.hbfavclone.util.BitmapCache;
import ash.glay.hbfavclone.util.UserImageListener;
import ash.glay.hbfavclone.util.Utility;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * ユーザーコメントを表示するListView
 */
public class UserCommentListView extends Object {

    View mRootView;
    @InjectView(R.id.recycleView)
    RecyclerView mRecyclerView;

    final private Context mContext;
    final private ImageLoader mImageLoader;
    final private BitmapCache mBitmapCache;

    private BookmarkInfo mBookmarkInfo;
    private UserbookmarkAdapter mAdapter;

    public UserCommentListView(Context context, BookmarkInfo bookmarkInfo) {
        mBookmarkInfo = bookmarkInfo;
        mRootView = LayoutInflater.from(context).inflate(R.layout.bookmarks_list_xml, null);
        ButterKnife.inject(this, mRootView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        mContext = context.getApplicationContext();

        mBitmapCache = ((Application) mContext.getApplicationContext()).getBitmapCache();
        RequestQueue queue = ((Application) mContext.getApplicationContext()).getRequestQueue();
        mImageLoader = new ImageLoader(queue, mBitmapCache);
    }

    public void destroy() {
        mBookmarkInfo = null;
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }
        ButterKnife.reset(this);
    }

    /**
     * リストビューを取得します<br />
     * このタイミングまでアダプタの生成を遅延
     *
     * @return
     */
    public View getView() {
        if (mAdapter == null) {
            mAdapter = new UserbookmarkAdapter(mBookmarkInfo);
            mRecyclerView.setAdapter(mAdapter);
        }
        return mRootView;
    }

    /**
     * ビューホルダー
     */
    static class CommentediewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.userImage)
        ImageView userImage;
        @InjectView(R.id.userName)
        TextView userName;
        @InjectView(R.id.comment)
        TextView comment;
        @InjectView(R.id.when)
        TextView when;

        public CommentediewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    /**
     * ビューホルダー（コメントなし）
     */
    class NoCommentedViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.icon_view)
        RecyclerView userImage;

        public NoCommentedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            userImage.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        }
    }

    /**
     * ビューホルダー（ヘッダ）
     */
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.header)
        TextView header;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    /**
     * ビューホルダー（コメントなしネスト）
     */
    static class NestedNoCommentedViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.userImage)
        ImageView userImage;

        public NestedNoCommentedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    /**
     * RecyclerViewのアダプタ
     */
    class UserbookmarkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        final private BookmarkInfo mBookmarkInfo;

        private static final int HEADER = 0;
        private static final int COMMENTED_USER = 1;
        private static final int NOCOMMENT_USER = 2;

        UserbookmarkAdapter(BookmarkInfo info) {
            mBookmarkInfo = info;
        }

        @Override
        public int getItemCount() {
            return mBookmarkInfo.getHasCommentUsers().size() + 3;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mBookmarkInfo.getHasCommentUsers().size() + 1) {
                Log.i("debug", "position:" + position + "はヘッダ");
                return HEADER;
            } else if (position > 0 && position <= mBookmarkInfo.getHasCommentUsers().size() + 1) {
                Log.i("debug", "position:" + position + "はブクマコメ");
                return COMMENTED_USER;
            } else {
                Log.i("debug", "position:" + position + "はコメなし");
                return NOCOMMENT_USER;
            }
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            switch (viewType) {
                case HEADER:
                    return new HeaderViewHolder(inflater.inflate(R.layout.cell_usercomment_header, viewGroup, false));
                case COMMENTED_USER:
                    return new CommentediewHolder(inflater.inflate(R.layout.cell_bookmark_commented, viewGroup, false));
                case NOCOMMENT_USER:
                    return new NoCommentedViewHolder(inflater.inflate(R.layout.cell_bookmark_no_commented, viewGroup, false));
            }

            throw new IllegalArgumentException("ありえない状態");
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            // コメントありユーザー
            if (getItemViewType(position) == COMMENTED_USER) {
                CommentedUser user = mBookmarkInfo.getHasCommentUsers().get(position - 1);
                CommentediewHolder userHolder = (CommentediewHolder) holder;
                userHolder.userName.setText(user.getUser());
                userHolder.comment.setText(user.getComment());
                userHolder.when.setText(Utility.getTimeString(mContext, user.timestamp));

                if (userHolder.userImage.getTag() != null) {
                    ImageLoader.ImageContainer imageContainer = (ImageLoader.ImageContainer) userHolder.userImage.getTag();
                    imageContainer.cancelRequest();
                }
                ImageLoader.ImageListener userImageListener = new UserImageListener(userHolder.userImage, mBitmapCache);
                userHolder.userImage.setTag(mImageLoader.get(urlFromUserName(user.getUser()), userImageListener));
            }
            // コメントなしユーザー
            else if (getItemViewType(position) == NOCOMMENT_USER) {
                NoCommentedViewHolder userHolder = (NoCommentedViewHolder) holder;
                userHolder.userImage.setAdapter(new NestedUserbookmarkAdapter());
            }
            // ヘッダ
            else if (getItemViewType(position) == HEADER) {
                HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
                if (position == 0) {
                    headerHolder.header.setText(mBookmarkInfo.getHasCommentUsers().size()
                            + " " + mContext.getResources().getString(R.string.commented_users));
                } else {
                    headerHolder.header.setText(mContext.getResources().getString(R.string.another_users));
                }
            }
        }

        class NestedUserbookmarkAdapter extends RecyclerView.Adapter<NestedNoCommentedViewHolder> {
            @Override
            public NestedNoCommentedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new NestedNoCommentedViewHolder(inflater.inflate(R.layout.cell_bookmark_no_commented_inner, parent, false));
            }

            @Override
            public void onBindViewHolder(NestedNoCommentedViewHolder holder, int position) {
                CommentedUser user = mBookmarkInfo.getNotCommentUsers().get(position);
                if (holder.userImage.getTag() != null) {
                    ImageLoader.ImageContainer imageContainer = (ImageLoader.ImageContainer) holder.userImage.getTag();
                    imageContainer.cancelRequest();
                }
                ImageLoader.ImageListener userImageListener = new UserImageListener(holder.userImage, mBitmapCache);
                holder.userImage.setTag(mImageLoader.get(urlFromUserName(user.getUser()), userImageListener));
            }

            @Override
            public int getItemCount() {
                return mBookmarkInfo.getNotCommentUsers().size();
            }
        }
    }

    /**
     * ユーザーアイコンを取得します
     *
     * @param userName
     * @return
     */
    private static String urlFromUserName(String userName) {
        return "http://www.st-hatena.com/users/" + userName.substring(0, 2) + "/" + userName + "/profile_l.gif";
    }
}
