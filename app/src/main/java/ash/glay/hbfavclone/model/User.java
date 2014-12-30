package ash.glay.hbfavclone.model;

import java.net.URI;

import lombok.Value;

/**
 * ユーザーを表すオブジェクト<br />
 * テーブルを正規化したい
 */
@Value
public class User {
    public String name;
    public URI profile_image_url;
}
