package com.xxl.job.core.biz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by xuxueli on 17/3/23.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogResult implements Serializable {

    private int fromLineNum;
    private int toLineNum;
    private String logContent;
    private boolean isEnd;

}
