package ash.glay.hbfavclone.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Value;

/**
 * コメントユーザーを表すモデル
 */
@Value
public class CommentedUser implements Serializable {
    public String user;
    public Date timestamp;
    public String comment;
}
