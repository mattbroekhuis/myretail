package com.myretail.framework.cassandra

import com.contrastsecurity.cassandra.migration.CassandraMigration
import com.contrastsecurity.cassandra.migration.config.Keyspace
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.core.env.Environment

/**
 * runs on spring startup. There might be a better way to get into the lifecycle than this.
 */
class CassandraMigrationRunner implements ApplicationListener<ContextRefreshedEvent> {

    Logger logger = LoggerFactory.getLogger(CassandraMigrationRunner)

    @Autowired
    private Environment environment

    private boolean lock;

    void migrate() {
        logger.info("Applying Cassandra Migrations")
        String module = environment.getProperty("module.key", "product")
        String host = environment.getRequiredProperty("CASSANDRA_PORT_9042_TCP_ADDR")
        Integer port = environment.getRequiredProperty("CASSANDRA_PORT_9042_TCP_PORT") as Integer
        String username = environment.getProperty("CASSANDRA_USERNAME", "cassandra")
        String password = environment.getProperty("CASSANDRA_PASSWORD", "cassandra")



        Keyspace keySpace = new Keyspace();
        keySpace.setName(module);
        keySpace.getCluster().setContactpoints(host);
        keySpace.getCluster().setPort(port);
        keySpace.getCluster().setUsername(username);
        keySpace.getCluster().setPassword(password);

        CassandraMigration cm = new CassandraMigration();
        cm.setKeyspace(keySpace);
        cm.migrate();
    }

    @Override
    void onApplicationEvent(ContextRefreshedEvent event) {
        if (!lock) {
            lock = true
            migrate()
        }
    }
}