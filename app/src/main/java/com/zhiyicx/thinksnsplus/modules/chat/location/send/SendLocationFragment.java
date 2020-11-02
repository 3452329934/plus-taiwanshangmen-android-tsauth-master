package com.zhiyicx.thinksnsplus.modules.chat.location.send;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.jakewharton.rxbinding.view.RxView;
import com.nineoldandroids.animation.ObjectAnimator;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.common.utils.ToastUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.common.utils.recycleviewdecoration.CustomLinearDecoration;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.ChatLocationBean;
import com.zhiyicx.thinksnsplus.modules.chat.location.search.SearchLocationActivity;
import com.zhiyicx.thinksnsplus.modules.circle.create.location.CircleLocationFragment;
import com.zhiyicx.thinksnsplus.widget.EnableCheckBox;
import com.zhiyicx.thinksnsplus.widget.TSSearchView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Author Jliuer
 * @Date 2018/06/12/15:29
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SendLocationFragment extends TSListFragment<SendLocationContract.Presenter, ChatLocationBean>
        implements SendLocationContract.View, AMap.OnMyLocationChangeListener, PoiSearch.OnPoiSearchListener,
        AMap.OnMapClickListener, AMap.OnCameraChangeListener, GeocodeSearch.OnGeocodeSearchListener {

    public static final String LOCATION_DATA = "";
    public static final String USER_NAME = "user_name";
    public static final int LOCATION_ZOOM = 18;
    public static final int REQUST_CODE_AREA = 1;

    @BindView(R.id.mv_map)
    MapView mMapView;
    @BindView(R.id.searchView)
    TSSearchView mSearchView;
    @BindView(R.id.iv_location)
    ImageView mLocationView;
    @BindView(R.id.iv_mark)
    ImageView mMarkView;

    private AMap aMap;

    private double mLatitude;
    private double mLongitude;
    private String mAddress;

    private LatLng mFinalChoosePosition;
    private GeocodeSearch geocoderSearch;
    private boolean isHandDrag;
    private boolean isFirstLoadList = true;
    private boolean isBackFromSearchChoose;

    private ChatLocationBean checkData;

    private String userName = "未知用户";
    private MyLocationStyle myLocationStyle;

    public static SendLocationFragment newInstance(Bundle bundle) {
        SendLocationFragment sendLocationFragment = new SendLocationFragment();
        sendLocationFragment.setArguments(bundle);
        return sendLocationFragment;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mToolbarRight.setEnabled(false);
    }

    @Override
    protected void initData() {
        super.initData();
        initAmap();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView.onCreate(savedInstanceState);
    }

    @Override
    protected String setCenterTitle() {
        return userName = getArguments() != null ? getArguments().getString(USER_NAME, "未知用户") : userName;
    }

    @Override
    protected String setRightTitle() {
        return getString(R.string.send);
    }

    @Override
    protected boolean isRefreshEnable() {
        return false;
    }

    @Override
    protected boolean setUseCenterLoading() {
        return true;
    }

    @Override
    protected void requestNetData(Long maxId, boolean isLoadMore) {
        super.requestNetData(maxId, isLoadMore);
        if (isLoadMore) {
            searchPoi();
        }
    }

    @Override
    protected void setRightClick() {
        super.setRightClick();
        LatLng latLng = new LatLng(checkData.getLatLonPoint().getLatitude(), checkData.getLatLonPoint().getLongitude());
        aMap.addMarker(new MarkerOptions().position(latLng).title(checkData.getTitle()).snippet("DefaultMarker"));
        aMap.getMapScreenShot(bitmap -> {
            try {
                int w = DeviceUtils.getScreenWidth(mActivity);
                int h = (int) (((float) w / 3) * 2);
                String image = FileUtils.saveBitmapToFile(mActivity, zoomImg(bitmap, w, h), System.currentTimeMillis() + "amap.jpg");
                Intent intent = getActivity().getIntent();
                intent.putExtra(TSEMConstants.BUNDLE_LOCATION_LATITUDE, checkData.getLatLonPoint().getLatitude());
                intent.putExtra(TSEMConstants.BUNDLE_LOCATION_LONGITUDE, checkData.getLatLonPoint().getLongitude());
                intent.putExtra(TSEMConstants.BUNDLE_LOCATION_ADDRESS, checkData.getSnippet());
                intent.putExtra(TSEMConstants.BUNDLE_LOCATION_TITLE, checkData.getTitle());
                intent.putExtra(TSEMConstants.BUNDLE_LOCATION_IMAGE, image);
                getActivity().setResult(RESULT_OK, intent);
                getActivity().finish();
            } catch (Exception ignore) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = getActivity().getIntent();
        getActivity().setResult(RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUST_CODE_AREA && data != null && data.getExtras() != null) {
            PoiItem poiItem = data.getExtras().getParcelable(CircleLocationFragment.BUNDLE_DATA);
            if (poiItem != null) {
                isBackFromSearchChoose = true;
                ChatLocationBean bean = new ChatLocationBean();
                bean.setTitle(poiItem.getTitle());
                bean.setCity(poiItem.getCityName());
                bean.setAdress(poiItem.getAdName());
                bean.setSnippet(poiItem.getSnippet());
                bean.setLatLonPoint(poiItem.getLatLonPoint());

                mFinalChoosePosition = convertToLatLng(bean.getLatLonPoint());
                isHandDrag = false;
                checkData = bean;
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mFinalChoosePosition.latitude, mFinalChoosePosition.longitude), LOCATION_ZOOM));
            }
        }
    }

    @Override
    public void onMyLocationChange(Location location) {
        LogUtils.d("Cathy", location == null ? "定位失败" : location.getExtras());
        if (location != null) {
            mPage = DEFAULT_PAGE;
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            if (location.getExtras() != null) {
                mAddress = location.getExtras().getString("Address");
            }
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude, mLongitude), LOCATION_ZOOM));
            mFinalChoosePosition = new LatLng(mLatitude, mLongitude);
            searchPoi();


        } else {
            closeLoadingView();
            showSnackErrorMessage("定位失败");
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        mFinalChoosePosition = cameraPosition.target;
        ObjectAnimator animator = ObjectAnimator.ofFloat(mMarkView, "translationY", 0, -50, 0)
                .setDuration(300);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
        mPage = DEFAULT_PAGE;
        if (isHandDrag || isFirstLoadList) {
            //手动去拖动地图
            getAddress(cameraPosition.target);
            searchPoi();
        } else if (isBackFromSearchChoose) {
            searchPoi();
        }
        isHandDrag = true;
        isFirstLoadList = false;
        isBackFromSearchChoose = false;

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                // 逆转地里编码不是每次都可以得到对应地图上的opi
                mAddress = result.getRegeocodeAddress().getFormatAddress();
                LogUtils.d("逆地理编码回调  得到的地址：" + mAddress);
            } else {
                ToastUtils.showToast("没有结果");
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        closeLoadingView();
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {
                List<PoiItem> poiItems = result.getPois();
                List<ChatLocationBean> tem = new ArrayList<>();
                if (poiItems != null && poiItems.size() > 0) {
                    for (PoiItem poiItem : poiItems) {
                        ChatLocationBean bean = new ChatLocationBean();
                        bean.setTitle(poiItem.getTitle());
                        bean.setCity(poiItem.getCityName());
                        bean.setAdress(poiItem.getAdName());
                        bean.setSnippet(poiItem.getSnippet());
                        bean.setLatLonPoint(poiItem.getLatLonPoint());
                        if (mListDatas.contains(bean)) {
                            continue;
                        }
                        tem.add(bean);
                    }
                    onNetResponseSuccess(tem, getPage() > 1);
                }
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return new CommonAdapter<ChatLocationBean>(getActivity(), R.layout.item_chat_location,
                mListDatas) {
            @Override
            protected void convert(ViewHolder holder, ChatLocationBean locationBean, int position) {
                holder.setText(R.id.tv_location_name, locationBean.getTitle());
                holder.setText(R.id.tv_location_address, locationBean.getSnippet());
                EnableCheckBox checkBox = holder.getView(R.id.cb_checkbox);
                checkBox.setChecked(locationBean.equals(checkData));
                checkBox.setOnTouchEventListener(isEnabled -> {
                    holder.itemView.performClick();
                    return isEnabled;
                });
                RxView.clicks(holder.itemView)
                        .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                        .subscribe(aVoid -> {
                            mFinalChoosePosition = convertToLatLng(locationBean.getLatLonPoint());
                            isHandDrag = false;
                            checkData = locationBean;
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mFinalChoosePosition.latitude, mFinalChoosePosition.longitude), LOCATION_ZOOM));
                            refreshCheck();
                            mToolbarRight.setEnabled(true);
                        });
            }
        };
    }

    private void refreshCheck() {
        refreshData();
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_chat_send_location;
    }

    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new CustomLinearDecoration(0, getResources().getDimensionPixelSize(R.dimen
                .divider_line), 0, 0, ContextCompat.getDrawable(getContext(), R.drawable
                .shape_recyclerview_grey_divider));
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroyView() {
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        if (geocoderSearch != null) {
            geocoderSearch.setOnGeocodeSearchListener(null);
        }
        geocoderSearch = null;
        super.onDestroyView();
    }

    private void initAmap() {
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        aMap.setOnMapClickListener(this);
        aMap.setOnCameraChangeListener(this);
        aMap.setOnMyLocationChangeListener(this);
        aMap.setMyLocationEnabled(true);
        geocoderSearch = new GeocodeSearch(mActivity);
        geocoderSearch.setOnGeocodeSearchListener(this);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(LOCATION_ZOOM));


        myLocationStyle = new MyLocationStyle();
        //定位一次，且将视角移动到地图中心点。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));

        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        // 不显示定位蓝点图标
        myLocationStyle.showMyLocation(false);
    }

    private void searchPoi() {
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        PoiSearch.Query query = new PoiSearch.Query("", LOCATION_DATA, mAddress);
        query.setPageSize(DEFAULT_PAGE_DB_SIZE);
        query.setPageNum(getPage());

        LatLonPoint lpTemp = convertToLatLonPoint(mFinalChoosePosition);
        PoiSearch search = new PoiSearch(getContext(), query);
        search.setBound(new PoiSearch.SearchBound(lpTemp, 1000, true));
        search.setOnPoiSearchListener(this);
        search.searchPOIAsyn();
        mToolbarRight.setEnabled(false);
    }

    public void getAddress(final LatLng latLonPoint) {
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(convertToLatLonPoint(latLonPoint), 200, GeocodeSearch.AMAP);
        // 设置同步逆地理编码请求
        geocoderSearch.getFromLocationAsyn(query);
    }

    private LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }

    public LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    public Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        if (bm == null) {
            return null;
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        Bitmap newbm = Bitmap.createBitmap(bm, (width - newWidth) / 2, (height - newHeight) / 2, newWidth, newHeight);
        if (!bm.isRecycled()) {
            bm.recycle();
            bm = null;
        }
        return newbm;
    }

    @OnClick({R.id.iv_location, R.id.searchView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_location:
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude, mLongitude), LOCATION_ZOOM));
                break;
            case R.id.searchView:
                Intent intent = new Intent(getActivity(), SearchLocationActivity.class);
                startActivityForResult(intent, REQUST_CODE_AREA);
                break;
            default:
        }
    }

}
