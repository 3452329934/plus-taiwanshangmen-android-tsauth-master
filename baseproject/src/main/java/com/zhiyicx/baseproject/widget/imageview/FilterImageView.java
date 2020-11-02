package com.zhiyicx.baseproject.widget.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.zhiyicx.baseproject.R;
import com.zhiyicx.common.utils.SkinUtils;

import java.util.Arrays;


/**
 * @Describe show #mPressedColor color when pressed
 * @Author Jungle68
 * @Date 2017/2/29
 * @Contact master.jungle68@gmail.com
 */
public class FilterImageView extends android.support.v7.widget.AppCompatImageView {
    public static final int SHAPE_MODE_RECT = 0;
    public static final int SHAPE_MODE_ROUND_RECT = 1;
    public static final int SHAPE_MODE_SQUARE = 2;
    public static final int SHAPE_MODE_CIRLCE = 3;
    /**
     * cover：#000000 alpha 15%
     */
    private static final int DEFAULT_PRESSED_COLOR = 0x26000000;
    /**
     * pressed color
     */
    private boolean mNeedPressColor = false;
    private int mPressedColor = DEFAULT_PRESSED_COLOR;
    private int mShapeMode = SHAPE_MODE_RECT;


    private float mRadius = 0;
    private int mStrokeColor = 0x26000000;
    private float mStrokeWidth = 0;
    private boolean mShapeChanged;

    private Path mPath;
    private Shape mShape, mStrokeShape;
    private Paint mPaint, mStrokePaint, mPathPaint;
    private Bitmap mShapeBitmap, mStrokeBitmap;

    private PathExtension mExtension;

    private PorterDuffXfermode DST_IN = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private PorterDuffXfermode DST_OUT = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);


    private boolean isText;

    private Bitmap mLongImageBitmap;
    private Bitmap mGifImageBitmap;

    private boolean mIshowLongTag;
    private boolean mIshowGifTag;
    private boolean mIsLoaded;

    private Paint mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);

    public FilterImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }


    public FilterImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterImageView(Context context) {
        this(context, null);
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        setLayerType(LAYER_TYPE_HARDWARE, null);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FilterImageView);
        if (attrs != null) {
            mPressedColor = array.getInteger(R.styleable.FilterImageView_pressColor, DEFAULT_PRESSED_COLOR);
            mNeedPressColor = array.getBoolean(R.styleable.FilterImageView_needPressColor, false);
            mShapeMode = array.getInteger(R.styleable.FilterImageView_pressShape, SHAPE_MODE_RECT);

            mRadius = array.getDimension(R.styleable.FilterImageView_round_radius, 0);

            mStrokeWidth = array.getDimension(R.styleable.FilterImageView_stroke_width, 0);
            mStrokeColor = array.getColor(R.styleable.FilterImageView_stroke_color, mStrokeColor);
            array.recycle();
        }
        if (mNeedPressColor) {
            mPaint = new TextPaint();
            mPaint.setColor(mPressedColor);
            mPaint.setAntiAlias(true);
        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setFilterBitmap(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setXfermode(DST_IN);



        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setFilterBitmap(true);
        mStrokePaint.setColor(Color.BLACK);

        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathPaint.setFilterBitmap(true);
        mPathPaint.setColor(Color.BLACK);
        mPathPaint.setXfermode(DST_OUT);

        mPath = new Path();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed || mShapeChanged) {
            mShapeChanged = false;

            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            if (mShapeMode == SHAPE_MODE_CIRLCE) {
                int min = Math.min(width, height);
                mRadius = (float) min / 2;
            }

            if (mShape == null || mRadius != 0) {
                float[] radius = new float[8];
                Arrays.fill(radius, mRadius);
                mShape = new RoundRectShape(radius, null, null);
                mStrokeShape = new RoundRectShape(radius, null, null);
            }
            mShape.resize(width, height);
            mStrokeShape.resize(width - mStrokeWidth * 2, height - mStrokeWidth * 2);

            makeStrokeBitmap();
            makeShapeBitmap();

            if (mExtension != null) {
                mExtension.onLayout(mPath, width, height);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        if (isText) {
            mPaint.setColor(SkinUtils.getColor(R.color.general_for_hint));
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);
            mPaint.setTextSize(getWidth() / 2);
            mPaint.setColor(Color.WHITE);
            canvas.drawText("匿", getWidth() / 2 - mPaint.measureText("匿") / 2, getHeight() / 2 - (mPaint.descent() + mPaint.ascent()) / 2, mPaint);
        }
        if (mIshowLongTag) {
            if (mLongImageBitmap == null || mLongImageBitmap.isRecycled()) {
                mLongImageBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_longpic);
            }
            canvas.drawBitmap(mLongImageBitmap, getWidth() - mLongImageBitmap.getWidth(), getHeight() - mLongImageBitmap.getHeight(), null);
        }

        if (mIshowGifTag) {
            if (mGifImageBitmap == null || mGifImageBitmap.isRecycled()) {
                mGifImageBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_gif);
            }
            canvas.drawBitmap(mGifImageBitmap, getWidth() - mGifImageBitmap.getWidth(), getHeight() - mGifImageBitmap.getHeight(), null);
        }

        if (isPressed() && mNeedPressColor) {
            switch (mShapeMode) {
                // square
                case SHAPE_MODE_SQUARE:
                    canvas.drawColor(mPressedColor);
                    break;
                // circle
                case SHAPE_MODE_CIRLCE:
                    canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);
                    break;
                default:

            }
        }

        if (mStrokeWidth > 0 && mStrokeShape != null) {
            if (mStrokeBitmap == null || mStrokeBitmap.isRecycled()) {
                makeStrokeBitmap();
            }
            int i = canvas.saveLayer(0, 0, getMeasuredWidth(), getMeasuredHeight(), null, Canvas.ALL_SAVE_FLAG);
            mStrokePaint.setXfermode(null);
            canvas.drawBitmap(mStrokeBitmap, 0, 0, mStrokePaint);
            canvas.translate(mStrokeWidth, mStrokeWidth);
            mStrokePaint.setXfermode(DST_OUT);
            mStrokeShape.draw(canvas, mStrokePaint);
            canvas.restoreToCount(i);
        }

        if (mExtension != null) {
            canvas.drawPath(mPath, mPathPaint);
        }

        switch (mShapeMode) {
            case SHAPE_MODE_ROUND_RECT:
            case SHAPE_MODE_CIRLCE:
            default:
                if (mShapeBitmap == null || mShapeBitmap.isRecycled()) {
                    makeShapeBitmap();
                }
                canvas.drawBitmap(mShapeBitmap, 0, 0, mPaint);
        }
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        super.dispatchSetPressed(pressed);
        if (mNeedPressColor) {
            invalidate();
        }
    }

    public void setIsText(boolean isText) {
        this.isText = isText;
        postInvalidate();
    }

    /**
     * @param ishowGifTag 是否显示 gif 标识
     */
    public void setIshowGifTag(boolean ishowGifTag) {
        mIshowGifTag = ishowGifTag;
        if (ishowGifTag) {
            if (mGifImageBitmap == null) {
                mGifImageBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_gif);
            }
            postInvalidate();
        }
    }

    public boolean isLoaded() {
        return mIsLoaded;
    }

    public void setLoaded(boolean loaded) {
        mIsLoaded = loaded;
    }

    public void hideGifTag() {
        if (mIshowGifTag) {
            mGifImageBitmap = null;
            postInvalidate();
        }
    }

    public boolean isIshowGifTag() {
        return mIshowGifTag;
    }

    /**
     * @param isShow 是否显示长图标识 ，gif 标识优先于 长图标识显示
     */
    public void showLongImageTag(boolean isShow) {
        this.mIshowLongTag = isShow;
        if (!mIshowGifTag && isShow) {
            if (mLongImageBitmap == null) {
                mLongImageBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_longpic);
            }
            postInvalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseBitmap(mShapeBitmap);
        releaseBitmap(mStrokeBitmap);
        releaseBitmap(mLongImageBitmap);
        releaseBitmap(mGifImageBitmap);
    }

    private void makeStrokeBitmap() {
        if (mStrokeWidth <= 0) {
            return;
        }

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        if (w == 0 || h == 0) {
            return;
        }

        releaseBitmap(mStrokeBitmap);

        mStrokeBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(mStrokeBitmap);
        mPaint1.setColor(mStrokeColor);
        c.drawRect(new RectF(0, 0, w, h), mPaint1);
    }

    private void releaseBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    private void makeShapeBitmap() {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        if (w == 0 || h == 0) {
            return;
        }

        releaseBitmap(mShapeBitmap);

        mShapeBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(mShapeBitmap);
        mPaint1.setColor(Color.BLACK);
        mShape.draw(c, mPaint1);
    }

    public void setExtension(PathExtension extension) {
        mExtension = extension;
        requestLayout();
    }

    public void setStroke(int strokeColor, float strokeWidth) {
        if (mStrokeWidth <= 0) {
            return;
        }

        if (mStrokeWidth != strokeWidth) {
            mStrokeWidth = strokeWidth;

            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            mStrokeShape.resize(width - mStrokeWidth * 2, height - mStrokeWidth * 2);

            postInvalidate();
        }

        if (mStrokeColor != strokeColor) {
            mStrokeColor = strokeColor;

            makeStrokeBitmap();
            postInvalidate();
        }
    }

    public void setStrokeColor(int strokeColor) {
        setStroke(strokeColor, mStrokeWidth);
    }

    public void setStrokeWidth(float strokeWidth) {
        setStroke(mStrokeColor, strokeWidth);
    }

    public void setShape(int shapeMode, float radius) {
        mShapeChanged = mShapeMode != shapeMode || mRadius != radius;

        if (mShapeChanged) {
            mShapeMode = shapeMode;
            mRadius = radius;

            mShape = null;
            mStrokeShape = null;
            requestLayout();
        }
    }

    public void setShapeMode(int shapeMode) {
        setShape(shapeMode, mRadius);
    }

    public void setShapeRadius(float radius) {
        setShape(mShapeMode, radius);
    }

    public interface PathExtension {
        void onLayout(Path path, int width, int height);
    }
}
