package ash.glay.hbfavclone.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ash.glay.hbfavclone.R;
import ash.glay.hbfavclone.model.Stats;

public class GraphView extends View {

    private TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mPath = new Path();
    private DashPathEffect mDashPathEffect = new DashPathEffect(new float[]{15, 20}, 0);
    private int mAnimateValue;
    private int mSelection;

    private List<Stats> mStats;

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GraphView);
        mAnimateValue = a.getInt(R.styleable.GraphView_animatevalue, 100);
        mSelection = a.getInt(R.styleable.GraphView_selection, -1);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        setMeasuredDimension((int) (320 * metrics.density), (int) (320 * metrics.density));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final float density = metrics.density;
        mTextPaint.setColor(getResources().getColor(R.color.secondary_text));
        mTextPaint.setTextSize(12.f * density);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.icons));
        canvas.drawColor(getResources().getColor(R.color.icons));

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1.f * density);
        // ベース線を引く
        mPaint.setColor(getResources().getColor(R.color.divider));
        int height;
        int value;
        for (int i = 0; i < 6; i++) {
            value = 25 - i * 5;
            height = heightFromValue(value, false);
            mPaint.setPathEffect(i != 5 ? mDashPathEffect : null);
            canvas.drawText(String.valueOf(value), 4.f * density, height + 4.f * density, mTextPaint);
            canvas.drawLine(20.f * density, height, 320.f * density, height, mPaint);
        }

        if (mStats == null) {
            return;
        }

        mPaint.setStrokeWidth(4.f * density);
        int colorNormal = getResources().getColor(R.color.primary);
        int colorSelected = getResources().getColor(R.color.primary_dark);
        int[] hours = new int[24];
        Calendar c = Calendar.getInstance(Locale.JAPAN);
        int counter = 0;
        for (Stats s : mStats) {
            c.setTime(s.getDate());
            int aHour = c.get(Calendar.HOUR_OF_DAY);
            int aMinute = c.get(Calendar.MINUTE);
            hours[aHour]++;

            int plot = Math.round((10.f + aHour * 12.5f + aMinute * 0.21f) * density);
            height = heightFromValue(s.getCount(), true);
            mPaint.setColor(counter == mSelection ? colorSelected : colorNormal);
            canvas.drawLine(plot, height, plot, 310.f * density, mPaint);
            counter++;
        }

        // 時間当たりの同期試行回数
        int count = 0;
        boolean isInitial = true;
        mPaint2.setStrokeWidth(2.f * density);
        mPaint2.setColor(getResources().getColor(R.color.alert));
        mPaint2.setStyle(Paint.Style.FILL);
        mPaint2.setAlpha(220);
        for (int v : hours) {
            // プロット位置
            if (isInitial) {
                canvas.drawCircle((26.25f + count * 12.5f) * density, heightFromValue(v, true), 10.f, mPaint2);
                canvas.drawPoint((26.25f + count * 12.5f) * density, heightFromValue(v, true), mPaint2);
                mPath.moveTo((int) ((26.25f + count * 12.5f) * density), heightFromValue(v, true));
                isInitial = false;
            } else {
                canvas.drawCircle((26.25f + count * 12.5f) * density, heightFromValue(v, true), 10.f, mPaint2);
                mPath.lineTo((int) ((26.25f + count * 12.5f) * density), heightFromValue(v, true));
            }
            count++;
        }
        mPaint2.setStyle(Paint.Style.STROKE);
        mPaint2.setStrokeJoin(Paint.Join.BEVEL);
        mPaint2.setStrokeWidth(2.f * density);
        canvas.drawPath(mPath, mPaint2);

    }

    /**
     * 統計情報をグラフにセット
     *
     * @param stats
     */
    public void setStats(List<Stats> stats) {
        mStats = stats;
    }

    public void setAnimatevalue(int animateValue) {
        this.mAnimateValue = animateValue;
        invalidate();
    }

    public int getAnimatevalue() {
        return mAnimateValue;
    }

    private int heightFromValue(int value, boolean animated) {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        final float density = metrics.density;
        float animatedValue = animated ? (float) value * ((float) mAnimateValue / 100.f) : value;
        return Math.max(Math.round((310.f - 12.f * animatedValue) * density), 0);
    }
}
