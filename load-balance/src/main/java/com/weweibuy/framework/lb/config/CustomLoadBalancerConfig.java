package com.weweibuy.framework.lb.config;

import com.weweibuy.framework.lb.endpoint.LoadBalanceEndpoint;
import com.weweibuy.framework.lb.support.LoadBalanceOperator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author durenhao
 * @date 2020/9/25 11:13
 **/
@Configuration
@EnableConfigurationProperties({LoadBalanceProperties.class})
public class CustomLoadBalancerConfig {

    @Bean
    public LoadBalanceEndpoint customLoadBalanceEndpoint() {
        return new LoadBalanceEndpoint(loadBalanceOperator());
    }


    @Bean
    public LoadBalanceOperator loadBalanceOperator() {
        return new LoadBalanceOperator();
    }


}
