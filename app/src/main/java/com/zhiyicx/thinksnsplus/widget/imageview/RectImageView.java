package com.zhiyicx.thinksnsplus.widget.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import com.zhiyicx.thinksnsplus.R;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/8/11
 * @Email Jliuer@aliyun.com
 * @Description 圆角图片，可添加阴影
 */
public class RectImageView extends AppCompatImageView {

    //save bundle state
    private static final String STATE_INSTANCE = "state_instance";
    private static final String STATE_TYPE = "state_type";
    private static final String STATE_BORDER_RADIUS = "state_border_radius";
    private static final String STATE_BORDER_WIDTH = "state_border_width";
    private static final String STATE_BORDER_COLOR = "state_border_color";

    private static final int TYPE_NORMAL = -1;
    private static final int TYPE_CIRCLE = 0;
    private static final int TYPE_ROUND = 1;
    private static final int BODER_RADIUS_DEFAULT = 5;
    private static final int SHADOW_RADIUS_DEFAULT = 3;
    private static final int BORDER_WIDTH = 0;
    private static final int SHADOW_WIDTH = 0;
    private static final int BORDER_COLOR = Color.BLACK;

    private int borderRadius;
    private int type;
    private int border_width;
    private int border_color;
    private int shadow_width;
    private int shadow_radius;
    private Paint paint;
    private Paint boder_paint;
    private Matrix matrix;
    private int width;
    private int radius;
    private BitmapShader bitmapShader;
    private RectF rectF;
    private RectF mRectFShadow;
    private Paint mPaintShadow;

    private Bitmap mLongImageBitmap;
    private Bitmap mGifImageBitmap;

    private boolean mIshowLongTag;
    private boolean mIshowGifTag;

    public RectImageView(Context context) {
        super(context);
    }

    public RectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inital(context, attrs);
    }

    private void inital(Context context, AttributeSet attrs) {
        matrix = new Matrix();
        paint = new Paint();
        boder_paint = new Paint();
        mPaintShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintShadow.setColor(Color.parseColor("#ededed"));
        paint.setColor(Color.parseColor("#ededed"));
        mPaintShadow.setDither(true);
//        setLayerType(LAYER_TYPE_SOFTWARE, null);
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.RectImageView);
        borderRadius = dp2px(array.getDimension(R.styleable.RectImageView_borderRadius, BODER_RADIUS_DEFAULT));
        type = array.getInt(R.styleable.RectImageView_type, TYPE_NORMAL);
        border_width = dp2px(array.getDimension(R.styleable.RectImageView_borderWidth, BORDER_WIDTH));
        shadow_width = dp2px(array.getDimension(R.styleable.RectImageView_shadowWidth, SHADOW_WIDTH));
        shadow_radius = dp2px(array.getDimension(R.styleable.RectImageView_shadowRadius, SHADOW_RADIUS_DEFAULT));
        border_color = array.getInt(R.styleable.RectImageView_borderColor, BORDER_COLOR);
        mPaintShadow.setShadowLayer(shadow_radius, 0, 0, Color.parseColor("#80000000"));
        setPadding(shadow_width, shadow_width, shadow_width, shadow_width);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (type == TYPE_CIRCLE) {
            width = Math.min(getMeasuredWidth(), getMeasuredHeight());
            radius = width / 2 - border_width / 2;
            setMeasuredDimension(width, width);
        }
    }


    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(w, h, config);
        } catch (Exception ignore) {
            return null;
        }
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    private void setBitmapShader() {
        if (matrix == null) {
            matrix = new Matrix();
        }
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        Bitmap bitmap = drawableToBitmap(drawable);
        if (bitmap == null) {
            return;
        }
        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        int viewwidth = getWidth();
        int viewheight = getHeight();
        int drawablewidth = bitmap.getWidth();
        int drawableheight = bitmap.getHeight();
        float dx = 0, dy = 0;

        float scale1 = 1.0f;
        float scale2 = 1.0f;
        final boolean fits = (drawablewidth < 0 || viewwidth == drawablewidth)
                && (drawableheight < 0 || viewheight == drawableheight);
        if (type == TYPE_CIRCLE) {
            int size = Math.min(drawablewidth, drawableheight);
            scale = width * 1.0f / size;
        } else if (type == TYPE_ROUND) {
            scale = Math.max(viewwidth * 1.0f / drawablewidth, viewheight
                    * 1.0f / drawableheight);
        } else {
            return;
        }

        if (drawablewidth <= 0 || drawableheight <= 0) {
            drawable.setBounds(0, 0, viewwidth, viewheight);
            matrix = null;
        } else {
            drawable.setBounds(0, 0, drawablewidth, drawableheight);
            if (ScaleType.MATRIX == getScaleType()) {
                if (matrix.isIdentity()) {
                    matrix = null;
                }
            } else if (fits) {
                matrix = null;
            } else if (ScaleType.CENTER == getScaleType()) {
                matrix.setTranslate(Math.round((viewwidth - drawablewidth) * 0.5f),
                        Math.round((viewheight - drawableheight) * 0.5f));
            } else if (ScaleType.CENTER_CROP == getScaleType()) {
                if (drawablewidth * viewheight > viewwidth * drawableheight) {
                    dx = (viewwidth - drawablewidth * scale) * 0.5f;
                } else {
                    dy = (viewheight - drawableheight * scale) * 0.5f;
                }
                matrix.setScale(scale, scale);
                matrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
            } else if (ScaleType.CENTER_INSIDE == getScaleType()) {

                if (drawablewidth <= viewwidth && drawableheight <= viewheight) {
                    scale = 1.0f;
                } else {
                    scale = Math.min((float) viewwidth / (float) drawablewidth,
                            (float) viewheight / (float) drawableheight);
                }
                dx = Math.round((viewwidth - drawablewidth * scale) * 0.5f);
                dy = Math.round((viewheight - drawableheight * scale) * 0.5f);
                matrix.setScale(scale, scale);
                matrix.postTranslate(dx, dy);
            } else {
                if (drawablewidth * viewheight > viewwidth * drawableheight) {
                    dx = (viewwidth - drawablewidth * scale) * 0.5f;
                } else {
                    dy = (viewheight - drawableheight * scale) * 0.5f;
                }
                matrix.setScale(scale, scale);
                matrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
            }
        }
        if (ScaleType.FIT_XY == getScaleType() && matrix != null) {
            scale1 = viewwidth * 1.0f / drawablewidth;
            scale2 = viewheight * 1.0f / drawableheight;
            matrix.setScale(scale1, scale2);
        }
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        paint.setAntiAlias(true);
        boder_paint.setAntiAlias(true);
        boder_paint.setStyle(Paint.Style.STROKE);
        boder_paint.setColor(border_color);
        boder_paint.setStrokeWidth(border_width);
        setBitmapShader();
        if (type == TYPE_ROUND) {
            if (shadow_width > 0) {
                canvas.drawRoundRect(mRectFShadow, borderRadius, borderRadius, mPaintShadow);
            }
            canvas.drawRoundRect(rectF, borderRadius, borderRadius,
                    paint);
            if (border_width > 0) {
                canvas.drawRoundRect(rectF, borderRadius, borderRadius,
                        boder_paint);
            }
        } else if (type == TYPE_CIRCLE) {
            canvas.drawCircle(radius, radius, radius, paint);
            if (border_width > 0) {
                canvas.drawCircle(radius, radius, radius - border_width / 2, boder_paint);
            }
        } else {
            getDrawable().draw(canvas);
        }

        if (mIshowLongTag && mLongImageBitmap != null) {
            canvas.drawBitmap(mLongImageBitmap, getWidth() - mLongImageBitmap.getWidth(), getHeight() - mLongImageBitmap.getHeight(), null);
        }

        if (mIshowGifTag && mGifImageBitmap != null) {
            canvas.drawBitmap(mGifImageBitmap, getWidth() - mGifImageBitmap.getWidth(), getHeight() - mGifImageBitmap.getHeight(), null);
        }
    }

    public void setBorder_width(int border_width) {
        int px = dp2px(border_width);
        if (this.border_width != px) {
            this.border_width = px;
            invalidate();
        }
    }

    public void setBorder_color(int border_color) {
        if (this.border_color == border_color) {
            return;
        }
        this.border_color = border_color;
        boder_paint.setColor(border_color);
        invalidate();
    }

    public void setBorderRadius(int borderRadius) {
        int px = dp2px(borderRadius);
        if (this.borderRadius != px) {
            this.borderRadius = px;
            invalidate();
        }
    }

    public void setType(int type) {
        if (this.type != type) {
            this.type = type;
            if (this.type != TYPE_ROUND && this.type != TYPE_CIRCLE) {
                this.type = TYPE_NORMAL;
            }
            requestLayout();
        }
    }

    public int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (type == TYPE_ROUND) {
            rectF = new RectF(border_width / 2, border_width / 2, getWidth() - border_width / 2, getHeight() - border_width / 2);
            mRectFShadow = new RectF(shadow_width, shadow_width, getWidth() - shadow_width, getHeight() - shadow_width);
            rectF.inset(shadow_width, shadow_width);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
        bundle.putInt(STATE_TYPE, type);
        bundle.putInt(STATE_BORDER_RADIUS, borderRadius);
        bundle.putInt(STATE_BORDER_WIDTH, border_width);
        bundle.putInt(STATE_BORDER_COLOR, border_color);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(((Bundle) state)
                    .getParcelable(STATE_INSTANCE));
            this.type = bundle.getInt(STATE_TYPE);
            this.borderRadius = bundle.getInt(STATE_BORDER_RADIUS);
            this.border_width = bundle.getInt(STATE_BORDER_WIDTH);
            this.border_color = bundle.getInt(STATE_BORDER_COLOR);
        } else {
            super.onRestoreInstanceState(state);
        }
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
}