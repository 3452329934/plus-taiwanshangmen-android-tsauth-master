package com.zhiyicx.thinksnsplus;

import android.util.SparseArray;

import com.google.gson.Gson;
import com.zhiyicx.baseproject.base.SystemConfigBean;
import com.zhiyicx.common.config.MarkdownConfig;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;

import static com.zhiyicx.imsdk.db.base.BaseDao.TIME_DEFAULT_ADD;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/3/17
 * @Contact master.jungle68@gmail.com
 */

public class TimeTest {

    /**
     * utc 时间测试
     */
    @Test
    public void finalTest() {
        System.out.println(System.currentTimeMillis());
        String time = "2017-03-17 8:28:56";
        System.out.println("TimeUtils.converTime(time, TimeZone.getDefault()) = " + TimeUtils.utc2LocalStr(time));
        System.out.println("TimeUtils.getTimeFriendlyForDetail() = " + TimeUtils.getTimeFriendlyForDetail(TimeUtils.utc2LocalStr(time)));

        java.util.Calendar ca2 = java.util.Calendar.getInstance();
        System.out.println("ca2 = " + ca2.getTimeInMillis());
    }

    /**
     * 获取当前 0 时区时间测试
     */
    @Test
    public void getCurrenZeroTime() {
        System.out.println("cal str = " + TimeUtils.getCurrenZeroTimeStr());
        System.out.println("cal  long= " + TimeUtils.getCurrenZeroTime());

    }

    @Test
    public void rxMerge() {
        UserInfoBean userInfoBean1 = new UserInfoBean();
        userInfoBean1.setUser_id(10L);
        UserInfoBean userInfoBean2 = new UserInfoBean();
        userInfoBean2.setName("李四");
        Observable<UserInfoBean> userInfoBeanObservable1 = Observable.just(userInfoBean1);
        Observable<UserInfoBean> userInfoBeanObservable2 = Observable.just(userInfoBean2);
        Observable.merge(userInfoBeanObservable1, userInfoBeanObservable2)
                .subscribe(new Action1<UserInfoBean>() {
                    @Override
                    public void call(UserInfoBean userInfoBean) {
                        System.out.println("userInfoBean = " + userInfoBean.getUser_id());
                    }
                });
        Observable.zip(userInfoBeanObservable1, userInfoBeanObservable2, new Func2<UserInfoBean, UserInfoBean, UserInfoBean>() {
            @Override
            public UserInfoBean call(UserInfoBean userInfoBean, UserInfoBean userInfoBean2) {
                userInfoBean.setName(userInfoBean2.getName());
                return userInfoBean;
            }
        }).subscribe(new Action1<UserInfoBean>() {
            @Override
            public void call(UserInfoBean userInfoBean) {
                System.out.println("userInfoBean = " + userInfoBean.getUser_id() + userInfoBean.getName());
            }
        });


    }

    @Test
    public void rxCombineLast() {
        UserInfoBean userInfoBean1 = new UserInfoBean();
        userInfoBean1.setName("张三");
        UserInfoBean userInfoBean2 = new UserInfoBean();
        userInfoBean2.setName("李四");
        Observable<UserInfoBean> userInfoBeanObservable1 = Observable.just(userInfoBean1);
        Observable<UserInfoBean> userInfoBeanObservable2 = Observable.just(userInfoBean2);

        Observable.combineLatest(userInfoBeanObservable1, userInfoBeanObservable2, new Func2<UserInfoBean, UserInfoBean, UserInfoBean>() {
            @Override
            public UserInfoBean call(UserInfoBean userInfoBean, UserInfoBean userInfoBean2) {
                System.out.println("userInfoBean = " + userInfoBean.getName());
                System.out.println("userInfoBean2 = " + userInfoBean2.getName());
                return userInfoBean;

            }
        }).subscribe(new Action1<UserInfoBean>() {
            @Override
            public void call(UserInfoBean userInfoBean) {
                System.out.println(userInfoBean.getName());
            }
        });

    }

    @Test
    public void rxZip() {
        UserInfoBean userInfoBean1 = new UserInfoBean();
        userInfoBean1.setUser_id(10L);
        UserInfoBean userInfoBean2 = new UserInfoBean();
        userInfoBean2.setName("李四");
        Observable<UserInfoBean> userInfoBeanObservable1 = Observable.just(userInfoBean1);
        Observable<UserInfoBean> userInfoBeanObservable2 = Observable.just(userInfoBean2);
        Observable.zip(userInfoBeanObservable1, userInfoBeanObservable2, new Func2<UserInfoBean, UserInfoBean, UserInfoBean>() {
            @Override
            public UserInfoBean call(UserInfoBean userInfoBean, UserInfoBean userInfoBean2) {
                userInfoBean.setName(userInfoBean2.getName());
                return userInfoBean;
            }
        }).subscribe(new Action1<UserInfoBean>() {
            @Override
            public void call(UserInfoBean userInfoBean) {
                System.out.println("userInfoBean = " + userInfoBean.getUser_id() + userInfoBean.getName());
            }
        });


    }


    @Test
    public void testdjgkdgj() {

        long a = 325237164109463553L;
        long b = (a >> 23) + TIME_DEFAULT_ADD;
        System.out.println("b = " + b);

    }

    /**
     * summary
     * steps
     * expected
     */
    @Test
    public void testChar() throws Exception {
        System.out.println("testChar= " + "帅".getBytes().length);
    }

    @Test
    public void testABC() {
        String src = "<a>xxx</a><a>bbbb</a>";
        Matcher matcher = Pattern.compile(MarkdownConfig.FILTER_A_FORMAT).matcher(src);
        while (matcher.find()) {
            int count = matcher.groupCount();
            for (int i = 0; i < count; i++) {
                System.out.println("reg::" + i + ":::" + matcher.group(i));
            }
        }
    }

    @Test
    public void testAT() {
        String src = "< \u00ad@小元\u00ad来围观,你走开\u00ad@小霸\u00ad小霸\u00ad >";
        String test = " \u00AD@王二丫\u00AD ";
        Matcher matcher = Pattern.compile(MarkdownConfig.AT_FORMAT).matcher(test);
        while (matcher.find()) {
            System.out.println("start::" + matcher.start() + "end::" + matcher.end() + ":::" + test.subSequence(matcher.start(), matcher.end()).toString());
            int count = matcher.groupCount();
            for (int i = 0; i < count; i++) {
                System.out.println("reg::" + i + ":::" + matcher.group(i));
            }
        }
    }

    @Test
    public void testATComplete() {
        String src = "< \u00ad@小元\u00ad来围观,你走开\u00ad@小霸\u00ad@小霸王>";
//        String src = "<@小元来围观,你走开@小霸@小霸王>";
        String rege = "(?<!\u00ad)@[^./\\s\u00ad@]+";
        String atrege = "\\u00ad(?:@[^/]+?)\\u00ad";
        String holderrege = "☆zhiyi☆";
        Matcher at = Pattern.compile(atrege).matcher(src);
        List<String> atList = new ArrayList<>();
        boolean isFind = false;
        while (at.find()) {
            atList.add(at.group(0));
            isFind = true;
        }
        if (isFind) {
            src = at.replaceAll(holderrege);
        }
        Matcher prefix = Pattern.compile(rege).matcher(src);
        while (prefix.find()) {
            src = src.replaceFirst(prefix.group(0), MarkdownConfig.AT_MARK + prefix.group(0) + MarkdownConfig.AT_SUFFIX);
            System.out.println("prefix::" + src);
        }
        Matcher holder = Pattern.compile(holderrege).matcher(src);
        int i = 0;
        while (holder.find()) {
            if (i < atList.size()) {
                src = src.replaceFirst(holder.group(0), atList.get(i));
            }
            i++;
            System.out.println("holder::" + src);
        }
    }


    @Test
    public void testSparry() {
        List<String> stringList = new ArrayList<>();
        stringList.add("1");
        stringList.add("2");
        stringList.add("3");

        SparseArray<String> test = new SparseArray<>();
        for (String src : stringList) {
            test.append(stringList.indexOf(src), src);
        }

        test.put(0, "test");

        System.out.println(test.get(0));
        System.out.println(stringList.get(0));

    }

    @Test
    public void testJson() {
        String s = "{\n" +
                "    \"server:version\": \"2.0.0\",\n" +
                "    \"hots_area\": [\n" +
                "        {\n" +
                "            \"name\": \"中国 四川省 成都市\",\n" +
                "            \"sort\": 0\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"中国 北京 北京\",\n" +
                "            \"sort\": 0\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"中国 上海 上海\",\n" +
                "            \"sort\": 0\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"中国 江苏省 南京市\",\n" +
                "            \"sort\": 0\n" +
                "        }\n" +
                "    ],\n" +
                "    \"wallet:ratio\": 100,\n" +
                "    \"wallet:recharge-type\": [\n" +
                "        \"applepay_upacp\",\n" +
                "        \"alipay\",\n" +
                "        \"alipay_wap\",\n" +
                "        \"alipay_pc_direct\",\n" +
                "        \"alipay_qr\",\n" +
                "        \"wx\",\n" +
                "        \"wx_wap\"\n" +
                "    ],\n" +
                "    \"ad\": [],\n" +
                "    \"site\": {\n" +
                "        \"gold\": {\n" +
                "            \"status\": true\n" +
                "        },\n" +
                "        \"reward\": {\n" +
                "            \"status\": true,\n" +
                "            \"amounts\": \"100,500,1000\"\n" +
                "        },\n" +
                "        \"reserved_nickname\": \"root,admin\",\n" +
                "        \"client_email\": \"admin@123.com\",\n" +
                "        \"user_invite_template\": \"我发现了一个全平台社交系统ThinkSNS+，快来加入吧：http://t.cn/RpFfbbi\",\n" +
                "        \"anonymous\": {\n" +
                "            \"status\": true,\n" +
                "            \"rule\": \"111\"\n" +
                "        },\n" +
                "        \"about_url\": null,\n" +
                "        \"status\": true,\n" +
                "        \"off_reason\": \"站点维护中请稍后再访问\",\n" +
                "        \"app\": {\n" +
                "            \"status\": true\n" +
                "        },\n" +
                "        \"h5\": {\n" +
                "            \"status\": true\n" +
                "        },\n" +
                "        \"aboutUs\": {\n" +
                "            \"url\": null,\n" +
                "            \"content\": \"关于我们^2^\"\n" +
                "        },\n" +
                "        \"gold_name\": {\n" +
                "            \"name\": \"金币\",\n" +
                "            \"unit\": \"个\"\n" +
                "        },\n" +
                "        \"currency_name\": {\n" +
                "            \"id\": 1,\n" +
                "            \"name\": \"积分\",\n" +
                "            \"unit\": \"\",\n" +
                "            \"enable\": 1\n" +
                "        }\n" +
                "    },\n" +
                "    \"registerSettings\": null,\n" +
                "    \"wallet:cash\": {\n" +
                "        \"open\": true\n" +
                "    },\n" +
                "    \"wallet:recharge\": {\n" +
                "        \"open\": true\n" +
                "    },\n" +
                "    \"wallet:transform\": {\n" +
                "        \"open\": true\n" +
                "    },\n" +
                "    \"currency:cash\": {\n" +
                "        \"open\": true\n" +
                "    },\n" +
                "    \"currency:recharge\": {\n" +
                "        \"open\": true,\n" +
                "        \"IAP_only\": true\n" +
                "    },\n" +
                "    \"limit\": 15,\n" +
                "    \"question:apply_amount\": 200,\n" +
                "    \"question:onlookers_amount\": 100,\n" +
                "    \"plus-appversion\": {\n" +
                "        \"open\": false\n" +
                "    },\n" +
                "    \"checkin\": true,\n" +
                "    \"checkin:attach_balance\": 1,\n" +
                "    \"feed\": {\n" +
                "        \"reward\": true,\n" +
                "        \"paycontrol\": true,\n" +
                "        \"items\": [\n" +
                "            \"100\",\n" +
                "            \"500\",\n" +
                "            \"1000\"\n" +
                "        ],\n" +
                "        \"limit\": 30\n" +
                "    },\n" +
                "    \"news:contribute\": {\n" +
                "        \"pay\": false,\n" +
                "        \"verified\": false\n" +
                "    },\n" +
                "    \"news:pay_conyribute\": 1,\n" +
                "    \"group:create\": {\n" +
                "        \"need_verified\": false\n" +
                "    },\n" +
                "    \"group:reward\": {\n" +
                "        \"status\": true\n" +
                "    }\n" +
                "}";
        Gson gson = new Gson();
        SystemConfigBean testString = gson.fromJson(s, SystemConfigBean.class);
        System.out.print(testString.getQuestionConfig().getAnonymity_rule() == null);
    }

}
