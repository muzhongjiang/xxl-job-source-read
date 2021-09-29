package com.xxl.job.admin.core.route.strategy;

import cn.hutool.core.util.RandomUtil;
import com.xxl.job.admin.core.route.ExecutorRouter;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

import java.util.List;

/**
 * 随机路由
 *
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteRandom extends ExecutorRouter {

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        int index = RandomUtil.randomInt(addressList.size());
        return new ReturnT<>(addressList.get(index));
    }

}
