package ash.glay.hbfavclone.view;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ash.glay.hbfavclone.R;
import ash.glay.hbfavclone.model.Stats;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecentLogFragment extends Fragment {
    final static private SimpleDateFormat sSdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    private static final String SYNC_LOG_FILE = "date-param";
    private String mSyncLogFile;

    @InjectView(R.id.graphView)
    GraphView mGraphView;
    @InjectView(R.id.listView)
    ListView mListView;

    public static RecentLogFragment newInstance(String logDate) {
        RecentLogFragment fragment = new RecentLogFragment();
        Bundle args = new Bundle();
        args.putString(SYNC_LOG_FILE, logDate);
        fragment.setArguments(args);
        return fragment;
    }

    public RecentLogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSyncLogFile = getArguments().getString(SYNC_LOG_FILE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recent_log_page, container, false);
        ButterKnife.inject(this, v);
        List<Stats> syncStats = makeStatsData(mSyncLogFile);
        mGraphView.setStats(syncStats);
        mListView.setAdapter(new StatsListAdapter(getActivity(), syncStats));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mGraphView.setSelectionPoint(position);
            }
        });
        return v;
    }

    public void resetAnimation() {
        if (mGraphView != null) {
            mGraphView.setAnimatevalue(0);
        }
    }

    public void showAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofInt(mGraphView, "animatevalue", 0, 100);
        animator.setStartDelay(100);
        animator.setDuration(400);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    private static class StatsListAdapter extends BaseAdapter {

        private List<Stats> mData;
        private int colorNormal;
        private int colorAlert;
        private int colorNodata;
        private int colorDate;
        final static SimpleDateFormat sSdt = new SimpleDateFormat("MM:dd");

        StatsListAdapter(Context context, List<Stats> data) {
            colorNormal = context.getResources().getColor(R.color.primary_text);
            colorAlert = context.getResources().getColor(R.color.alert);
            colorNodata = context.getResources().getColor(R.color.accent);
            colorDate = context.getResources().getColor(R.color.secondary_text);
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Stats getItem(int position) {
            return mData.get(position);
        }

        @Override
        public boolean isEnabled(int position) {
            return mData.get(position).isSuccess() && mData.get(position).getCount() != 0;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            }
            TextView tv1 = ButterKnife.findById(convertView, android.R.id.text1);
            TextView tv2 = ButterKnife.findById(convertView, android.R.id.text2);

            Stats s = getItem(position);
            tv1.setTextColor(s.isSuccess() ? s.getCount() != 0 ? colorNormal : colorNodata : colorAlert);
            tv2.setTextColor(colorDate);
            tv1.setText(s.getStatus());
            tv2.setText(sSdf.format(s.getDate()));

            return convertView;
        }
    }

    /**
     * 統計データを生成します
     *
     * @return
     */
    private List<Stats> makeStatsData(String fileName) {
        List<Stats> result = new ArrayList<>();
        final Pattern pattern = Pattern.compile("^(.*?) : (\\d{4})年(\\d{1,2})月(\\d{1,2})日 (\\d{1,2}):(\\d{2})");
        final Pattern pattern2 = Pattern.compile("^.*?(\\d+)件$");
        Matcher matcher = pattern.matcher("");
        Matcher matcher2 = pattern2.matcher("");
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Date date = null;
                matcher.reset(line);
                if (matcher.find()) {
                    try {
                        String dateString = String.format("%04d/%02d/%02d %02d:%02d", Integer.valueOf(matcher.group(2)),
                                Integer.valueOf(matcher.group(3)), Integer.valueOf(matcher.group(4)), Integer.valueOf(matcher.group(5)), Integer.valueOf(matcher.group(6)));
                        date = sSdf.parse(dateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    matcher2.reset(matcher.group(1));
                    if (matcher2.find()) {
                        result.add(new Stats(date, true, Integer.parseInt(matcher2.group(1)), matcher.group(1)));
                    } else {
                        result.add(new Stats(date, false, 0, matcher.group(1)));
                    }
                } else {
                    Log.i("debug", line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(result, new Comparator<Stats>() {
            @Override
            public int compare(Stats lhs, Stats rhs) {
                return lhs.getDate().compareTo(rhs.getDate());
            }
        });
        return result;
    }
}
