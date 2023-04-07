package com.example.app;

import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppApplication {



    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

}
