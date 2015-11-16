package com.myretail.framework.docker

import com.spotify.docker.client.messages.ContainerConfig
import com.spotify.docker.client.messages.HostConfig
import com.spotify.docker.client.messages.PortBinding
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.TimeUnit

ExtendedDockerClient docker = DockerConnectionFactory.instance

Logger logger = LoggerFactory.getLogger(startEnvironment)

String apiContainerName = getRequiredProp('API_CONTAINER_NAME')
String apiImageName = getRequiredProp('API_IMAGE_NAME')
Integer apiExposedRestPort = getRequiredIntProp('API_REST_PORT');

String cassandraContainerName = getRequiredProp('CASSANDRA_CONTAINER_NAME')
String cassandraImageName = getRequiredProp('CASSANDRA_IMAGE_NAME')
Integer cassandraExposedPort = getRequiredIntProp("CASSANDRA_PORT_9042_TCP_PORT")

/** Cassandra **/
HostConfig cassandraHostConfig = HostConfig.builder().portBindings(getPortBindingsVendorToExposed([(9042): cassandraExposedPort])).build()
ContainerConfig cassandraContainerConfig = ContainerConfig.builder().image(cassandraImageName).build()

/** API **/
HostConfig apiHostConfig = HostConfig.builder().links("$cassandraContainerName:cassandra").portBindings(getPortBindingsVendorToExposed([(8080): apiExposedRestPort])).build()
ContainerConfig apiContainerConfig = ContainerConfig.builder().hostname(apiContainerName).hostConfig(apiHostConfig).image(apiImageName).build()

/** destroy previous runs **/
docker.destroyContainer(cassandraContainerName)
docker.destroyContainer(apiContainerName)

/** start containers **/
docker.createAndStart(cassandraContainerConfig, cassandraContainerName)

Thread.sleep(15000)
docker.createAndStart(apiContainerConfig, apiContainerName)

docker.waitForURL("http", docker.dockerHostIP(), apiExposedRestPort, "/status", 30)

logger.info("Closing docker client connection (need to figure out why this is taking so long, probably connection pool)")

//this takes way too long
//docker.close()

logger.info("Environment Initialized")


static Map<String, List<PortBinding>> getPortBindingsVendorToExposed(Map<Integer, Integer> portBindings) {
    final Map<String, List<PortBinding>> builderBindings = new HashMap<>()
    for (Map.Entry<Integer, Integer> o : portBindings.entrySet()) {
        Integer vendorDefaultPort = o.getKey()
        Integer exposedPortToUse = o.getValue()
        PortBinding http = PortBinding.of("", exposedPortToUse)
        builderBindings.put(vendorDefaultPort.toString() + "/tcp", [http])
    }
    return builderBindings;
}


static Integer getRequiredIntProp(String key) {
    String result = getProp(key, true);
    if (null != result && result.isInteger()) {
        return result as int;
    }
    return null;
}

static String getRequiredProp(String key) {
    return getProp(key, true);
}

static String getProp(String key, boolean required) {

    String value = null;
    if (null == key) {
        throw new IllegalArgumentException("null key not permitted");
    }

    if (System.getProperty(key)) {
        value = System.getProperty(key);
    } else if (System.getenv(key)) {
        value = System.getenv(key);
    } else if (required) {
        throw new Exception("unable to resolve property " + key)
    }

    return value;

}