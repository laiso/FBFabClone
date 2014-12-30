package ash.glay.hbfavclone;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ash.glay.hbfavclone.util.Constants;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 最近同期した時刻をリスト表示するActivity
 */
public class RecentActivity extends Activity {

    @InjectView(R.id.listView2)
    ListView mRecentSyncData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        ButterKnife.inject(this);

        mRecentSyncData.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, readLogFile()));
    }

    /**
     * ログファイルを読み込みリスト化する
     *
     * @return
     */
    private List<String> readLogFile() {
        List<String> logList = new ArrayList<>();
        File logFile = new File(getFilesDir() + "/" + Constants.LOG_FILE_NAME);
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logList.add(line);
            }
        } catch (IOException e) {
            for (StackTraceElement stackTrace : e.getStackTrace())
                logList.add(stackTrace.toString());
        }
        return logList;
    }
}
