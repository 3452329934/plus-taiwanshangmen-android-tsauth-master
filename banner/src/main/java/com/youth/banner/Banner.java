package com.youth.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youth.banner.listener.OnBannerClickListener;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoaderInterface;
import com.youth.banner.view.BannerViewPager;
import com.zhiyicx.baseproject.widget.imageview.FilterImageView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.view.ViewPager.OnPageChangeListener;
import static android.support.v4.view.ViewPager.PageTransformer;
import static com.youth.banner.BannerConfig.NUM_INDICATOR;
import static com.youth.banner.BannerConfig.NUM_INDICATOR_FOR_GOODS;
import static com.zhiyicx.baseproject.widget.imageview.FilterImageView.SHAPE_MODE_RECT;

public class Banner extends FrameLayout implements OnPageChangeListener, ImageLoaderInterface.OnImageSourceReady {
    public String tag = "banner";
    private int mIndicatorMargin = BannerConfig.PADDING_SIZE;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private int indicatorSize;
    private int bannerStyle = BannerConfig.CIRCLE_INDICATOR;
    private int delayTime = BannerConfig.TIME;
    private int scrollTime = BannerConfig.DURATION;
    private boolean isNeedLoop = BannerConfig.IS_NEED_LOOP;
    private boolean isAutoPlay = BannerConfig.IS_AUTO_PLAY;
    private boolean isNeedDownStopUpAutoPlay = BannerConfig.IS_DONW_STOP_UP_AUTO_PLAY;
    private boolean isDownStopAutoPlay = BannerConfig.IS_AUTO_PLAY;
    private boolean mIsBase64Image = BannerConfig.IS_BASE64_IMAGE;
    private boolean isScroll = BannerConfig.IS_SCROLL;
    private boolean isEnableScroll = BannerConfig.IS_ENABLE_SCROLL;
    private int mIndicatorSelectedResId = R.drawable.gray_radius;
    private int mIndicatorUnselectedResId = R.drawable.white_radius;
    private int titleHeight;
    private int titleBackground;
    private int titleTextColor;
    private int titleTextSize;
    private int count = 0;
    private int currentItem;
    private int gravity = -1;
    private int lastPosition = 1;
    private int scaleType = 1;
    private List<String> titles;
    private List imageUrls;
    private List<View> imageViews;
    private List<ImageView> indicatorImages;
    // 图片的形状
    private int mShapeMode = SHAPE_MODE_RECT;
    private float mRadius = 0;
    private Context context;
    private BannerViewPager viewPager;
    private TextView bannerTitle, numIndicatorInside, numIndicator, numIndicatorGoods;
    private LinearLayout indicator, indicatorInside, titleView;
    private ImageLoaderInterface imageLoader;
    private BannerPagerAdapter adapter;
    private OnPageChangeListener mOnPageChangeListener;
    private BannerScroller mScroller;
    private OnBannerClickListener bannerListener;
    private OnBannerListener listener;
    private OnBannerTimeListener mTimeListener;
    private DisplayMetrics dm;

    private ImageLoaderInterface.OnImageSourceReady onImageSourceReady;

    private boolean mSourceReadied = false;

    //是否新的一页
    private boolean isNewPage = false;

    //当前毫秒数
    private long currentSecond = 0;

    private WeakHandler handler = new WeakHandler();

    public Banner(Context context) {
        this(context, null);
    }

    public Banner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Banner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        titles = new ArrayList<>();
        imageUrls = new ArrayList<>();
        imageViews = new ArrayList<>();
        indicatorImages = new ArrayList<>();
        dm = context.getResources().getDisplayMetrics();
        indicatorSize = dm.widthPixels / 80;
        initView(context, attrs);
    }

    private void handleTypedArray(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Banner);
        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.Banner_indicator_width, indicatorSize);
        mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.Banner_indicator_height, indicatorSize);
        mIndicatorMargin = typedArray.getDimensionPixelSize(R.styleable.Banner_indicator_margin, BannerConfig.PADDING_SIZE);
        mIndicatorSelectedResId = typedArray.getResourceId(R.styleable.Banner_indicator_drawable_selected, R.drawable.gray_radius);
        mIndicatorUnselectedResId = typedArray.getResourceId(R.styleable.Banner_indicator_drawable_unselected, R.drawable.white_radius);
        scaleType = typedArray.getInt(R.styleable.Banner_image_scale_type, scaleType);
        delayTime = typedArray.getInt(R.styleable.Banner_delay_time, BannerConfig.TIME);
        scrollTime = typedArray.getInt(R.styleable.Banner_scroll_time, BannerConfig.DURATION);
        isNeedLoop = typedArray.getBoolean(R.styleable.Banner_is_need_loop, BannerConfig.IS_NEED_LOOP);
        isAutoPlay = typedArray.getBoolean(R.styleable.Banner_is_auto_play, BannerConfig.IS_AUTO_PLAY);
        isEnableScroll = typedArray.getBoolean(R.styleable.Banner_is_enable_scroll, BannerConfig.IS_ENABLE_SCROLL);
        titleBackground = typedArray.getResourceId(R.styleable.Banner_title_background, BannerConfig.TITLE_BACKGROUND);
        titleHeight = typedArray.getDimensionPixelSize(R.styleable.Banner_title_height, BannerConfig.TITLE_HEIGHT);
        titleTextColor = typedArray.getColor(R.styleable.Banner_title_textcolor, BannerConfig.TITLE_TEXT_COLOR);
        titleTextSize = typedArray.getDimensionPixelSize(R.styleable.Banner_title_textsize, BannerConfig.TITLE_TEXT_SIZE);
        typedArray.recycle();
    }


    private void initView(Context context, AttributeSet attrs) {
        imageViews.clear();
        View view = LayoutInflater.from(context).inflate(R.layout.banner, this, true);
        viewPager = (BannerViewPager) view.findViewById(R.id.bannerViewPager);
        titleView = (LinearLayout) view.findViewById(R.id.titleView);
        indicator = (LinearLayout) view.findViewById(R.id.circleIndicator);
        indicatorInside = (LinearLayout) view.findViewById(R.id.indicatorInside);
        bannerTitle = (TextView) view.findViewById(R.id.bannerTitle);
        numIndicator = (TextView) view.findViewById(R.id.numIndicator);
        numIndicatorInside = (TextView) view.findViewById(R.id.numIndicatorInside);
        numIndicatorGoods = view.findViewById(R.id.numIndicator_goods);
        handleTypedArray(context, attrs);
        initViewPagerScroll();
    }

    private void initViewPagerScroll() {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mScroller = new BannerScroller(viewPager.getContext());
            mScroller.setDuration(scrollTime);
            mField.set(viewPager, mScroller);
        } catch (Exception e) {
            Log.e(tag, e.getMessage());
        }
    }

    public Banner isNeedLoop(boolean isNeedLoop) {
        this.isNeedLoop = isNeedLoop;
        return this;
    }

    public Banner isAutoPlay(boolean isAutoPlay) {
        this.isAutoPlay = isAutoPlay;
        return this;
    }

    public Banner isNeedDownStopUpAutoPlay(boolean isNeedDownStopUpAutoPlay) {
        this.isNeedDownStopUpAutoPlay = isNeedDownStopUpAutoPlay;
        return this;
    }

    public Banner isDownStopAutoPlay(boolean isDownStopAutoPlay) {
        this.isDownStopAutoPlay = isDownStopAutoPlay;
        return this;
    }

    public void isBase64Image(boolean base64Image) {
        mIsBase64Image = base64Image;
    }

    public Banner setImageLoader(ImageLoaderInterface imageLoader) {
        this.imageLoader = imageLoader;
        return this;
    }

    public Banner setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
        return this;
    }

    public Banner setTitleTextSize(int titleTextSize) {
        this.titleTextSize = titleTextSize;
        return this;
    }

    public Banner setDelayTime(int delayTime) {
        this.delayTime = delayTime;
        currentSecond = 0;
        return this;
    }

    public Banner setScrollTime(int scrollTime) {
        this.scrollTime = scrollTime;
        return this;
    }

    public Banner setIndicatorGravity(int type) {
        switch (type) {
            case BannerConfig.LEFT:
                this.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                break;
            case BannerConfig.CENTER:
                this.gravity = Gravity.CENTER;
                break;
            case BannerConfig.RIGHT:
                this.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                break;
            default:
                break;
        }
        return this;
    }

    public Banner setBannerAnimation(Class<? extends PageTransformer> transformer) {
        try {
            setPageTransformer(true, transformer.newInstance());
        } catch (Exception e) {
            Log.e(tag, "Please set the PageTransformer class");
        }
        return this;
    }

    /**
     * Set the number of pages that should be retained to either side of the
     * current page in the view hierarchy in an idle state. Pages beyond this
     * limit will be recreated from the adapter when needed.
     *
     * @param limit How many pages will be kept offscreen in an idle state.
     * @return Banner
     */
    public Banner setOffscreenPageLimit(int limit) {
        if (viewPager != null) {
            viewPager.setOffscreenPageLimit(limit);
        }
        return this;
    }

    /**
     * Set a {@link PageTransformer} that will be called for each attached page whenever
     * the scroll position is changed. This allows the application to apply custom property
     * transformations to each page, overriding the default sliding look and feel.
     *
     * @param reverseDrawingOrder true if the supplied PageTransformer requires page views
     *                            to be drawn from last to first instead of first to last.
     * @param transformer         PageTransformer that will modify each page's animation properties
     * @return Banner
     */
    public Banner setPageTransformer(boolean reverseDrawingOrder, PageTransformer transformer) {
        viewPager.setPageTransformer(reverseDrawingOrder, transformer);
        return this;
    }

    public Banner setBannerTitles(List<String> titles) {
        this.titles = titles;
        return this;
    }

    public Banner setBannerStyle(int bannerStyle) {
        this.bannerStyle = bannerStyle;
        return this;
    }

    public Banner setViewPagerIsScroll(boolean isScroll) {
        this.isScroll = isScroll;
        return this;
    }

    public Banner setImages(List<?> imageUrls) {
        this.imageUrls = imageUrls;
        this.count = imageUrls.size();
        return this;
    }

    public Banner addImages(String imageUrl) {
        this.imageUrls.add(imageUrl);
        this.count = imageUrls.size();
        return this;
    }

    public void update(List<?> imageUrls, List<String> titles) {
        this.imageUrls.clear();
        this.titles.clear();
        this.imageUrls.addAll(imageUrls);
        this.titles.addAll(titles);
        this.count = this.imageUrls.size();
        start();
    }

    public void update(List<?> imageUrls) {
        this.imageUrls.clear();
        this.imageUrls.addAll(imageUrls);
        this.count = this.imageUrls.size();
        start();
    }

    public void updateBannerStyle(int bannerStyle) {
        indicator.setVisibility(GONE);
        numIndicator.setVisibility(GONE);
        numIndicatorInside.setVisibility(GONE);
        indicatorInside.setVisibility(GONE);
        bannerTitle.setVisibility(View.GONE);
        titleView.setVisibility(View.GONE);
        this.bannerStyle = bannerStyle;
        start();
    }

    public Banner start() {
        setBannerStyleUI();
        setImageList(imageUrls);
        setData();
        return this;
    }

    private void setTitleStyleUI() {

        if (titleBackground != -1) {
            titleView.setBackgroundResource(titleBackground);
//            titleView.setBackgroundColor(titleBackground);
        }
        if (titleHeight != -1) {
            titleView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleHeight));
        }
        if (titleTextColor != -1) {
            bannerTitle.setTextColor(titleTextColor);
        }
        if (titleTextSize != -1) {
            bannerTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        }
        if (titles != null && titles.size() > 0) {
            bannerTitle.setText(titles.get(0));
            bannerTitle.setVisibility(View.VISIBLE);
            titleView.setVisibility(View.VISIBLE);
        }
    }

    private void setBannerStyleUI() {
        int visibility;
        if (count > 1) {
            visibility = View.VISIBLE;
        } else {
            visibility = View.GONE;
        }
        switch (bannerStyle) {
            case BannerConfig.CIRCLE_INDICATOR:
                indicator.setVisibility(visibility);
                break;
            case NUM_INDICATOR_FOR_GOODS:
                numIndicatorGoods.setVisibility(visibility);
                break;
            case NUM_INDICATOR:
                numIndicator.setVisibility(visibility);
                break;
            case BannerConfig.NUM_INDICATOR_TITLE:
                numIndicatorInside.setVisibility(visibility);
                setTitleStyleUI();
                break;
            case BannerConfig.CIRCLE_INDICATOR_TITLE:
                indicator.setVisibility(visibility);
                setTitleStyleUI();
                break;
            case BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE:
                indicatorInside.setVisibility(visibility);
                setTitleStyleUI();
                break;
            default:
        }
    }

    private void initImages() {
        imageViews.clear();
        if (bannerStyle == BannerConfig.CIRCLE_INDICATOR ||
                bannerStyle == BannerConfig.CIRCLE_INDICATOR_TITLE ||
                bannerStyle == BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE) {
            createIndicator();
        } else if (bannerStyle == BannerConfig.NUM_INDICATOR_TITLE) {
            numIndicatorInside.setText("1/" + count);
        } else if (bannerStyle == NUM_INDICATOR) {
            numIndicator.setText("1/" + count);
        } else if (bannerStyle == NUM_INDICATOR_FOR_GOODS) {
            numIndicatorGoods.setText("1/" + count);
        }
    }

    private void setImageList(List<?> imagesUrl) {
        if (imagesUrl == null || imagesUrl.size() <= 0) {
            Log.e(tag, "Please set the images data.");
            return;
        }
        initImages();
        if (isNeedLoop) {
            for (int i = 0; i <= count + 1; i++) {
                View imageView = null;
                if (imageLoader != null) {
                    imageView = imageLoader.createImageView(context);
                    if (imageView instanceof FilterImageView) {
                        ((FilterImageView) imageView).setShape(mShapeMode, mRadius);
                    }

                }
                if (imageView == null) {
                    imageView = new FilterImageView(context);
                    ((FilterImageView) imageView).setShape(mShapeMode, mRadius);
                }

                setScaleType(imageView);
                Object url;
                // 增加这个的目的是为了能循环滚动
                if (i == 0) {
                    url = imagesUrl.get(count - 1);
                } else if (i == count + 1) {
                    url = imagesUrl.get(0);
                } else {
                    url = imagesUrl.get(i - 1);
                }
                imageViews.add(imageView);
                if (imageLoader != null) {
                    if (mIsBase64Image) {
                        imageLoader.displayImage(context, (String) url, imageView);
                    } else {
                        imageLoader.displayImage(context, url, imageView, i == 0 ? this : null);
                    }
                } else {
                    Log.e(tag, "Please set images loader.");
                }
            }
        } else {
            //不循环
            for (int i = 0; i < count; i++) {
                View imageView = null;
                if (imageLoader != null) {
                    imageView = imageLoader.createImageView(context);
                    if (imageView instanceof FilterImageView) {
                        ((FilterImageView) imageView).setShape(mShapeMode, mRadius);
                    }
                }
                if (imageView == null) {
                    imageView = new FilterImageView(context);
                    ((FilterImageView) imageView).setShape(mShapeMode, mRadius);
                }
                setScaleType(imageView);
                Object url;
                url = imagesUrl.get(i);
                imageViews.add(imageView);
                if (imageLoader != null) {
                    if (mIsBase64Image) {
                        imageLoader.displayImage(context, (String) url, imageView);
                    } else {
                        imageLoader.displayImage(context, url, imageView, i == 0 ? this : null);
                    }
                } else {
                    Log.e(tag, "Please set images loader.");
                }
            }
        }

    }

    private void setScaleType(View imageView) {
        if (imageView instanceof ImageView) {
            ImageView view = ((ImageView) imageView);
            switch (scaleType) {
                case 0:
                    view.setScaleType(ScaleType.CENTER);
                    break;
                case 1:
                    view.setScaleType(ScaleType.CENTER_CROP);
                    break;
                case 2:
                    view.setScaleType(ScaleType.CENTER_INSIDE);
                    break;
                case 3:
                    view.setScaleType(ScaleType.FIT_CENTER);
                    break;
                case 4:
                    view.setScaleType(ScaleType.FIT_END);
                    break;
                case 5:
                    view.setScaleType(ScaleType.FIT_START);
                    break;
                case 6:
                    view.setScaleType(ScaleType.FIT_XY);
                    break;
                case 7:
                    view.setScaleType(ScaleType.MATRIX);
                    break;
                default:
            }

        }
    }

    private void createIndicator() {
        indicatorImages.clear();
        indicator.removeAllViews();
        indicatorInside.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mIndicatorWidth, mIndicatorHeight);
            params.leftMargin = mIndicatorMargin;
            params.rightMargin = mIndicatorMargin;
            if (i == 0) {
                imageView.setImageResource(mIndicatorUnselectedResId);
            } else {
                imageView.setImageResource(mIndicatorSelectedResId);
            }

            indicatorImages.add(imageView);
            if (bannerStyle == BannerConfig.CIRCLE_INDICATOR ||
                    bannerStyle == BannerConfig.CIRCLE_INDICATOR_TITLE) {
                indicator.addView(imageView, params);
            } else if (bannerStyle == BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE) {
                indicatorInside.addView(imageView, params);
            }
        }
    }

    public void setShape(int shapeMode, float radius) {
        this.mShapeMode = shapeMode;
        this.mRadius = radius;
    }


    private void setData() {
        currentItem = 1;
        if (adapter == null) {
            adapter = new BannerPagerAdapter();

            viewPager.addOnPageChangeListener(this);
        }
        viewPager.setAdapter(adapter);
        viewPager.setFocusable(true);
        if (isNeedLoop) {
            viewPager.setCurrentItem(1);
        } else {
            viewPager.setCurrentItem(0);
        }
        if (gravity != -1) {
            indicator.setGravity(gravity);
        }
        if (isScroll && count > 1) {
            viewPager.setScrollable(true);
        } else {
            viewPager.setScrollable(false);
        }
        if (isAutoPlay && mSourceReadied) {
            startAutoPlay();
        }
    }


    public void startAutoPlay() {
        isAutoPlay = true;
        handler.removeCallbacks(task);
        handler.removeCallbacks(timmer);
        handler.post(timmer);
        handler.postDelayed(task, delayTime);
    }

    public void stopAutoPlay() {
        isAutoPlay = false;
        handler.removeCallbacks(task);
    }


    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (count > 1 && isAutoPlay) {
                currentItem = currentItem % (count + 1) + 1;
                if (currentItem == 1) {
                    viewPager.setCurrentItem(currentItem, false);
                    handler.post(task);
                } else {
                    viewPager.setCurrentItem(currentItem);
                    handler.postDelayed(task, delayTime);
                }
            }
        }
    };

    private final Runnable timmer = new Runnable() {
        String skip = "跳过 ";

        @Override
        public synchronized void run() {
            if (isNewPage) {
                currentSecond = 0;
                isNewPage = false;
            }
            if (mTimeListener != null) {
                long checkTime = (delayTime - currentSecond) / 1000;
                String time = checkTime + "s";
                if (checkTime <= 0) {
                    if (mTimeListener != null) {
                        mTimeListener.onFinish();
                    }
                    handler.removeCallbacks(this);
                }
                if (mTimeListener != null) {
                    // 第一个广告不可跳过
                    mTimeListener.onTimeTick(getCurrentItem() == 0 ? time : skip + time);
                }
            } else {
                handler.removeCallbacks(this);
                return;
            }
            currentSecond += 1000;
            if (delayTime == currentSecond && getCurrentItem() == imageUrls.size() - 1) {
                if (mTimeListener != null) {
                    mTimeListener.onFinish();
                }
                handler.removeCallbacks(this);
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.i(tag, ev.getAction() + "--" + isAutoPlay);
        if (isNeedDownStopUpAutoPlay) {
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_OUTSIDE) {
                startAutoPlay();
            } else if (action == MotionEvent.ACTION_DOWN) {
                if (isDownStopAutoPlay) {
                    stopAutoPlay();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 返回真实的位置
     *
     * @param position
     * @return 下标从0开始
     */
    public int toRealPosition(int position) {
        int realPosition = (position - 1) % count;
        if (realPosition < 0) {
            realPosition += count;
        }
        return realPosition;
    }

    /**
     * banner 图片资源加载准备完成
     *
     * @param loadStatues
     */
    @Override
    public void onImageSourceRead(boolean loadStatues) {
        mSourceReadied = true;
        if (onImageSourceReady != null) {
            onImageSourceReady.onImageSourceRead(loadStatues);
        }
        if (isAutoPlay) {
            startAutoPlay();
        }
    }

    class BannerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            container.addView(imageViews.get(position));
            View view = imageViews.get(position);
            if (bannerListener != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(tag, "你正在使用旧版点击事件接口，下标是从1开始，" +
                                "为了体验请更换为setOnBannerListener，下标从0开始计算");
                        bannerListener.OnBannerClick(position);
                    }
                });
            }
            if (listener != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isNeedLoop) {
                            listener.onBannerClick(toRealPosition(position));
                        } else {
                            listener.onBannerClick(position);
                        }
                    }
                });
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (isNeedLoop) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
            currentItem = viewPager.getCurrentItem();
            switch (state) {
                case 0://No operation
                    if (currentItem == 0) {
                        viewPager.setCurrentItem(count, false);
                    } else if (currentItem == count + 1) {
                        viewPager.setCurrentItem(1, false);
                    }
                    break;
                case 1://start Sliding
                    if (currentItem == count + 1) {
                        viewPager.setCurrentItem(1, false);
                    } else if (currentItem == 0) {
                        viewPager.setCurrentItem(count, false);
                    }
                    break;
                case 2://end Sliding
                    break;
                default:
            }
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        isNewPage = true;
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
        if (bannerStyle == BannerConfig.CIRCLE_INDICATOR ||
                bannerStyle == BannerConfig.CIRCLE_INDICATOR_TITLE ||
                bannerStyle == BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE) {
            indicatorImages.get((lastPosition - 1 + count) % count).setImageResource(mIndicatorSelectedResId);
            indicatorImages.get((position - 1 + count) % count).setImageResource(mIndicatorUnselectedResId);
            lastPosition = position;
        }
        //需要循环时，才重置position，其他情况正常翻页
        if (isNeedLoop) {
            if (position == 0) {
                position = count;
            }
            if (position > count) {
                position = 1;
            }
        } else {
            position = position + 1;
        }
        switch (bannerStyle) {
            case BannerConfig.CIRCLE_INDICATOR:
                break;
            case NUM_INDICATOR_FOR_GOODS:
                numIndicatorGoods.setText(position + "/" + count);
                break;
            case NUM_INDICATOR:
                numIndicator.setText(position + "/" + count);
                break;
            case BannerConfig.NUM_INDICATOR_TITLE:
                numIndicatorInside.setText(position + "/" + count);
                bannerTitle.setText(titles.get(position - 1));
                break;
            case BannerConfig.CIRCLE_INDICATOR_TITLE:
                bannerTitle.setText(titles.get(position - 1));
                break;
            case BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE:
                bannerTitle.setText(titles.get(position - 1));
                break;
            default:
        }

    }

    /**
     * 监听图片加载成功或失败回调
     *
     * @param l
     */
    public void setOnImageSourceReady(ImageLoaderInterface.OnImageSourceReady l) {
        onImageSourceReady = l;
    }

    @Deprecated
    public Banner setOnBannerClickListener(OnBannerClickListener listener) {
        this.bannerListener = listener;
        return this;
    }

    /**
     * 废弃了旧版接口，新版的接口下标是从1开始，同时解决下标越界问题
     *
     * @param listener
     * @return
     */
    public Banner setOnBannerListener(OnBannerListener listener) {
        this.listener = listener;
        return this;
    }

    public Banner setTimeListener(OnBannerTimeListener timeListener) {
        mTimeListener = timeListener;
        return this;
    }

    public interface OnBannerTimeListener {
        void onTimeTick(String time);

        void onFinish();
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    public void releaseBanner() {
        handler.removeCallbacks(task);
        handler.removeCallbacks(timmer);
        handler.removeCallbacksAndMessages(null);
    }

    public int getCurrentItem() {
        return toRealPosition(currentItem);
    }

    public int getItemCount() {
        return imageUrls.size();
    }

    public List getImageUrls() {
        return imageUrls;
    }

    public List<ImageView> getIndicatorImages() {
        return indicatorImages;
    }
}
