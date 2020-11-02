package com.zhiyicx.thinksnsplus.modules.chat.location.search;

import com.zhiyicx.thinksnsplus.modules.circle.create.location.CircleLocationActivity;
/**
 * @Author Jliuer
 * @Date 2018/06/13/17:38
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SearchLocationActivity extends CircleLocationActivity {
    @Override
    protected SearchLocationFragment getFragment() {
        return new SearchLocationFragment();
    }
}
