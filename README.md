```sh
# Build the project
mvn package

# Run the project
java -jar target/MonoTanksClient-1.0-SNAPSHOT.jar --nickname Java1

# or if you are working in devcontainer
java -jar target/MonoTanksClient-1.0-SNAPSHOT.jar --nickname Java --host host.docker.internal
```

```sh
# Build docker image
docker build -t java .

# Run docker container
docker run java --nickname Java1
```