package com.myretail.webservice.product

import com.myretail.framework.cassandra.CassandraMigrationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(CassandraConfig)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CassandraMigrationRunner migrationRunner() {
        new CassandraMigrationRunner()
    }
}