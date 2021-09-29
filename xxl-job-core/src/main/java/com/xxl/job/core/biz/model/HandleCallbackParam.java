package com.xxl.job.core.biz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by xuxueli on 17/3/2.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandleCallbackParam implements Serializable {

    private long logId;
    private long logDateTim;

    private int handleCode;
    private String handleMsg;

}
