package ash.glay.hbfavclone.model;

import java.util.Date;

import lombok.Value;

/**
 * 統計情報をオブジェクト化、とりあえず雑
 */
@Value
public class Stats {
    /**
     * 実行日付
     */
    Date date;
    /**
     * 実行結果
     */
    boolean isSuccess;
    /**
     * 取得件数（実行結果がfalseのとき必ず0）
     */
    int count;
    /**
     * ステータス
     */
    String status;
}
