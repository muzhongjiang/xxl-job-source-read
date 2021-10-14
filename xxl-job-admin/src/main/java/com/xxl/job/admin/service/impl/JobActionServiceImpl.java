package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.JobAction;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.trigger.JobStatusEnum;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.dao.JobActionDao;
import com.xxl.job.admin.service.JobActionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.hutool.core.date.DatePattern.PURE_DATE_FORMAT;

@Slf4j
@Service
public class JobActionServiceImpl implements JobActionService {

    @Autowired
    private JobActionDao jobActionDao;


    private JobAction buildJobAction(
            final int jobId,
            final TriggerTypeEnum triggerType,
            final Integer executorFailRetryCount,
            final String executorParam
    ) {
        XxlJobInfo jobInfo = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().loadById(jobId);
        JobAction jobAction = new JobAction();
        BeanUtils.copyProperties(jobInfo, jobAction);
        jobAction
                .setId(System.nanoTime())
                .setJobId(jobInfo.getId())
                .setCronTime(jobInfo.getTriggerNextTime())
                .setBatchId(Integer.parseInt(PURE_DATE_FORMAT.format(new Date())))
        ;


        if (triggerType != null) {
            jobAction.setTriggerType(triggerType.name());
        }
        if (executorFailRetryCount != null) {
            jobAction.setExecutorFailRetryCount(executorFailRetryCount);
        }
        if (executorParam != null) {
            jobAction.setExecutorParam(executorParam);
        }
        return jobAction;

    }

    @Override
    public int buildAndSaveAction(
            final int jobId,
            final TriggerTypeEnum triggerType,
            final Integer executorFailRetryCount,
            final String executorParam
    ) {
        return jobActionDao.insert(buildJobAction(jobId, triggerType, executorFailRetryCount, executorParam));
    }

    @Override
    public List<JobAction> batchInsert(List<JobAction> jobActionList, Long nowAction) {
        log.info("batchInsert-> batch size is :{}", jobActionList.size());
        List<JobAction> insertList = new ArrayList<>();
        for (JobAction jobAction : jobActionList) {
            //更新时单条更新
            //原因：批量更新时，在极端情况下，数据会有批量数据处理时间的buff
            //     此时如果有其它地方修改了某条数据 会数据库中的数据被批量更新的覆盖
            if (isNeedUpdateAction(jobAction, nowAction)) {
                this.update(jobAction);
            } else {
                insertList.add(jobAction);
            }
        }
        if (insertList.size() != 0) {
            jobActionDao.batchInsert(insertList);
        }
        return jobActionList;
    }

    /**
     * 判断是更新该是修改
     */
    private boolean isNeedUpdateAction(JobAction jobAction, Long nowAction) {

        JobAction action = jobActionDao.findById(jobAction.getId());
        if (action != null) {
            //如果该任务不是在运行中：
            if (!JobStatusEnum.RUNNING.toString().equals(action.getStatus())) {
                jobAction.setStatus(action.getStatus());
//TODO          jobAction.setReadyDependency(action.getReadyDependency());
            } else {
                BeanUtils.copyProperties(action, jobAction);
            }
            return true;
        } else {
            //生成action时，任务过时:
            if (jobAction.getId() < nowAction) {
                jobAction.setStatus(JobStatusEnum.FAILED.toString());
                jobAction.setLastResult("生成action时，任务过时，直接设置为失败");
                log.info("生成action时，任务过时，直接设置为失败:" + jobAction.getId());
            }
        }
        return false;
    }

    @Override
    public int insert(JobAction jobAction, Long nowAction) {
        if (isNeedUpdateAction(jobAction, nowAction)) {
            return jobActionDao.update(jobAction);
        } else {
            return jobActionDao.insert(jobAction);
        }
    }

    @Override
    public int delete(Long id) {
        return jobActionDao.delete(id);
    }

    @Override
    public int update(JobAction jobAction) {
        return jobActionDao.update(jobAction);
    }

    @Override
    public List<JobAction> getAll() {
        return jobActionDao.getAll();
    }

    @Override
    public JobAction findById(Long actionId) {
        return jobActionDao.findById(actionId);
    }

    @Override
    public JobAction findById(String actionId) {
        return this.findById(Long.parseLong(actionId));
    }

    @Override
    public JobAction findLatestByJobId(Long jobId) {
        return jobActionDao.findLatestByJobId(jobId);
    }

    @Override
    public List<JobAction> findByJobId(Long jobId) {
        return jobActionDao.findByJobId(jobId);
    }

    @Override
    public int updateStatus(String jobStatus) {
//        JobAction JobAction = findById(jobStatus.getActionId());
//        JobAction.setGmtModified(new Date());
//        JobAction tmp = BeanConvertUtils.convert(jobStatus);
//        JobAction.setStatus(tmp.getStatus());
//        JobAction.setReadyDependency(tmp.getReadyDependency());
//        JobAction.setHistoryId(jobStatus.getHistoryId());
//        return update(JobAction);
        return 0;
    }

}
