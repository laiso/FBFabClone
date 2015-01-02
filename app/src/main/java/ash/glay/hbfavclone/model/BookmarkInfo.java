package ash.glay.hbfavclone.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Value;

/**
 * ブックマーク情報
 */
@Value
public class BookmarkInfo implements Serializable {
    public String url;
    public int count;
    public List<CommentedUser> commentedUserList;

    /**
     * コメントしているユーザーだけを抽出
     *
     * @return
     */
    public List<CommentedUser> getHasCommentUsers() {
        List<CommentedUser> result = new ArrayList<>();
        for (CommentedUser user : commentedUserList) {
            if (!TextUtils.isEmpty(user.comment)) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * コメントしてないユーザーだけを抽出
     *
     * @return
     */
    public List<CommentedUser> getNotCommentUsers() {
        List<CommentedUser> result = new ArrayList<>();
        for (CommentedUser user : commentedUserList) {
            if (TextUtils.isEmpty(user.comment)) {
                result.add(user);
            }
        }
        return result;
    }
}
