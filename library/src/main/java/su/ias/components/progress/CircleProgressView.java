package su.ias.components.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import static su.ias.components.progress.Constants.INSTANCE_BG_COLOR;
import static su.ias.components.progress.Constants.INSTANCE_BG_THICKNESS;
import static su.ias.components.progress.Constants.INSTANCE_MAX;
import static su.ias.components.progress.Constants.INSTANCE_PIN;
import static su.ias.components.progress.Constants.INSTANCE_PROGRESS;
import static su.ias.components.progress.Constants.INSTANCE_PROGRESS_COLOR;
import static su.ias.components.progress.Constants.INSTANCE_START_ANGLE;
import static su.ias.components.progress.Constants.INSTANCE_STATE;
import static su.ias.components.progress.Constants.INSTANCE_THICKNESS;

/**
 * Created on 5/25/17.
 */
@SuppressWarnings("unused")
public class CircleProgressView extends View {

    private static final float arcAngle = 360;

    private Paint paint = new Paint();
    private RectF rectF = new RectF();

    private float bgThickness;
    private float thickness;
    private int progress;
    private int max;
    private float startAngle;

    private Bitmap pin;

    private int bgColor;
    private int progressColor;

    public CircleProgressView(Context context) {
        super(context);
        init(null, 0);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {

        super(context, attrs);
        init(attrs, 0);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);

    }

    private void init(@Nullable AttributeSet attrs, int defStyleAttr) {

        // init paint
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        TypedArray array =
                getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        try {
            //@formatter:off
            thickness = array.getDimensionPixelSize(
                    R.styleable.CircleProgressView_backgroundThickness,
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            2.4f,
                            getResources().getDisplayMetrics()));

            bgThickness = array.getDimensionPixelSize(
                    R.styleable.CircleProgressView_progressThickness,
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            2.0f,
                            getResources().getDisplayMetrics()));
            //@formatter:on
            bgColor = array.getColor(R.styleable.CircleProgressView_bgColor, Color.GRAY);
            progressColor =
                    array.getColor(R.styleable.CircleProgressView_progressColor, Color.BLACK);
            pin = BitmapUtils.getBitmap(getContext(),
                                        array.getResourceId(R.styleable.CircleProgressView_pin,
                                                            R.drawable.ic_progress_pin));
            max = array.getInt(R.styleable.CircleProgressView_maxProgress, 100);
            progress = array.getInt(R.styleable.CircleProgressView_progress, 100);
            startAngle = array.getFloat(R.styleable.CircleProgressView_startAngle, 270f);
        } finally {
            array.recycle();
        }
    }

    public void setPin(int pinRes) {
        pin = BitmapUtils.getBitmap(getContext(), pinRes);
        invalidate();
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (this.progress > getMax()) {
            this.progress = getMax();
        }
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    public void setProgressColor(int color) {
        progressColor = color;
        invalidate();
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        rectF.set(pin.getHeight() / 2 + bgThickness / 2,
                  pin.getHeight() / 2 + bgThickness / 2,
                  width - pin.getHeight() / 2 - bgThickness / 2,
                  MeasureSpec.getSize(heightMeasureSpec) - pin.getHeight() / 2 - bgThickness / 2f);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //draw bg circle
        paint.setColor(bgColor);
        paint.setStrokeWidth(bgThickness);
        float innerCircleRadius = getHeight()/2 - pin.getHeight() / 2 - bgThickness / 2;

        canvas.drawCircle(getHeight() / 2.0f, getHeight() / 2.0f, innerCircleRadius, paint);

        //draw progress circle
        paint.setStrokeWidth(thickness);
        paint.setColor(progressColor);

        float finishedStartAngle = startAngle;
        if (getProgress() == 0) {
            finishedStartAngle = 0.01f;
        }
        float finishedSweepAngle = getProgress() / (float) getMax() * arcAngle;
        canvas.drawArc(rectF, finishedStartAngle, -finishedSweepAngle, false, paint);

        // draw pin rotate canvas and draw)
        if (pin != null) {
            canvas.save();
            canvas.rotate(-finishedSweepAngle, getWidth() / 2, getHeight() / 2);
            canvas.rotate(startAngle + 90, getWidth() / 2, getHeight() / 2);
            canvas.translate(canvas.getWidth() / 2 - pin.getWidth() / 2, bgThickness / 2);
            canvas.drawBitmap(pin, 0, 0, null);
            canvas.restore();
        }

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(INSTANCE_THICKNESS, thickness);
        bundle.putFloat(INSTANCE_BG_THICKNESS, bgThickness);
        bundle.putInt(INSTANCE_PROGRESS_COLOR, progressColor);
        bundle.putInt(INSTANCE_BG_COLOR, bgColor);
        bundle.putInt(INSTANCE_MAX, max);
        bundle.putInt(INSTANCE_PROGRESS, progress);
        bundle.putFloat(INSTANCE_START_ANGLE, startAngle);
        bundle.putParcelable(INSTANCE_PIN, pin);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            thickness = bundle.getFloat(INSTANCE_THICKNESS);
            bgThickness = bundle.getFloat(INSTANCE_BG_THICKNESS);
            bgColor = bundle.getInt(INSTANCE_BG_COLOR);
            max = bundle.getInt(INSTANCE_MAX);
            progress = bundle.getInt(INSTANCE_PROGRESS);
            pin = bundle.getParcelable(INSTANCE_PIN);
            startAngle = bundle.getFloat(INSTANCE_START_ANGLE);
            setProgressColor(bundle.getInt(INSTANCE_PROGRESS_COLOR));
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }
}
