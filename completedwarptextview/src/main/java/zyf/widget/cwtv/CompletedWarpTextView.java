package zyf.widget.cwtv;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangYifan on 2017/1/26.
 * <p>
 * 一个上下没有留白的TextView
 * <p>
 */

public class CompletedWarpTextView extends View {
    private String mText;
    private List<String> mSpliteTexts;
    private Paint mPaint;

    private int mViewWidth;
    private int mViewHeight;
    private int mSingleBaseLineHeight;
    private int mSingleLineHeight;

    private float mSpacingmult;
    private float mSpacingadd;
    private int[] mPadding;

    public CompletedWarpTextView(Context context) {
        this(context, null);
    }

    public CompletedWarpTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(20);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setAntiAlias(true);
        mText = "";
        mSpacingmult = 1.0F;
        mSpacingadd = 0;
        mPadding = new int[]{0, 0, 0, 0};
        setWillNotDraw(false);
    }

    /**
     * 设置内容
     *
     * @param text 内容
     */
    public void setText(String text) {
        if (text != null) {
            mText = text;
        } else {
            mText = "";
        }
        requestLayout();
        invalidate();
    }

    /**
     * 设置行间距
     *
     * @param spacingmult 多倍行间距
     * @param spacingadd  多倍行间距之外再加多少像素
     */
    public void setSpacing(float spacingmult, float spacingadd) {
        mSpacingmult = spacingmult;
        mSpacingadd = spacingadd;
        requestLayout();
        invalidate();
    }

    /**
     * 设置字体大小，单位sp
     *
     * @param sp sp
     */
    public void setTextSize(int sp) {
        Context c = getContext();
        Resources r;

        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, r.getDisplayMetrics());
        mPaint.setTextSize(textSize);
        requestLayout();
        invalidate();
    }

    /**
     * 设置字体颜色
     *
     * @param color int类型的颜色
     */
    public void setTextColor(@ColorInt int color) {
        mPaint.setColor(color);
        invalidate();
    }

    /**
     * 设置字体靠左
     */
    public void setTextAlignLeft() {
        mPaint.setTextAlign(Paint.Align.LEFT);
        invalidate();
    }

    /**
     * 设置字体在本TextView里居中
     */
    public void setTextAlignCenter() {
        mPaint.setTextAlign(Paint.Align.CENTER);
        invalidate();
    }

    public void setPaddingBottom(int bottom) {
        mPadding[3] = bottom;
        requestLayout();
        invalidate();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        mPadding[0] = left;
        mPadding[1] = top;
        mPadding[2] = right;
        mPadding[3] = bottom;
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int width = (int) mPaint.measureText(mText);
        Rect rect = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), rect);
        mSingleBaseLineHeight = rect.height() - rect.bottom;
        mSingleLineHeight = rect.height();
        mSpliteTexts = autoSplit(mText, mPaint, width < widthSpecSize ? width : widthSpecSize);
        if (mSpliteTexts.size() <= 1) {
            switch (widthSpecMode) {
                case MeasureSpec.AT_MOST:
                    mViewWidth = width;
                    break;
                case MeasureSpec.EXACTLY:
                    mViewWidth = widthSpecSize;
                    break;
            }
            mViewHeight = mSingleLineHeight;
        } else {
            mViewWidth = widthSpecSize;
            mViewHeight = (int) ((mSingleLineHeight * mSpacingmult + mSpacingadd) * mSpliteTexts.size() - mSingleLineHeight * (mSpacingmult - 1) - mSpacingadd);
        }

        setMeasuredDimension(mViewWidth + mPadding[0] + mPadding[2], mViewHeight + mPadding[1] + mPadding[3]);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mPadding[0], mPadding[1]);
        int width = 0;
        if (mPaint.getTextAlign() == Paint.Align.CENTER) {
            width = (int) (mViewWidth * 1F / 2);
        }
        int height = mSingleBaseLineHeight;
        int spacing = (int) (mSingleLineHeight * (mSpacingmult - 1) + mSpacingadd);
        if (mSpliteTexts != null) {
            for (String line : mSpliteTexts) {
                canvas.drawText(line, width, height, mPaint);
                height += mSingleLineHeight + spacing;
            }
        }
    }

    @Override
    public int getBaseline() {
        return mViewHeight + mPadding[1];
    }

    private List<String> autoSplit(String text, Paint paint, float width) {
        long time1 = System.currentTimeMillis();

        int length = text.length();
        float textWidth = paint.measureText(text);
        List<String> texts = new ArrayList<>();
        if (textWidth <= width) {
            texts.add(text);
            return texts;
        }

        int start = 0;
        int end = 1;
        while (start < length) {
            if (paint.measureText(text, start, end) > width) { //文本宽度超出控件宽度时
                texts.add((String) text.subSequence(start, end - 1));
                start = end - 1;
            }
            if (end == length) { //不足一行的文本
                texts.add((String) text.subSequence(start, length));
                break;
            }
            end++;
        }

        return texts;
    }
}