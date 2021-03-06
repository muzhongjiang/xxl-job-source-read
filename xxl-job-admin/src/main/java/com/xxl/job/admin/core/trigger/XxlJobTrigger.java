package com.xxl.job.admin.core.trigger;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.scheduler.XxlJobScheduler;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.client.ExecutorBizClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.util.IpUtil;
import com.xxl.job.core.util.ThrowableUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * xxl-job trigger
 * Created by xuxueli on 17/7/13.
 */
@Slf4j
public class XxlJobTrigger {

    /**
     * trigger job
     *
     * @param jobId
     * @param triggerType
     * @param failRetryCount >=0: use this param
     *                       <0: use param from job info config
     * @param executorParam  null: use job param
     *                       not null: cover job param
     * @param addressList    null: use executor addressList
     *                       not null: cover
     */
    public static void trigger(int jobId,
                               TriggerTypeEnum triggerType,
                               int failRetryCount,
                               String executorParam,
                               String addressList) {

        // load data
        XxlJobInfo jobInfo = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().loadById(jobId);
        if (jobInfo == null) {
            log.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }
        if (executorParam != null) {
            jobInfo.setExecutorParam(executorParam);
        }
        int finalFailRetryCount = failRetryCount >= 0 ? failRetryCount : jobInfo.getExecutorFailRetryCount();
        XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(jobInfo.getJobGroup());

        // cover addressList
        if (StrUtil.isNotBlank(addressList)) {
            group.setAddressType(1);
            group.setAddressList(addressList.trim());
        }

        // sharding param
        int[] shardingParam = {0, 1};// TODO ???


        processTrigger(group, jobInfo, finalFailRetryCount, triggerType, shardingParam[0], shardingParam[1]);

    }


    /**
     * @param group job group, registry list may be empty
     * @param index sharding index
     * @param total sharding index
     */
    private static void processTrigger(XxlJobGroup group, XxlJobInfo jobInfo, int finalFailRetryCount, TriggerTypeEnum triggerType, int index, int total) {

        //改写begin >>>>>>>>>>>>>>>>：
        //1.1 生成实例（action）：
        //1.2 执行实例（action）：
        //1.3 history关联actionId：
        //改写end <<<<<<<<


        // param
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(
                jobInfo.getExecutorBlockStrategy(),
                ExecutorBlockStrategyEnum.SERIAL_EXECUTION
        );  // block strategy
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);    // route strategy


        // 1、save log-id
        XxlJobLog jobLog = new XxlJobLog()
//                .setJobActionId(jobAction.getId()) // history关联actionId
                .setJobGroup(jobInfo.getJobGroup())
                .setJobId(jobInfo.getId())
//                .setTriggerTime(new Date());
                .setTriggerTime(new Date(jobInfo.getTriggerNextTime()));

        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().save(jobLog);
        log.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

        // 2、init trigger-param
        TriggerParam triggerParam = new TriggerParam()
                .setJobId(jobInfo.getId())
                .setExecutorHandler(jobInfo.getExecutorHandler())
                .setExecutorParams(jobInfo.getExecutorParam())
                .setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy())
                .setExecutorTimeout(jobInfo.getExecutorTimeout())
                .setLogId(jobLog.getId())
                .setLogDateTime(jobLog.getTriggerTime().getTime())
                .setGlueType(jobInfo.getGlueType())
                .setGlueSource(jobInfo.getGlueSource())
                .setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime())
                .setBroadcastIndex(index)
                .setBroadcastTotal(total);

        // 3、init workerAddress
        String workerAddress = null;
        ReturnT<String> routeAddressResult;
        if (CollectionUtil.isNotEmpty(group.getRegistryList())) {
            routeAddressResult = executorRouteStrategyEnum.getRouter().route(triggerParam, group.getRegistryList());
            if (routeAddressResult.getCode() == ReturnT.SUCCESS_CODE) {
                workerAddress = routeAddressResult.getContent();
            }
        } else {
            routeAddressResult = new ReturnT<>(ReturnT.FAIL_CODE, "调度失败：执行器地址为空");
        }

        // 4、trigger remote executor
        ReturnT<String> triggerResult = null;
        if (workerAddress != null) {
            triggerResult = runExecutor(triggerParam, workerAddress);
        } else {
            triggerResult = new ReturnT<>(ReturnT.FAIL_CODE, null);
        }

        // 5、collection trigger info
        StringBuffer triggerMsgSb = new StringBuffer();
        triggerMsgSb.append(I18nUtil.getString("jobconf_trigger_type")).append("：").append(triggerType.getTitle());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_admin_adress")).append("：").append(IpUtil.getIp());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regtype")).append("：")
                .append((group.getAddressType() == 0) ? I18nUtil.getString("jobgroup_field_addressType_0") : I18nUtil.getString("jobgroup_field_addressType_1"));
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regaddress")).append("：").append(group.getRegistryList());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorRouteStrategy")).append("：").append(executorRouteStrategyEnum.getTitle());

        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorBlockStrategy")).append("：").append(blockStrategy.getTitle());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_timeout")).append("：").append(jobInfo.getExecutorTimeout());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorFailRetryCount")).append("：").append(finalFailRetryCount);

        triggerMsgSb
                .append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>")
                .append(I18nUtil.getString("jobconf_trigger_run"))
                .append("<<<<<<<<<<< </span><br>")
                .append((routeAddressResult != null && routeAddressResult.getMsg() != null) ? routeAddressResult.getMsg() + "<br><br>" : "").append(triggerResult.getMsg() != null ? triggerResult.getMsg() : "");

        // 6、save log trigger-info
        jobLog.setExecutorAddress(workerAddress);
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setExecutorFailRetryCount(finalFailRetryCount);
        //jobLog.setTriggerTime();
        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(triggerMsgSb.toString());
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateTriggerInfo(jobLog);

        log.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
    }

    /**
     * run executor
     *
     * @param triggerParam
     * @param address
     * @return
     */
    public static ReturnT<String> runExecutor(TriggerParam triggerParam, String address) {
        ReturnT<String> runResult = null;
        try {
            ExecutorBizClient executorBiz = XxlJobScheduler.getExecutorBizClient(address);
            runResult = executorBiz.run(triggerParam);
        } catch (Exception e) {
            log.error(">>>>>>>>>>> xxl-job trigger error, please check if the executor[{}] is running.", address, e);
            runResult = new ReturnT<>(ReturnT.FAIL_CODE, ThrowableUtil.toString(e));
        }

        StringBuffer runResultSB = new StringBuffer(I18nUtil.getString("jobconf_trigger_run") + "：");
        runResultSB.append("<br>address：").append(address);
        runResultSB.append("<br>code：").append(runResult.getCode());
        runResultSB.append("<br>msg：").append(runResult.getMsg());

        runResult.setMsg(runResultSB.toString());
        return runResult;
    }

}
