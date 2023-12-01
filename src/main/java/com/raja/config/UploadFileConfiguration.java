package com.raja.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties
@Configuration
@Getter
@Setter
public class UploadFileConfiguration {

    private String region;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String mainFolder;

}
