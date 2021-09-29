package com.xxl.job.core.biz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by xuxueli on 2017-05-10 20:22:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistryParam implements Serializable {

    private String registryGroup;
    private String registryKey;
    private String registryValue;

}
