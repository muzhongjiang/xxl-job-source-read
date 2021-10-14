package com.xxl.job.admin.service;


import com.xxl.job.admin.core.model.JobAction;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;

import java.util.List;

/**
 * JobAction Service
 */
public interface JobActionService {

    /**
     * 构建action数据并保存到db
     */
    int buildAndSaveAction(
            final int jobId,
            final TriggerTypeEnum triggerType,
            final Integer executorFailRetryCount,
            final String executorParam
    );

    int insert(JobAction JobAction, Long nowAction);

    /**
     * 批量插入
     */
    List<JobAction> batchInsert(List<JobAction> JobActionList, Long nowAction);

    int delete(Long id);

    int update(JobAction JobAction);

    List<JobAction> getAll();

    JobAction findById(Long actionId);

    JobAction findById(String actionId);

    JobAction findLatestByJobId(Long jobId);

    List<JobAction> findByJobId(Long jobId);

    int updateStatus(String jobStatus);

//    Tuple<JobActionVo, JobStatus> findJobActionVo(Long jobId);
//
//    /**
//     * 查找当前版本的运行状态
//     *
//     * @param actionId
//     * @return
//     */
//    JobStatus findJobStatus(Long actionId);
//
//
//    JobStatus findJobStatusByJobId(Long jobId);
//
//
//    Integer updateStatus(Long id, String status);
//
//    Integer updateStatusAndReadDependency(JobAction JobAction);
//
//
//    List<JobAction> getAfterAction(Long action);
//
//    /**
//     * 根据jobId 获取所有的版本
//     */
//    List<Long> getActionVersionByJobId(Long jobId);
//
//    List<JobActionVo> getNotRunScheduleJob();
//
//    List<JobActionVo> getFailedJob();
//
//    List<GroupTaskVo> findByJobIds(List<Integer> idList, String startDate, String endDate, TablePageForm pageForm, String status);
//
//    void deleteHistoryRecord(Integer beforeDay);
//
//    void deleteAllHistoryRecord(Integer beforeDay);
//
//    List<JobAction> findByStartAndEnd(Long startAction, Long endAction, Integer jobId, Integer limit);
//
//    boolean deleteAction(long startAction, long endAction, Integer jobId);
//
//    JobAction findTodaySuccessByJobId(int id);
//
//    List<JobActionMani> getAllManifest(Long endAction);

}
