package com.cardlookup.panlookup.config;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.listener.MethodExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class DataSourceProxyConfig {

    private static final Logger log = LoggerFactory.getLogger("DSProxy");

    @Bean
    public MethodExecutionListener lifecycleLoggingListener() {
        return new MethodExecutionListener() {

            @Override
            public void beforeMethod(MethodExecutionContext executionContext) {
                if(executionContext.getMethod().getName().equals("getConnection")) {
                    log.info("Before method: {}()",
                            executionContext.getMethod().getName());
                }
            }

            @Override
            public void afterMethod(MethodExecutionContext executionContext) {

            }
        };
    }

}
