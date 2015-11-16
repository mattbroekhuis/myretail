package com.myretail.framework.docker

import com.spotify.docker.client.messages.ContainerConfig
import com.spotify.docker.client.messages.HostConfig
import com.spotify.docker.client.messages.PortBinding
import org.slf4j.Logger
import org.slf4j.LoggerFactory

ExtendedDockerClient docker = DockerConnectionFactory.instance

Logger logger = LoggerFactory.getLogger(startEnvironment)

String apiContainerName = getRequiredProp('API_CONTAINER_NAME')
String apiImageName = getRequiredProp('API_IMAGE_NAME')
Integer apiExposedRestPort = getRequiredIntProp('API_REST_PORT');

String cassandraContainerName = getRequiredProp('CASSANDRA_CONTAINER_NAME')
String cassandraImageName = getRequiredProp('CASSANDRA_IMAGE_NAME')
Integer cassandraExposedPort = getRequiredIntProp("CASSANDRA_PORT_9042_TCP_PORT")

/** Cassandra **/
//todo, opening up the external port is screwing up the format of the cassandra.yml file
//HostConfig cassandraHostConfig = HostConfig.builder().portBindings(getPortBindingsVendorToExposed([(9042): cassandraExposedPort])).build()
ContainerConfig cassandraContainerConfig = ContainerConfig.builder().image(cassandraImageName).build()

/** API **/
HostConfig apiHostConfig = HostConfig.builder().links("$cassandraContainerName:cassandra").portBindings(getPortBindingsVendorToExposed([(8080): apiExposedRestPort])).build()
ContainerConfig apiContainerConfig = ContainerConfig.builder().hostname(apiContainerName).hostConfig(apiHostConfig).image(apiImageName).build()

/** destroy previous runs **/
docker.destroyContainer(cassandraContainerName)
docker.destroyContainer(apiContainerName)

/** start containers **/
docker.createAndStart(cassandraContainerConfig, cassandraContainerName)

Thread.sleep(15000) //todo this most definitely needs to be updated to query cassandra and check that it's "alive". The driver the app server is using does not handle waiting for it well at all.
docker.createAndStart(apiContainerConfig, apiContainerName)

docker.waitForURL("http", docker.dockerHostIP(), apiExposedRestPort, "/status", 30)

logger.info("Closing docker client connection")

//this takes way too long
docker.close()

logger.info("Environment Initialized. Something is causing maven to hang here for some weird reason, need to figure out why, but it eventually gets through it. I think it is jersey in spotify client taking a long time to close connections")


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