package su.ias.components.progress.sample;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import su.ias.components.progress.CircleProgressView;
import su.ias.components.progress.MaterialProgressView;
import su.ias.components.progress.ProgressButtonView;

public class MainActivity extends AppCompatActivity {

    private MaterialProgressView progressView;
    private CircleProgressView pView;
    private int startValue;
    private boolean isRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_sample).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressButtonClick(v);
            }
        });

        progressView = (MaterialProgressView) findViewById(R.id.pprogress);
        findViewById(R.id.btn_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progressView.isRunning()) {
                    progressView.stopAnimation();
                } else {
                    progressView.startAnimation();
                }
            }
        });

        pView = (CircleProgressView) findViewById(R.id.pView);

        final ValueAnimator animator = ValueAnimator.ofInt(pView.getMax(), 0).setDuration(4000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                if (value < 10) {
                    pView.setProgressColor(Color.RED);
                    pView.setThickness(24f);
                } else {
                    pView.setProgressColor(Color.BLACK);
                    pView.setThickness(12f);
                }
                pView.setProgress(value);
            }
        });

        findViewById(R.id.bnt_progress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startValue = pView.getProgress();
                isRun = (isRun && startValue != 0);
                if (!isRun) {
                    if (startValue != 0) {
                        animator.setIntValues(startValue, 0);
                    } else {
                        animator.setIntValues(pView.getMax(), 0);
                    }
                    animator.start();
                    isRun = true;
                } else {
                    animator.cancel();
                    isRun = false;
                }
            }
        });

    }

    public void progressButtonClick(View view) {
        ProgressButtonView button = (ProgressButtonView) view;
        if (button.isProgress()) {
            button.showSuccess();
        } else if (button.isError()) {
            button.preventDefault();
        } else if (button.isSuccess()) {
            button.showError();
        } else {
            button.showProgress();
        }
    }
}
