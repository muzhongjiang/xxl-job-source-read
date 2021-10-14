package com.xxl.job.admin.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * job action
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class JobAction {

    private Long id;

    private Integer jobId;

//    private Integer auto = 0;
//
//    private String configs;
//
//    private String cronExpression;// 应该执行的时间，jobId+cronExpression 唯一
//
//    private String cycle;
//
//    private String dependencies;
//
//    private String jobDependencies;
//
//    private String description;
//
//    private Integer groupId;
//
//    private String host;
//
//    private int hostGroupId;

    private String lastResult;

//    private String resources;//资源文件
//    private String runType;
//    private String script;


    private String status;

    private int batchId;//20211011

    private long cronTime;	// 调度生命周期中当前执行周期时间
    private Date createTime;
    private Date updateTime;


//======================================================================================================


    private int jobGroup;        // 执行器主键ID
    private String jobDesc;  //描述（或者名称）

    private String author;        // 负责人
    private String alarmEmail;    // 告警邮件

    private String scheduleType;            // 调度类型
    private String scheduleConf;            // 调度配置，值含义取决于调度类型
    private String misfireStrategy;            // 调度过期策略

    private String triggerType;    // TriggerTypeEnum
    private String executorRouteStrategy;    // 执行器路由策略
    private String executorHandler;            // 执行器，任务Handler名称
    private String executorParam;            // 执行器，任务参数
    private String executorBlockStrategy;    // 阻塞处理策略
    private int executorTimeout;            // 任务执行超时时间，单位秒
    private int executorFailRetryCount;        // 失败重试次数

    private String glueType;        // GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
    private String glueSource;        // GLUE源代码
    private String glueRemark;        // GLUE备注
    private Date glueUpdatetime;    // GLUE更新时间

    private String childJobId;        // 子任务ID，多个逗号分隔

    private int triggerStatus;        // 调度状态：0-停止，1-运行
    private long triggerLastTime;    // 上次调度时间
//    private long triggerNextTime;    // 下次调度时间


}
