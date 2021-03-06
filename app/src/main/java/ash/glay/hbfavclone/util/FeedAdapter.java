package ash.glay.hbfavclone.util;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import ash.glay.hbfavclone.Application;
import ash.glay.hbfavclone.R;
import ash.glay.hbfavclone.contentprovider.FeedDAO;
import ash.glay.hbfavclone.model.FeedItem;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Feedを表示するリストアダプタ
 */
public class FeedAdapter extends CursorAdapter {

    final private ImageLoader mImageLoader;
    final private BitmapCache mBitmapCache;

    public FeedAdapter(Context context, Cursor cursor) {
        super(context, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        RequestQueue queue = ((Application) context.getApplicationContext()).getRequestQueue();
        mBitmapCache = ((Application) context.getApplicationContext()).getBitmapCache();
        mImageLoader = new ImageLoader(queue, mBitmapCache);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.cell_bookmark, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        FeedItem item = FeedDAO.getInstance().feedFromCursor(cursor);

        holder.userName.setText(item.user.name);
        if (!TextUtils.isEmpty(item.comment)) {
            holder.comment.setVisibility(View.VISIBLE);
            holder.comment.setText(item.comment);
        } else {
            holder.comment.setVisibility(View.GONE);
            holder.comment.setText("");
        }
        holder.pageTitle.setText(item.title);
        holder.when.setText(Utility.getTimeString(context, item.datetime));

        if (holder.userImage.getTag() != null) {
            ImageLoader.ImageContainer imageContainer = (ImageLoader.ImageContainer) holder.userImage.getTag();
            imageContainer.cancelRequest();
        }

        ImageLoader.ImageListener userImageListener = new UserImageListener(holder.userImage, mBitmapCache);
        holder.userImage.setTag(mImageLoader.get(item.user.profile_image_url.toString(), userImageListener));

        if (holder.favicon.getTag() != null) {
            ImageLoader.ImageContainer imageContainer = (ImageLoader.ImageContainer) holder.favicon.getTag();
            imageContainer.cancelRequest();
        }

        ImageLoader.ImageListener faviconLoader = ImageLoader.getImageListener(holder.favicon, android.R.color.transparent, android.R.color.transparent);
        holder.userImage.setTag(mImageLoader.get(item.favicon_url.toString(), faviconLoader));
    }


    /**
     * ビューホルダークラス
     */
    static class ViewHolder {
        @InjectView(R.id.userImage)
        ImageView userImage;
        @InjectView(R.id.userName)
        TextView userName;
        @InjectView(R.id.comment)
        TextView comment;
        @InjectView(R.id.pageTitle)
        TextView pageTitle;
        @InjectView(R.id.when)
        TextView when;
        @InjectView(R.id.favicon)
        ImageView favicon;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
