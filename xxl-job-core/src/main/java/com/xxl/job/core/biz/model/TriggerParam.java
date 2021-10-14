package com.xxl.job.core.biz.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by xuxueli on 16/7/22.
 */
@Data
@Accessors(chain = true)
public class TriggerParam implements Serializable{

    private int jobId;

    private String executorHandler;
    private String executorParams;
    private String executorBlockStrategy;
    private int executorTimeout;

    private long logId;
    private long logDateTime;

    private String glueType;
    private String glueSource;
    private long glueUpdatetime;

    private int broadcastIndex;// 分片参数：当前分片
    private int broadcastTotal;// 分片参数：总分片

}
