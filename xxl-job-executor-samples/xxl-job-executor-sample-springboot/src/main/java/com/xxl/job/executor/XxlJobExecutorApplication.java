package com.xxl.job.executor;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */
@Slf4j
@SpringBootApplication
public class XxlJobExecutorApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(XxlJobExecutorApplication.class, args);

        Environment env = application.getEnvironment();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        final String SERVER = String.format("http://localhost:%s%s", port, StrUtil.emptyIfNull(path));
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application  is running! Access URLs:\n\t" +
                        "WEB: \t {}\n\t" +
                        "----------------------------------------------------------",
                SERVER
        );
    }

}