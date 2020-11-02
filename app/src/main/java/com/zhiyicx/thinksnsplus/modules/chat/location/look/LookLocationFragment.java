package com.zhiyicx.thinksnsplus.modules.chat.location.look;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.hyphenate.easeui.utils.OpenMapUtil;
import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.utils.appprocess.AndroidProcess;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.chat.location.send.SendLocationFragment;
import com.zhiyicx.thinksnsplus.modules.chat.location.tofriends.ToFriendsActivity;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;
import static com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow.POPUPWINDOW_ALPHA;
import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @author Catherine
 * @describe
 * @date 2018/1/10
 * @contact email:648129313@qq.com
 */
public class LookLocationFragment extends TSFragment<LookLocationContract.Presenter> implements LookLocationContract.View, AMap.OnMyLocationChangeListener {


    @BindView(R.id.mapView)
    MapView mMapView;
    @BindView(R.id.tv_navigation)
    TextView mTvNavigation;
    @BindView(R.id.tv_location_name)
    TextView mTvLocationName;
    @BindView(R.id.tv_location_address)
    TextView mTvLocationAddress;

    private AMap aMap;

    private double mLatitude;
    private double mLongitude;
    private String mAddress;
    private String mTitle;
    private String mImage;
    private String mChatId;

    private boolean mIsSend;

    private ActionPopupWindow mActionPopupWindow;
    private Marker mLocationMark;

    public LookLocationFragment instance(Bundle bundle) {
        LookLocationFragment fragment = new LookLocationFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(View rootView) {
        RxView.clicks(mTvNavigation)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> gotoMap());
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        if (bundle.containsKey(TSEMConstants.BUNDLE_LOCATION_LATITUDE)) {
            mLatitude = bundle.getDouble(TSEMConstants.BUNDLE_LOCATION_LATITUDE);
        }
        if (bundle.containsKey(TSEMConstants.BUNDLE_LOCATION_LONGITUDE)) {
            mLongitude = bundle.getDouble(TSEMConstants.BUNDLE_LOCATION_LONGITUDE);
        }
        if (bundle.containsKey(TSEMConstants.BUNDLE_LOCATION_ADDRESS)) {
            mAddress = bundle.getString(TSEMConstants.BUNDLE_LOCATION_ADDRESS);
        }
        if (bundle.containsKey(TSEMConstants.BUNDLE_LOCATION_TITLE)) {
            mTitle = bundle.getString(TSEMConstants.BUNDLE_LOCATION_TITLE);
        }
        if (bundle.containsKey(TSEMConstants.BUNDLE_LOCATION_IMAGE)) {
            mImage = bundle.getString(TSEMConstants.BUNDLE_LOCATION_IMAGE);
        }
        if (bundle.containsKey(TSEMConstants.BUNDLE_CHAT_ID)) {
            mChatId = bundle.getString(TSEMConstants.BUNDLE_CHAT_ID);
        }
        if (mLatitude != 0 && mLongitude != 0 && !TextUtils.isEmpty(mAddress)) {
            mIsSend = false;
        } else {
            mIsSend = true;
        }
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        if (mIsSend) {
            aMap.setOnMyLocationChangeListener(this);
            aMap.moveCamera(CameraUpdateFactory.zoomTo(SendLocationFragment.LOCATION_ZOOM));
            MyLocationStyle myLocationStyle;
            /*初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);*/
            myLocationStyle = new MyLocationStyle();
            myLocationStyle.showMyLocation(true);
            //定位一次，且将视角移动到地图中心点。
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
            // 自定义定位蓝点图标
            myLocationStyle.showMyLocation(true);
            aMap.setMyLocationStyle(myLocationStyle);
            /*设置默认定位按钮是否显示，非必需设置。*/
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            /*设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。*/
            aMap.setMyLocationEnabled(true);
        } else {
            // 添加已有的定位点
            LatLng latLng = new LatLng(mLatitude, mLongitude);
            mLocationMark = aMap.addMarker(new MarkerOptions().position(latLng).title(mAddress).snippet(""));
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, SendLocationFragment.LOCATION_ZOOM));
            aMap.setOnMapTouchListener(motionEvent -> {
                if (mLocationMark.isInfoWindowShown()) {
                    mLocationMark.hideInfoWindow();
                }
            });
        }

        mTvLocationName.setText(mTitle);
        mTvLocationAddress.setText(mAddress);
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_look_location;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.chat_location_info);
    }

    @Override
    protected int setRightImg() {
        return R.mipmap.ico_transmit;
    }

    @Override
    protected void setRightClick() {
        ToFriendsActivity.startToFriendActivity(mActivity, mImage, mLatitude + "",
                mLongitude + "", mAddress, mTitle, mChatId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = getActivity().getIntent();
        getActivity().setResult(RESULT_OK, intent);
        getActivity().finish();
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

    @Override
    public void onDestroyView() {
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        super.onDestroyView();
    }

    @Override
    public void onMyLocationChange(Location location) {
        LogUtils.d("Cathy", location == null ? "定位失败" : location.getExtras());
        if (location != null) {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            if (location.getExtras() != null) {
                mAddress = location.getExtras().getString("Address");
            }
        } else {
            showSnackErrorMessage("定位失败");
        }
    }

    private void gotoMap() {
        initActionPopupWindow();
    }

    private void initActionPopupWindow() {
        if (!OpenMapUtil.getInstance().hasMultMapApp()) {
            if (!OpenMapUtil.getInstance().gotoMap(mActivity,
                    AndroidProcess.getProcessName(mActivity, android.os.Process.myPid()),
                    mLatitude,
                    mLongitude,
                    mAddress)) {
                showSnackErrorMessage("没有地图应用");
            }
        } else {
            mActionPopupWindow = ActionPopupWindow.builder()
                    .item1Str(OpenMapUtil.getInstance().isInstallByread(OpenMapUtil.MAP.GG.pkg) ? OpenMapUtil.MAP.GG.name : "")
                    .item1ClickListener(() -> {
                        OpenMapUtil.getInstance().gotoMap(mActivity,
                                AndroidProcess.getProcessName(mActivity, android.os.Process.myPid()),
                                mLatitude,
                                mLongitude,
                                mAddress, OpenMapUtil.MAP.GG);
                        mActionPopupWindow.hide();
                    })
                    .item2Str(OpenMapUtil.getInstance().isInstallByread(OpenMapUtil.MAP.GD.pkg) ? OpenMapUtil.MAP.GD.name : "")
                    .item2ClickListener(() -> {
                                OpenMapUtil.getInstance().gotoMap(mActivity,
                                        AndroidProcess.getProcessName(mActivity, android.os.Process.myPid()),
                                        mLatitude,
                                        mLongitude,
                                        mAddress, OpenMapUtil.MAP.GD);
                                mActionPopupWindow.hide();
                            }
                    )
                    .item3Str(OpenMapUtil.getInstance().isInstallByread(OpenMapUtil.MAP.BD.pkg) ? OpenMapUtil.MAP.BD.name : "")
                    .item3ClickListener(() -> {
                        OpenMapUtil.getInstance().gotoMap(mActivity,
                                AndroidProcess.getProcessName(mActivity, android.os.Process.myPid()),
                                mLatitude,
                                mLongitude,
                                mAddress, OpenMapUtil.MAP.BD);
                        mActionPopupWindow.hide();
                    })
                    .item4Str(OpenMapUtil.getInstance().isInstallByread(OpenMapUtil.MAP.TX.pkg) ? OpenMapUtil.MAP.TX.name : "")
                    .item4ClickListener(() -> {
                        OpenMapUtil.getInstance().gotoMap(mActivity,
                                AndroidProcess.getProcessName(mActivity, android.os.Process.myPid()),
                                mLatitude,
                                mLongitude,
                                mAddress, OpenMapUtil.MAP.TX);
                        mActionPopupWindow.hide();
                    })
                    .bottomStr(getString(R.string.cancel))
                    .bottomClickListener(() -> mActionPopupWindow.hide())
                    .isOutsideTouch(true)
                    .isFocus(true)
                    .backgroundAlpha(POPUPWINDOW_ALPHA)
                    .with(getActivity())
                    .build();
            mActionPopupWindow.show();
        }
    }
}
