package com.xxl.job.admin.core.route.strategy;

import com.xxl.job.admin.core.route.ExecutorRouter;
import com.xxl.job.admin.core.scheduler.XxlJobScheduler;
import com.xxl.job.core.biz.client.ExecutorBizClient;
import com.xxl.job.core.biz.model.IdleBeatParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 忙碌转移
 * <p>
 * Created by xuxueli on 17/3/10.
 */
@Slf4j
public class ExecutorRouteBusyover extends ExecutorRouter {

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {

        StringBuffer idleBeatResultSB = new StringBuffer();

        for (String address : addressList) {
            // beat:
            ReturnT<String> idleBeatResult = null;
            try {
                ExecutorBizClient executorBiz = XxlJobScheduler.getExecutorBizClient(address);
                //空闲检测:
                idleBeatResult = executorBiz.idleBeat(new IdleBeatParam(triggerParam.getJobId()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                idleBeatResult = new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
            }
            idleBeatResultSB
                    .append((idleBeatResultSB.length() > 0) ? "<br><br>" : "")
                    .append("空闲检测：")
                    .append("<br>address：").append(address)
                    .append("<br>code：").append(idleBeatResult.getCode())
                    .append("<br>msg：").append(idleBeatResult.getMsg());

            // beat success
            if (idleBeatResult.getCode() == ReturnT.SUCCESS_CODE) {
                idleBeatResult.setMsg(idleBeatResultSB.toString());
                idleBeatResult.setContent(address);
                return idleBeatResult;
            }

        }

        return new ReturnT<>(ReturnT.FAIL_CODE, idleBeatResultSB.toString());
    }

}
