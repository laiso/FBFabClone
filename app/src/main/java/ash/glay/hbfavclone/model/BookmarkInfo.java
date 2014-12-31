package ash.glay.hbfavclone.model;

import java.io.Serializable;
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
}
