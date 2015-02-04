package net.fhtagn.orientation.orientation.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import net.fhtagn.orientation.orientation.events.OrientationEvent;
import net.fhtagn.orientation.orientation.math.Quaternion;
import net.fhtagn.orientation.orientation.sensors.OrientationProvider;

public class LevelView extends View {
    private Paint bgPaint;
    private Paint textPaint;
    private float width, height;
    private OrientationProvider orientationProvider;

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.RED);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
    }

    public void setOrientationProvider(OrientationProvider provider) {
        orientationProvider = provider;
        orientationProvider.getEventBus().register(this);
    }

    public void onEvent(OrientationEvent event) {
        postInvalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        if (orientationProvider != null && !orientationProvider.getEventBus().isRegistered(this)) {
            orientationProvider.getEventBus().register(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (orientationProvider != null) {
            orientationProvider.getEventBus().unregister(this);
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Account for padding
        float xpad = (float)(getPaddingLeft() + getPaddingRight());
        float ypad = (float)(getPaddingTop() + getPaddingBottom());

        // Account for the label
        //if (mShowText) xpad += mTextWidth;

        width = (float)w - xpad;
        height = (float)h - ypad;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0.1f * width, 0.1f * height, 0.8f * width, 0.8f * height, bgPaint);

        if (orientationProvider != null) {
            final Quaternion q = orientationProvider.getOrientation();
            canvas.drawText(q.toString(), 0.1f * width, 0.1f * height, textPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int desiredWidth_dp = 100;
        final int desiredHeight_dp = 30;
        final float dp2px = getResources().getDisplayMetrics().density;
        final int desiredWidth = Math.max(1, (int) (dp2px * desiredWidth_dp));
        final int desiredHeight = Math.max(1, (int) (dp2px * desiredHeight_dp));

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            //width = Math.min(desiredWidth, widthSize);
            width = widthSize;
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            //height = Math.min(desiredHeight, heightSize);
            height = heightSize;
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }
}
