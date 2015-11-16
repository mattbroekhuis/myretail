package com.myretail.framework.docker

import com.spotify.docker.client.DefaultDockerClient

public class DockerConnectionFactory {

    public static ExtendedDockerClient getInstance() {
        new ExtendedDockerClientImpl(DefaultDockerClient.fromEnv().build())
    }

}
