package com.xxl.job.admin.core.route.strategy;

import com.xxl.job.admin.core.route.ExecutorRouter;
import com.xxl.job.admin.core.scheduler.XxlJobScheduler;
import com.xxl.job.core.biz.client.ExecutorBizClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 故障转移
 * <p>
 * Created by xuxueli on 17/3/10.
 */
@Slf4j
public class ExecutorRouteFailover extends ExecutorRouter {

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {

        StringBuffer beatResultSB = new StringBuffer();
        for (String address : addressList) {//循环 测试心跳，返回一个可用的worker
            ReturnT<String> beatResult = null;
            try {
                ExecutorBizClient executorBiz = XxlJobScheduler.getExecutorBizClient(address);
                beatResult = executorBiz.beat();  //测试心跳，返回一个可用的worker
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                beatResult = new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
            }
            beatResultSB.append((beatResultSB.length() > 0) ? "<br><br>" : "")
                    .append("心跳检测：")
                    .append("<br>address：").append(address)
                    .append("<br>code：").append(beatResult.getCode())
                    .append("<br>msg：").append(beatResult.getMsg());

            // beat success
            if (beatResult.getCode() == ReturnT.SUCCESS_CODE) {
                return beatResult
                        .setMsg(beatResultSB.toString())
                        .setContent(address);
            }
        }
        return new ReturnT<>(ReturnT.FAIL_CODE, beatResultSB.toString());

    }
}
