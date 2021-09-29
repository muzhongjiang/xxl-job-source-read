package com.xxl.job.core.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by xuxueli on 17/5/9.
 */
public enum ExecutorBlockStrategyEnum {

    SERIAL_EXECUTION("Serial execution"), //单机串行
    /*CONCURRENT_EXECUTION("并行"),*/
    DISCARD_LATER("Discard Later"), //丢弃后续调度
    COVER_EARLY("Cover Early"); //覆盖之前调度

    @Getter
    @Setter
    private String title;

    private ExecutorBlockStrategyEnum(String title) {
        this.title = title;
    }

    public static ExecutorBlockStrategyEnum match(String name, ExecutorBlockStrategyEnum defaultItem) {
        if (name != null) {
            for (ExecutorBlockStrategyEnum item : ExecutorBlockStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }
}
