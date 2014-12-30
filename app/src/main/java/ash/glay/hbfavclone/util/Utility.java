package ash.glay.hbfavclone.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * ユーティリティメソッドを定義するクラス
 */
public class Utility {

    private Utility() {
    }

    /**
     * ファイルをコピーします
     *
     * @param src
     * @param dst
     */
    public static void copyFile(File src, File dst) {

        try (FileInputStream inStream = new FileInputStream(src);
             FileOutputStream outStream = new FileOutputStream(dst)) {
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
