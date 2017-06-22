package su.ias.components.progress;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import static su.ias.components.progress.Constants.INSTANCE_COLOR;
import static su.ias.components.progress.Constants.INSTANCE_STATE;
import static su.ias.components.progress.Constants.INSTANCE_THICKNESS;

/**
 * Created on 5/31/17.
 * only load animation
 */

public class MaterialProgressView extends View {

    private int color;
    private int thickness;

    public MaterialProgressView(Context context) {
        super(context);
        init(null, 0);
    }

    public MaterialProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MaterialProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialProgressView(Context context,
                                @Nullable AttributeSet attrs,
                                int defStyleAttr,
                                int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(@Nullable AttributeSet attrs, int defStyleAttr) {
        initAttributeSet(attrs, defStyleAttr);
        initBackground();
    }

    private void initBackground() {
        setBackground(new CircularProgressDrawable(color, thickness));
        startAnimation();
    }

    private void initAttributeSet(@Nullable AttributeSet attrs, int defStyle) {

        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                                                                 R.styleable.MaterialProgressView,
                                                                 defStyle,
                                                                 0);

        try {

            color = a.getColor(R.styleable.MaterialProgressView_color, Color.RED);
            //@formatter:off
            thickness = a.getDimensionPixelSize(
                    R.styleable.MaterialProgressView_thickness,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
                                                    getResources().getDisplayMetrics()));
            //@formatter:on
        } finally {
            a.recycle();
        }

    }

    public boolean isRunning() {
        return ((Animatable) getBackground()).isRunning();
    }

    public void startAnimation() {
        if (isRunning())
            return;
        ((Animatable) getBackground()).start();
    }

    public void stopAnimation() {
        if (!isRunning())
            return;
        ((Animatable) getBackground()).stop();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_THICKNESS, thickness);
        bundle.putInt(INSTANCE_COLOR, color);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            thickness = bundle.getInt(INSTANCE_THICKNESS);
            color = bundle.getInt(INSTANCE_COLOR);
            initBackground();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }
}
