package com.xxl.job.core.biz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xuxueli 2020-04-11 22:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdleBeatParam implements Serializable {

    private int jobId;
    
}