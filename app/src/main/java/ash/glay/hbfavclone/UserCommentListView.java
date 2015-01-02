package ash.glay.hbfavclone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

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

    RecyclerView mRecyclerView;

    final private ImageLoader mImageLoader;
    final private BitmapCache mBitmapCache;
    final private Context mContext;

    private BookmarkInfo mBookmarkInfo;
    private UserbookmarkAdapter mAdapter;

    public UserCommentListView(Context context, BookmarkInfo bookmarkInfo) {
        mBookmarkInfo = bookmarkInfo;
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(context).inflate(R.layout.listview_usercomment, null);
        mRecyclerView = ButterKnife.findById(view, R.id.recyler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        mContext = context.getApplicationContext();
        RequestQueue queue = ((Application) context.getApplicationContext()).getRequestQueue();
        mBitmapCache = ((Application) context.getApplicationContext()).getBitmapCache();
        mImageLoader = new ImageLoader(queue, mBitmapCache);
    }

    public void destroy() {
        mBookmarkInfo = null;
        mRecyclerView.setAdapter(null);
        mRecyclerView = null;
    }

    /**
     * リストビューを取得します<br />
     * このタイミングまでアダプタの生成を遅延
     *
     * @return
     */
    public View getView() {
        if (mAdapter == null) {
            mAdapter = new UserbookmarkAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
        return mRecyclerView;
    }

    /**
     * ビューホルダー
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.userImage)
        ImageView userImage;
        @InjectView(R.id.userName)
        TextView userName;
        @InjectView(R.id.comment)
        TextView comment;
        @InjectView(R.id.when)
        TextView when;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    /**
     * RecyclerViewのアダプタ
     */
    class UserbookmarkAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_bookmark_commented, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return mBookmarkInfo.getCommentedUserList().size();
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            CommentedUser user = mBookmarkInfo.getCommentedUserList().get(position);
            holder.userName.setText(user.getUser());
            holder.comment.setText(user.getComment());
            holder.when.setText(Utility.getTimeString(mContext, user.timestamp));

            if (holder.userImage.getTag() != null) {
                ImageLoader.ImageContainer imageContainer = (ImageLoader.ImageContainer) holder.userImage.getTag();
                imageContainer.cancelRequest();
            }

            ImageLoader.ImageListener userImageListener = new UserImageListener(holder.userImage, mBitmapCache);
            holder.userImage.setTag(mImageLoader.get(urlFromUserName(user.getUser()), userImageListener));
        }

        private String urlFromUserName(String userName) {
            return "http://www.st-hatena.com/users/" + userName.substring(0, 2) + "/" + userName + "/profile_l.gif";
        }
    }
}
