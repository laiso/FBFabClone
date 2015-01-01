package ash.glay.hbfavclone;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ash.glay.hbfavclone.model.BookmarkInfo;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * ユーザーコメントを表示するFragment
 */
public class UserCommentFragment extends Fragment {

    @InjectView(R.id.list_view)
    ListView mListView;

    private static final String BOOKMARK_INFO = "bookmark-info";
    private BookmarkInfo mBookmarkInfo;

    private OnUserCommentFragmentListener mListener;

    public static UserCommentFragment newInstance(BookmarkInfo bookmarkInfo) {
        UserCommentFragment fragment = new UserCommentFragment();
        Bundle args = new Bundle();
        args.putSerializable(BOOKMARK_INFO, bookmarkInfo);
        fragment.setArguments(args);
        return fragment;
    }

    public UserCommentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBookmarkInfo = (BookmarkInfo) getArguments().getSerializable(BOOKMARK_INFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usercomment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnUserCommentFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnUserCommentFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnUserCommentFragmentListener {
        public void onFragmentInteraction(String id);
    }

}
