package com.myretail.webservice.product

import com.myretail.framework.cassandra.CassandraConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource

@SpringBootApplication
@Import(CassandraConfig)
@PropertySource("classpath:/module.properties")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}