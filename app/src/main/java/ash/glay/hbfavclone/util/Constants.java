package ash.glay.hbfavclone.util;

/**
 * Created by StraySheep on 2014/12/27.
 */
public class Constants {
    private Constants() {
    }

    /**
     * ログファイル名
     */
    public static final String LOG_FILE_NAME = "log.txt";

    /**
     * コネクションタイムアウト
     */
    public static final int CONNECTION_TIME_OUT = 25;

    /**
     * アカウントタイプ
     */
    public static final String ACCOUNT_TYPE = "glay.ash.hatena.account";

    /**
     * アカウントトークンタイプ
     */
    public static final String ACCOUNT_AUTHTOKEN_TYPE = "glay.ash.hbfavclone.authtokentype";

    /**
     * ID入力レスポンス
     */
    public static final String ACCOUNT_RESPONSE = "glay.ash.hbfavclone.response";

    /**
     * 認証リクエスト
     */
    public static final int REQUEST_LOGIN = 3253;

    /**
     * URLを渡すバンドルキー
     */
    public final static String BUNDLE_KEY_URL = "url-key";

    /**
     * ブックマーク情報取得時インテントアクション
     */
    public final static String ACTION_RECEIVE_BOOKMARK_INFO = "ash.glay.hbfavclone.hbcount";
}
