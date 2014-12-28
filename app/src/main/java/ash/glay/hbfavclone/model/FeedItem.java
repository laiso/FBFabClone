package ash.glay.hbfavclone.model;

import java.net.URI;
import java.util.Date;

/**
 * フィードを保持するモデルオブジェクト
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeedItem)) return false;

        FeedItem that = (FeedItem) o;

        if (!comment.equals(that.comment)) return false;
        if (!datetime.equals(that.datetime)) return false;
        if (!description.equals(that.description)) return false;
        if (!favicon_url.equals(that.favicon_url)) return false;
        if (!link.equals(that.link)) return false;
        if (!permalink.equals(that.permalink)) return false;
        if (thumbnail_url != null ? !thumbnail_url.equals(that.thumbnail_url) : that.thumbnail_url != null)
            return false;
        if (!title.equals(that.title)) return false;
        if (!user.equals(that.user)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        // 同値性とハッシュ算出ではcount（ブクマコメ数）とcreated_at（X分前みたいなの）は除外している
        // コメント内容が変わった場合は、別のアイテムとして認識されてしまうため適切ではないけど、一意性IDがないので

        int result = title.hashCode();
        result = 31 * result + link.hashCode();
        result = 31 * result + favicon_url.hashCode();
        result = 31 * result + comment.hashCode();
        result = 31 * result + datetime.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + permalink.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + (thumbnail_url != null ? thumbnail_url.hashCode() : 0);
        return result;
    }

    /**
     * ユーザーを表すオブジェクト<br />
     * テーブルを正規化したい
     */
    public static class User {
        public String name;
        public URI profile_image_url;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof User)) return false;

            User user = (User) o;

            if (!name.equals(user.name)) return false;
            if (!profile_image_url.equals(user.profile_image_url)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + profile_image_url.hashCode();
            return result;
        }

        public User(String name, URI profile_image_url) {
            this.name = name;
            this.profile_image_url = profile_image_url;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", profile_image_url=" + profile_image_url +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TimeLineFeed{" +
                "title='" + title + '\'' +
                ", link=" + link +
                ", favicon_url=" + favicon_url +
                ", comment='" + comment + '\'' +
                ", count=" + count +
                ", datetime=" + datetime +
                ", create_at='" + created_at + '\'' +
                ", user=" + user +
                ", permalink=" + permalink +
                ", description='" + description + '\'' +
                ", thumbnail_url=" + thumbnail_url +
                '}';
    }
}
