package com.hyphenate.easeui.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.util.Locale;

/**
 * @Author Jliuer
 * @Date 2018/06/20/10:18
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class OpenMapUtil {

    public static OpenMapUtil getInstance() {
        return SingleInstance.sOpenMapUtil;
    }

    private static class SingleInstance {
        static OpenMapUtil sOpenMapUtil = new OpenMapUtil();
    }

    public enum MAP {

        // 谷歌
        GG("com.google.android.apps.maps", "地图"),

        // 高德
        GD("com.autonavi.minimap", "高德地图"),

        // 腾讯
        TX("com.tencent.map", "腾讯地图"),

        // 百度
        BD("com.baidu.BaiduMap", "百度地图");

        public String pkg;
        public String name;

        MAP(String pkg, String name) {
            this.pkg = pkg;
            this.name = name;
        }
    }

    public boolean openMap(Activity activity, String appName, double latitude, double longitude, String address) {
        if (!gotoMap(activity, appName, latitude, longitude, address, MAP.GD)) {
            if (!gotoMap(activity, appName, latitude, longitude, address, MAP.BD)) {
                return gotoMap(activity, appName, latitude, longitude, address, MAP.TX);
            }
        }
        return true;
    }

    public boolean gotoMap(Activity activity, String appName, double latitude, double longitude, String address, MAP map) {
        if (isInstallByread(map.pkg)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage(map.pkg);
            String data = null;
            switch (map) {
                case GG:
                    data = getGGMapUri(latitude, longitude);
                    break;
                case BD:
                    data = getBdMapUri(latitude, longitude, address);
                    break;
                case GD:
                    data = getGdMapUri(appName, latitude, longitude, address);
                    break;
                case TX:
                    data = getTxMapUri(appName, latitude, longitude, address);
                    break;
                default:
            }
            if (TextUtils.isEmpty(data)) {
                return false;
            }
            Uri uri = Uri.parse(data);
            intent.setData(uri);
            intent.setPackage(map.pkg);
            activity.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    public boolean gotoMap(Activity activity, String appName, double latitude, double longitude, String address) {
        MAP map = getCanUseMap();
        if (map == null) {
            return false;
        }
        if (isInstallByread(map.pkg)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage(map.pkg);
            String data = null;
            switch (map) {
                case GG:
                    data = getGGMapUri(latitude, longitude);
                    break;
                case BD:
                    data = getBdMapUri(latitude, longitude, address);
                    break;
                case GD:
                    data = getGdMapUri(appName, latitude, longitude, address);
                    break;
                case TX:
                    data = getTxMapUri(appName, latitude, longitude, address);
                    break;
                default:
            }
            if (TextUtils.isEmpty(data)) {
                return false;
            }
            Uri uri = Uri.parse(data);
            intent.setData(uri);
            intent.setPackage(map.pkg);
            activity.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param dlat 经纬度
     * @param dlon
     * @return
     */
    private String getGGMapUri(double dlat, double dlon) {
        String newUri = "google.navigation:q=%1$f,%2$f";
        return String.format(Locale.getDefault(), newUri, dlat, dlon);
    }

    /**
     * @param appName 当前应用名称
     * @param dlat    经纬度
     * @param dlon
     * @param dname   地理位置名称
     * @return
     */
    private String getGdMapUri(String appName, double dlat, double dlon, String dname) {
        String newUri = "androidamap://navi?sourceApplication=%1$s&poiname=%2$s&lat=%3$f&lon=%4$f&dev=1&style=2";
        return String.format(Locale.getDefault(), newUri, appName, dname, dlat, dlon);
    }

    /**
     * @param dlat  经纬度
     * @param dlon
     * @param dname 地理位置名称
     * @return
     */
    private String getBdMapUri(double dlat, double dlon, String dname) {
        String newUri = "baidumap://map/direction?destination=latlng:%1$f,%2$f|name:%3$s&mode=driving";
        return String.format(Locale.getDefault(), newUri, dlat, dlon, dname);
    }

    /**
     * @param appName 当前应用名称
     * @param dlat    经纬度
     * @param dlon    多个
     * @param dname   地理位置名称
     * @return
     */
    private String getTxMapUri(String appName, double dlat, double dlon, String dname) {
        String newUri = "qqmap://map/routeplan?type=drive&to=%1$s&tocoord=%2$f,%3$f&referer=%4$s";
        return String.format(Locale.getDefault(), newUri, dname, dlat, dlon, appName);
    }

    public boolean hasMultMapApp() {
        int mapCount = 0;
        if (isInstallByread(MAP.GG.pkg)) {
            mapCount++;
        }
        if (isInstallByread(MAP.GD.pkg)) {
            mapCount++;
        }
        if (isInstallByread(MAP.BD.pkg)) {
            mapCount++;
        }
        if (isInstallByread(MAP.TX.pkg)) {
            mapCount++;
        }
        return mapCount > 1;
    }

    public MAP getCanUseMap() {
        MAP map = null;
        if (isInstallByread(MAP.GG.pkg)) {
            map = MAP.GG;
        }
        if (isInstallByread(MAP.GD.pkg)) {
            map = MAP.GD;
        }
        if (isInstallByread(MAP.BD.pkg)) {
            map = MAP.BD;
        }
        if (isInstallByread(MAP.TX.pkg)) {
            map = MAP.TX;
        }
        return map;
    }

    public boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }
}
