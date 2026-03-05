package com.seckill.core.boot.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger API文档配置
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket createApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sk.core"))
                .paths(PathSelectors.any())
                .build()
                .groupName("秒杀核心API");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("秒杀核心系统API文档")
                .description("秒杀核心业务接口文档")
                .version("1.0.0")
                .build();
    }
}