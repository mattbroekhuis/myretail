### Dependencies:
1. OSX - This should be platform independent, but only tested in OSX
2. Maven 3.3.3 - the build is actually enforced to use this, if you have an old version and just want to try it out you can change it in the framework superpom
3. Java 8 - older version will likely work, but tested with 8
4. Docker 1.9.x - Docker Machine is the way to get this for OSX

### Build instructions
1. Install Java and Maven. Easiest way on OSX is to get [homebrew](http://brew.sh/) and use it.  [Java install](http://stackoverflow.com/questions/24342886/how-to-install-java-8-on-mac) [Maven install](http://stackoverflow.com/a/17481557/412366)
2. Get docker-machine via the [Docker tool box](https://www.docker.com/docker-toolbox)
3. Initiate a docker-machine by running a command like below. I have 16gb of ram (hopefully you do too) and use docker all the time so I like a big memory capacity, you can get away with less likely. The last argument is the name of the machine. I use the name "local" . You will use this name for subsequent docker-machine commands
```bash
docker-machine create --driver=virtualbox --virtualbox-cpu-count "2" --virtualbox-memory "8000" --virtualbox-cpu-count "4" --virtualbox-disk-size "40000" local
```
4. Type in the command `docker ps`, and you should get a list of no running containers
5. Test out that you can pull an image from docker hub. Lets pull cassandra since that's what this demo uses: `docker pull cassandra:2.2.3` . If that doesn't work you might have some corporate proxy issues to deal with.

6. Run the build `mvn clean install` The first run of this build takes a long time as it downloads a lot of dependencies from both docker hub and maven central. Subsequent builds should come in around the 2 minute mark.
7. After the build runs, it leaves it's integration test resources running so you can inspect their state. Execute the command `docker ps -a` again to see what is running (the -a includes things that are not running, but if the build worked everything should be running). You can access the api @ the docker host's ip address on port 3000. You can get the docker machine's ip by executing `docker-machine ip local`. eg 192.168.99.100:3000
8. Access the api @ http://192.168.99.100:3000/product/13860428


### Running build in intellij
The maven build uses the [spotify docker client](https://github.com/spotify/docker-client)
The spotify client is simply a java client of the docker rest api. The benefit of this is not having to make native system calls to the docker executable from the build process. You can then use groovy instead of bash scripts etc
Since docker is running in a VM when you use docker-machine, the spotify client will look for an environment variables DOCKER_HOST and DOCKER_CERT_PATH. These environment variables should be set by docker-machine. If they aren't you can run `eval "$(docker-machine env local)"` I put this line in my .bash_profile
If you attempt to run the build in intellij via the maven plugin you will have to set these variables yourself in intellij configuration, or alternatively launch intellij from the shell so they are inherited (Another thing I put in my .bash_profile) `alias idea="/Applications/IntelliJ\ IDEA\ 15.app/Contents/MacOS/idea &"` (adjust for your version)



### General dev instructions (assuming Intellij is used)
Running the build is fine, but a lot of times you want to rapidly start and restart the services, debug them etc.
I've actually gone through the exercise of opening up the debug port into the docker container -- and that does work -- but it's still faster to iterate when what you're working on is outside of docker

We can still run any dependencies we may need in docker. In this case, the only dependency is cassandra.
In order to do so, execute the script `restartCassandra.sh` located in this repo by right clicking on it and running in Intellij. this starts up another instance of cassandra with default cql port 9042 open

Then create a run config for Application.java and pass in two system properties (use docker-machine ip output from previous step) `-DCASSANDRA_PORT_9042_TCP_ADDR=192.168.99.100 -DCASSANDRA_PORT_9042_TCP_PORT=9042`

Now, you have two environments. Your "local" one you iterate on for testing. And your "build one" that results from your maven commands. Also, this allows you to run the integration tests by simply right clicking on them in Intellij

### Other notes
instead of passing around ip addresses, I typically will update the local /etc/hosts to make aliases for the dependencies to the docker host. eg add an entry for hostname cassandra -> 192.168.99.100 . was considering doing this as part of the instructions, but oddly enough i've found a lot of companies lock out admin rights to their employees