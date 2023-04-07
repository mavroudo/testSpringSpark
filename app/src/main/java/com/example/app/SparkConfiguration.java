package com.example.app;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:application.properties")
@ConditionalOnProperty(
        value = "database",
        havingValue = "s3",
        matchIfMissing = true
)
public class SparkConfiguration {
    @Value("${app.name:siesta2}")
    private String appName;

    @Value("${master.uri:local[*]}")
    private String masterUri;

    @Value("${s3.user:minioadmin}")
    private String s3user;

    @Value("${s3.key:minioadmin}")
    private String s3key;

    @Value("${s3.timeout:600000}")
    private String s3timeout;

    @Value("${s3.endpoint:http://127.0.0.1:9000}")
    private String s3endpoint;

    @Bean
    public SparkConf sparkConf() {
        return new SparkConf()
                .setAppName(appName)
                .setMaster(masterUri)
                .set("fs.s3a.endpoint", s3endpoint)
                .set("fs.s3a.access.key", s3user)
                .set("fs.s3a.secret.key", s3key)
                .set("fs.s3a.connection.timeout", s3timeout)
                .set("fs.s3a.path.style.access", "true")
                .set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
                .set("fs.s3a.connection.ssl.enabled", "true")
                .set("fs.s3a.bucket.create.enabled", "true")
                .set("spark.sql.sources.partitionOverwriteMode", "dynamic");

    }

    @Bean
    public JavaSparkContext javaSparkContext() {
        return new JavaSparkContext(this.sparkSession().sparkContext());
    }

    @Bean
    public SparkSession sparkSession() {
        return SparkSession
                .builder()
                .config(this.sparkConf())
                .getOrCreate();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
