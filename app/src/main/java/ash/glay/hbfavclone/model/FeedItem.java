package ash.glay.hbfavclone.model;

import java.net.URI;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * フィードを保持するモデルオブジェクト
 */
@Data
// 同値性とハッシュ算出ではcount（ブクマコメ数）とcreated_at（X分前みたいなの）は除外している
// コメント内容が変わった場合は、別のアイテムとして認識されてしまうため適切ではないけど、一意性IDがないので
@EqualsAndHashCode(exclude = {"count", "created_at"})
public class FeedItem {
    public String title;
    public URI link;
    public URI favicon_url;
    public String comment;
    public int count;
    public Date datetime;
    public String created_at;
    public User user;
    public URI permalink;
    public String description;
    public URI thumbnail_url;

    /**
     * ユーザーを表すオブジェクト<br />
     * テーブルを正規化したい
     */
    @Value
    public static class User {
        public String name;
        public URI profile_image_url;

    }
}
