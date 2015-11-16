package com.myretail.webservice.product

import com.datastax.driver.core.Session
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cassandra.config.java.AbstractCqlTemplateConfiguration
import org.springframework.cassandra.core.keyspace.CreateKeyspaceSpecification
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.cassandra.core.CassandraTemplate
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories

@Configuration
@EnableCassandraRepositories(basePackages = ["com.myretail"])
class CassandraConfig extends AbstractCqlTemplateConfiguration {

    @Autowired
    private Environment env

    @Override
    protected String getKeyspaceName() {
        return "product"
    }

    @Override
    protected String getContactPoints() {
        env.getRequiredProperty("CASSANDRA_PORT_9042_TCP_ADDR")
    }

    @Override
    protected int getPort() {
        env.getRequiredProperty("CASSANDRA_PORT_9042_TCP_PORT") as int
    }

    @Bean
    public CassandraTemplate cassandraTemplate(Session session) {
        new CassandraTemplate(session)
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        [new CreateKeyspaceSpecification(getKeyspaceName()).ifNotExists()]
    }
}