package ash.glay.hbfavclone;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import ash.glay.hbfavclone.util.Constants;
import ash.glay.hbfavclone.view.RecentLogFragment;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 最近同期した時刻をリスト表示するActivity
 */
public class RecentActivity extends Activity implements ViewPager.OnPageChangeListener {

    @InjectView(R.id.viewPager)
    ViewPager mRecentSyncData;

    SyncLogPageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        ButterKnife.inject(this);

        mAdapter = new SyncLogPageAdapter(getFragmentManager(), this);
        mRecentSyncData.setAdapter(mAdapter);
        mRecentSyncData.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mAdapter.instantiateItem(mRecentSyncData, position).showAnimation();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    static class SyncLogPageAdapter extends FragmentPagerAdapter {

        List<String> mSyncLogList = new ArrayList<String>();

        SyncLogPageAdapter(FragmentManager fm, Context context) {
            super(fm);
            final Pattern pattern = Pattern.compile("^log_\\d{8}\\.txt$");
            for (File file : context.getFilesDir().listFiles()) {
                if (pattern.matcher(file.getName()).find()) {
                    mSyncLogList.add(file.getAbsolutePath());
                }
            }
        }

        @Override
        public RecentLogFragment instantiateItem(ViewGroup container, int position) {
            return (RecentLogFragment) super.instantiateItem(container, position);
        }

        @Override
        public RecentLogFragment getItem(int i) {
            return RecentLogFragment.newInstance(mSyncLogList.get(i));
        }

        @Override
        public int getCount() {
            return mSyncLogList.size();
        }
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
//                Log.i("info", line);
            }
        } catch (IOException e) {
            for (StackTraceElement stackTrace : e.getStackTrace())
                logList.add(stackTrace.toString());
        }
        return logList;
    }
}
