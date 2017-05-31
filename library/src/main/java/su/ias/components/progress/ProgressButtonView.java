package su.ias.components.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * Created on 18.01.17.
 * Custom progress button
 */

public class ProgressButtonView extends AppCompatButton {

    private final CharSequence emptyString = "";
    private State state;
    private int mDrawablePadding;
    private int mThickness;
    private int mProgressColor;
    private int mProgressBackground;
    private int mSuccessBackground;
    private int mErrorBackground;
    private int mSuccessDrawable;
    private int mErrorDrawable;
    private CharSequence idleText;
    private CharSequence errorText = "";
    private Drawable mBackgroundDrawable;
    private CircularProgressDrawable progressDrawable;

    public ProgressButtonView(Context context) {
        super(context);
        init(null, 0);
    }

    public ProgressButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ProgressButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {

        //@formatter:off
        mDrawablePadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8,
                getResources().getDisplayMetrics());
        //@formatter:on

        state = State.IDLE;

        if (attrs != null) {

            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                                                                   R.styleable.ProgressButtonView,
                                                                   defStyleAttr,
                                                                   0);

            try {

                mProgressColor = typedArray.getColor(R.styleable.ProgressButtonView_progressColor,
                                                     Color.WHITE);
                //@formatter:off
                mThickness = typedArray.getDimensionPixelSize(
                        R.styleable.ProgressButtonView_progressThickness,
                        (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                2,
                                getResources().getDisplayMetrics()));
                //@formatter:on

                mSuccessBackground =
                        typedArray.getColor(R.styleable.ProgressButtonView_successBackgroundTint,
                                            Color.GREEN);

                mErrorBackground =
                        typedArray.getColor(R.styleable.ProgressButtonView_successBackgroundTint,
                                            Color.RED);

                mProgressBackground =
                        typedArray.getColor(R.styleable.ProgressButtonView_progressBackgroundTint,
                                            Color.BLUE);

                mSuccessDrawable =
                        typedArray.getResourceId(R.styleable.ProgressButtonView_successDrawable,
                                                 android.R.drawable.ic_menu_send);

                mErrorDrawable =
                        typedArray.getResourceId(R.styleable.ProgressButtonView_errorDrawable,
                                                 android.R.drawable.ic_dialog_alert);

            } finally {
                typedArray.recycle();
            }

        }

        idleText = getText();
        mBackgroundDrawable = getBackground();
    }

    public void showProgress() {
        preventDefault();
        if (state != State.PROGRESS) {
            state = State.PROGRESS;
            this.setText(emptyString);
        }
        invalidate();
    }

    public void showSuccess() {
        if (state != State.SUCCESS) {
            state = State.SUCCESS;
            this.setText(emptyString);
        } else {
            preventDefault();
        }
        invalidate();
    }

    public void showError() {
        if (state != State.ERROR) {
            state = State.ERROR;
            this.setText(errorText);
        } else {
            preventDefault();
        }

        invalidate();
    }

    public boolean isSuccess() {
        return state == State.SUCCESS;
    }

    public boolean isError() {
        return state == State.ERROR;
    }

    public boolean isProgress() {
        return state == State.PROGRESS;
    }

    private void drawSuccessState(Canvas canvas) {
        getCenterDrawable(mSuccessDrawable).draw(canvas);
        setBackgroundTintColor(mSuccessBackground);
    }

    private void drawErrorState(Canvas canvas) {
        getCenterDrawable(mErrorDrawable).draw(canvas);
        setBackgroundTintColor(mErrorBackground);
    }

    public void preventDefault() {
        state = State.IDLE;
        this.setText(idleText);
        mBackgroundDrawable.clearColorFilter();
        if (progressDrawable != null) {
            progressDrawable.stop();
        }
    }

    private void drawProgress(Canvas canvas) {
        if (progressDrawable == null) {

            progressDrawable = new CircularProgressDrawable(mProgressColor, mThickness);

            setCustomBounds(progressDrawable);
            progressDrawable.setCallback(this);
        }
        setBackgroundTintColor(mProgressBackground);
        if (!progressDrawable.isRunning()) {
            progressDrawable.start();
        }
        progressDrawable.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (state) {
            case SUCCESS:
                drawSuccessState(canvas);
                break;
            case ERROR:
                drawErrorState(canvas);
                break;
            case PROGRESS:
                drawProgress(canvas);
                break;
            default:
                preventDefault();
        }

    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.setState(state);
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            this.state = savedState.getState();
            if (this.state != State.IDLE) {
                this.setText(emptyString);
            }
            super.onRestoreInstanceState(savedState.getSuperState());
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private Drawable getCenterDrawable(@DrawableRes int drawable) {
        Drawable d = ContextCompat.getDrawable(getContext(), drawable);
        setCustomBounds(d);
        return d;
    }

    private void setBackgroundTintColor(@ColorInt int color) {
        mBackgroundDrawable.clearColorFilter();
        mBackgroundDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    private void setCustomBounds(Drawable drawable) {

        int wOffset = getWidth() / 2;
        int hOffset = getHeight() / 2;
        int left = wOffset - hOffset / 2;
        int right = left + hOffset;

        int top = hOffset - hOffset / 2;
        int bottom = top + hOffset;

        if (drawable != progressDrawable) {
            if (drawable.getIntrinsicHeight() < getHeight() - mDrawablePadding * 2) {
                top = hOffset - drawable.getIntrinsicHeight() / 2;
                bottom = top + drawable.getIntrinsicHeight();
            }

            if (drawable.getIntrinsicWidth() < right - left) {
                left = wOffset - drawable.getIntrinsicWidth() / 2;
                right = left + drawable.getIntrinsicWidth();
            }
        }

        if (bottom > getHeight() || bottom <= hOffset) {
            bottom = getHeight() - mDrawablePadding - getPaddingBottom();
        }
        if (right > getWidth()) {
            right = getWidth() - mDrawablePadding - getPaddingRight();
            left = mDrawablePadding + getPaddingLeft();
        }
        drawable.setBounds(left, top, right, bottom);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == progressDrawable || super.verifyDrawable(who);
    }

    private enum State {
        IDLE, PROGRESS, SUCCESS, ERROR
    }

    static class SavedState extends BaseSavedState {

        private State state;

        public SavedState(Parcel source) {
            super(source);
            state = State.valueOf(source.readString());
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(state.name());
        }

    }

}