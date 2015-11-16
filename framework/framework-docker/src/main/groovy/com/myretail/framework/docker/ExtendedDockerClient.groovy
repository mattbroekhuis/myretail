package com.myretail.framework.docker

import com.spotify.docker.client.DockerClient
import com.spotify.docker.client.messages.ContainerConfig

interface ExtendedDockerClient extends DockerClient {
    /**
     * stops and removes container, ok to use if container isn't running or doesn't exist
     * @param name container id you want to get rid of
     */
    void destroyContainer(String name)

    /**
     * parses the docker host ip from the DOCKER_HOST environment variable
     * @return
     */
    String dockerHostIP()

    void waitForURL(String scheme, String host, int port, String path, Long timeoutSeconds)

    boolean pingServer(URL url)

    void createAndStart(ContainerConfig containerConfig, String containerName)

    boolean imageExists(String name)

    List<String> allImageTags()
}
