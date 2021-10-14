package com.xxl.job.admin.core.trigger;

/**
 * 任务状态
 */
public enum JobStatusEnum {

    /**
     * 等待状态
     */
    WAIT("wait"),
    /**
     * 运行状态
     */
    RUNNING("running"),
    /**
     * 成功状态
     */
    SUCCESS("success"),
    /**
     * 失败状态
     */
    FAILED("failed");

    private String status;

    JobStatusEnum(String status) {
        this.status = status;
    }

    public static JobStatusEnum parse(String status) {
        for (JobStatusEnum s : JobStatusEnum.values()) {
            if (s.status.equalsIgnoreCase(status)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return status;
    }

}
