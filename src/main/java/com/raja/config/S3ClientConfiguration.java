package com.raja.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3ClientConfiguration {

    @Autowired
    private UploadFileConfiguration configuration;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(configuration.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(configuration.getAccessKey(), configuration.getSecretKey())))
                .build();
    }
}
