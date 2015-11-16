Requirements:
Maven 3.3.3 -- the build is actually enforced to use this, if you have an old version and just want to try it out you can change it in the framework superpom
Docker 1.9.x
Java 8

This build is coupled to docker. It uses the docker spotify client.
The spotify client is simply a client of the docker rest api. The benefit of this is not having to make system calls from the build process.
Unless the build is done in linux, the client will look for an environment variable DOCKER_HOST and DOCKER_CERT_PATH.
These environment variables should be set by docker-machine. If they aren't you can run eval "$(docker-machine env local)". A good line to put in your .bash_profile
If you attempt to run the build in intellij, you will have to set these variables yourself in intellij configuration, or alternatively launch intellij from the shell so they are inherited

The first run of this build takes a very long time as it downloads a lot of dependencies.  This could be trimmed by


brew install python
pip install cql