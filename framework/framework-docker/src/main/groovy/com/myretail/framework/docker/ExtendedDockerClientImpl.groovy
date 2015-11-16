package com.myretail.framework.docker

import com.google.common.base.Preconditions
import com.google.common.base.Stopwatch
import com.spotify.docker.client.*
import com.spotify.docker.client.messages.*
import org.apache.commons.lang.StringUtils
import org.apache.commons.validator.routines.InetAddressValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Path
import java.util.concurrent.TimeUnit

class ExtendedDockerClientImpl implements ExtendedDockerClient {
    Logger logger = LoggerFactory.getLogger(ExtendedDockerClientImpl)
    private DockerClient wrapped

    ExtendedDockerClientImpl(DockerClient wrapped) {
        this.wrapped = Preconditions.checkNotNull(wrapped)
    }

    @Override
    public void destroyContainer(String name) {
        DockerClient docker;
        try {
            docker = DockerConnectionFactory.instance
            docker.stopContainer(name, 0);
            docker.removeContainer(name);
        } catch (ContainerNotFoundException cne) {
            logger.trace("container doesn't exist", cne);
        } finally {
            if (null != docker) {
                docker.close();
            }
        }
    }

    public static String getDockerHostIP() {
        String withProtocolAndPort = Preconditions.checkNotNull(System.getenv("DOCKER_HOST"), "You must set DOCKER_HOST!", null)
        String plainIp = StringUtils.substringBefore(StringUtils.substringAfter(withProtocolAndPort, "tcp://"), ":")
        Preconditions.checkArgument(InetAddressValidator.instance.isValid(plainIp), "Ip address %s is not valid!", plainIp)
        plainIp
    }

    @Override
    public String dockerHostIP() {
        getDockerHostIP()
    }

    @Override
    public void waitForURL(String scheme, String host, int port, String path, Long timeoutSeconds) {
        URL url = new URL(scheme, host, port, path);
        Stopwatch timer = Stopwatch.createStarted();
        Long start = System.currentTimeMillis();

        Long max = start + (timeoutSeconds * 1000);
        logger.info("waiting for app to start @ {}....", url);
        while (max > System.currentTimeMillis()) {
            Long elapsed = System.currentTimeMillis() - start;
            if (elapsed > 1000) {
                if (pingServer(url)) {
                    System.out.println("\n");
                    logger.info("app server started in {} seconds", timer.elapsed(TimeUnit.SECONDS));
                    return;
                }
            }
        }
        throw new IllegalStateException(String.format("app server did not start under max start time of  %s", timeoutSeconds));
    }

    @Override
    public boolean pingServer(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int code = connection.getResponseCode();
            return code == 200;
        } catch (IOException e) {
            logger.trace("app server not running @ {} ??", url, e);
        }
        return false;
    }


    @Override
    void createAndStart(ContainerConfig containerConfig, String containerName) {

        Stopwatch creation = Stopwatch.createStarted();
        if (!imageExists(containerConfig.image())) {
            logger.info("Pulling image {}", containerConfig.image())
            pull(containerConfig.image(), new AnsiProgressHandler())
        }
        ContainerCreation containerCreation = createContainer(containerConfig, containerName);
        String containerId = containerCreation.id();
        startContainer(containerId);

        logger.info("create container {} with id {} in {} seconds", containerName, containerId, creation.elapsed(TimeUnit.SECONDS));

    }


    @Override
    boolean imageExists(String name) {
        def tags = allImageTags()
        tags.contains(name) || tags.contains("$name:latest" as String)
    }

    @Override
    List<String> allImageTags() {
        List<String> all = []
        listImages(DockerClient.ListImagesParam.allImages(true)).each {
            all.addAll(it.repoTags())
        }
        all
    }

    /** delegation **/

    @Override
    String ping() throws DockerException, InterruptedException {
        wrapped.ping()
    }

    @Override
    Version version() throws DockerException, InterruptedException {
        wrapped.version()
    }

    @Override
    int auth(AuthConfig authConfig) throws DockerException, InterruptedException {
        return 0
    }

    @Override
    Info info() throws DockerException, InterruptedException {
        wrapped.info()
    }

    @Override
    List<Container> listContainers(DockerClient.ListContainersParam... params) throws DockerException, InterruptedException {
        wrapped.listContainers(params)
    }

    @Override
    List<Image> listImages(DockerClient.ListImagesParam... params) throws DockerException, InterruptedException {
        wrapped.listImages(params)
    }

    @Override
    ContainerInfo inspectContainer(String containerId) throws DockerException, InterruptedException {
        wrapped.inspectContainer(containerId)
    }

    @Override
    ContainerCreation commitContainer(String containerId, String repo, String tag, ContainerConfig config, String comment, String author) throws DockerException, InterruptedException {
        wrapped.commitContainer(containerId, repo, tag, config, comment, author)
    }

    @Override
    ImageInfo inspectImage(String image) throws DockerException, InterruptedException {
        wrapped.inspectImage(image)
    }

    @Override
    List<RemovedImage> removeImage(String image) throws DockerException, InterruptedException {
        wrapped.removeImage(image)
    }

    @Override
    List<RemovedImage> removeImage(String image, boolean force, boolean noPrune) throws DockerException, InterruptedException {
        wrapped.removeImage(image, force, noPrune)
    }

    @Override
    List<ImageSearchResult> searchImages(String term) throws DockerException, InterruptedException {
        wrapped.searchImages(term)
    }

    @Override
    void pull(String image) throws DockerException, InterruptedException {
        wrapped.pull(image)
    }

    @Override
    void pull(String image, ProgressHandler handler) throws DockerException, InterruptedException {
        wrapped.pull(image, handler)
    }

    @Override
    void pull(String image, AuthConfig authConfig) throws DockerException, InterruptedException {
        wrapped.pull(image, authConfig)
    }

    @Override
    void pull(String image, AuthConfig authConfig, ProgressHandler handler) throws DockerException, InterruptedException {
        wrapped.pull(image, authConfig, handler)
    }

    @Override
    void push(String image) throws DockerException, InterruptedException {
        wrapped.push(image)
    }

    @Override
    void push(String image, ProgressHandler handler) throws DockerException, InterruptedException {
        wrapped.push(image, handler)
    }

    @Override
    void tag(String image, String name) throws DockerException, InterruptedException {
        wrapped.tag(image, name)
    }

    @Override
    void tag(String image, String name, boolean force) throws DockerException, InterruptedException {
        wrapped.tag(image, name, force)
    }

    @Override
    String build(Path directory, DockerClient.BuildParameter... params) throws DockerException, InterruptedException, IOException {
        wrapped.build(directory, params)
    }

    @Override
    String build(Path directory, String name, DockerClient.BuildParameter... params) throws DockerException, InterruptedException, IOException {
        wrapped.build(directory, name, params)
    }

    @Override
    String build(Path directory, ProgressHandler handler, DockerClient.BuildParameter... params) throws DockerException, InterruptedException, IOException {
        wrapped.build(directory, handler, params)
    }

    @Override
    String build(Path directory, String name, ProgressHandler handler, DockerClient.BuildParameter... params) throws DockerException, InterruptedException, IOException {
        wrapped.build(directory, name, handler, params)
    }

    @Override
    String build(Path directory, String name, String dockerfile, ProgressHandler handler, DockerClient.BuildParameter... params) throws DockerException, InterruptedException, IOException {
        wrapped.build(directory, name, dockerfile, handler, params)
    }

    @Override
    ContainerCreation createContainer(ContainerConfig config) throws DockerException, InterruptedException {
        wrapped.createContainer(config)
    }

    @Override
    ContainerCreation createContainer(ContainerConfig config, String name) throws DockerException, InterruptedException {
        wrapped.createContainer(config, name)
    }

    @Override
    void startContainer(String containerId) throws DockerException, InterruptedException {
        wrapped.startContainer(containerId)
    }

    @Override
    void stopContainer(String containerId, int secondsToWaitBeforeKilling) throws DockerException, InterruptedException {
        wrapped.stopContainer(containerId, secondsToWaitBeforeKilling)
    }

    @Override
    void pauseContainer(String containerId) throws DockerException, InterruptedException {
        wrapped.pauseContainer(containerId)
    }

    @Override
    void unpauseContainer(String containerId) throws DockerException, InterruptedException {
        wrapped.unpauseContainer(containerId)
    }

    @Override
    void restartContainer(String containerId) throws DockerException, InterruptedException {
        wrapped.restartContainer(containerId)
    }

    @Override
    void restartContainer(String containerId, int secondsToWaitBeforeRestart) throws DockerException, InterruptedException {
        wrapped.restartContainer(containerId, secondsToWaitBeforeRestart)
    }

    @Override
    ContainerExit waitContainer(String containerId) throws DockerException, InterruptedException {
        wrapped.waitContainer(containerId)
    }

    @Override
    void killContainer(String containerId) throws DockerException, InterruptedException {
        wrapped.killContainer(containerId)
    }

    @Override
    void removeContainer(String containerId) throws DockerException, InterruptedException {
        wrapped.removeContainer(containerId)
    }

    @Override
    void removeContainer(String containerId, boolean removeVolumes) throws DockerException, InterruptedException {
        wrapped.removeContainer(containerId, removeVolumes)
    }

    @Override
    InputStream exportContainer(String containerId) throws DockerException, InterruptedException {
        wrapped.exportContainer(containerId)
    }

    @Override
    InputStream copyContainer(String containerId, String path) throws DockerException, InterruptedException {
        wrapped.copyContainer(containerId, path)
    }

    @Override
    LogStream logs(String containerId, DockerClient.LogsParam... params) throws DockerException, InterruptedException {
        wrapped.logs(containerId, params)
    }

    @Override
    String execCreate(String containerId, String[] cmd, DockerClient.ExecParameter... params) throws DockerException, InterruptedException {
        wrapped.execCreate(containerId, cmd, params)
    }

    @Override
    LogStream execStart(String execId, DockerClient.ExecStartParameter... params) throws DockerException, InterruptedException {
        wrapped.execStart(execId, params)
    }

    @Override
    ExecState execInspect(String execId) throws DockerException, InterruptedException {
        wrapped.execInspect(execId)
    }

    @Override
    ContainerStats stats(String containerId) throws DockerException, InterruptedException {
        wrapped.stats(containerId)
    }

    @Override
    void close() {
        wrapped.close()
    }

    @Override
    LogStream attachContainer(String containerId, DockerClient.AttachParameter... params) throws DockerException, InterruptedException {
        wrapped.attachContainer(containerId, params)
    }

    @Override
    String getHost() {
        wrapped.getHost()
    }
}
