# Java WebSocket Client for Hackathon 2024

This Java-based WebSocket client was developed for the Hackathon 2024, organized
by WULS-SGGW. It serves as a framework for participants to create AI agents that
can play the game.

To fully test and run the game, you will also need the game server and GUI
client, as the GUI provides a visual representation of gameplay. You can find
more information about the server and GUI client in the following repository:

- [Server and GUI Client Repository](https://github.com/INIT-SGGW/HackArena2024H2-Game)

## Development

The agent logic you are going to implement is located in
`src/main/java/com/github/INIT_SGGW/MonoTanksClient/Agent/MyAgent.java`:

```java
public class MyAgent extends Agent {

    private final String myId;

    public MyAgent(LobbyData lobbyData) {
        super(lobbyData);
        this.myId = lobbyData.playerId();
    }

    @Override
    public void onSubsequentLobbyData(LobbyData lobbyData) {

    }

    @Override
    public AgentResponse nextMove(GameState gameState) {
        double random = Math.random();

        if (random < 0.25) {
            if (Math.random() < 0.5) {
                return AgentResponse.createMoveResponse(MoveDirection.FORWARD);
            } else {
                return AgentResponse.createMoveResponse(MoveDirection.BACKWARD);
            }
        } else if (random < 0.5) {
            double tankRandom = Math.random();
            RotationDirection tankRotation = tankRandom < 0.33 ? RotationDirection.LEFT
                    : tankRandom < 0.66 ? RotationDirection.RIGHT : null;

            double turretRandom = Math.random();
            RotationDirection turretRotation = turretRandom < 0.33 ? RotationDirection.LEFT
                    : turretRandom < 0.66 ? RotationDirection.RIGHT : null;

            return AgentResponse.createRotationResponse(Optional.ofNullable(tankRotation),
                    Optional.ofNullable(turretRotation));
        } else if (random < 0.75) {
            return AgentResponse.createShootResponse();
        } else {
            return AgentResponse.createPassResponse();
        }
    }

    @Override
    public void onGameEnd(GameEnd gameEnd) {
        System.out.println("Game ended");
        GameEndPlayer winner = gameEnd.players()[0];
        if (winner.id().equals(this.myId)) {
            System.out.println("I won!");
        }

        for (GameEndPlayer player : gameEnd.players()) {
            System.out.println(player.nickname() + " - " + player.score());
        }
    }
}
```

The `Agent` class extends the `AgentTrait` interface, which defines the agent's
behavior. The constructor is called when the agent is created, and the
`nextMove` method is called every game tick to determine the agent's next move.
The `onGameEnd` method is called when the game ends to provide the final game
state.

`nextMove` returns an `AgentResponse` object, which can be one of the following:

- `MoveResponse`: Move the tank forward or backward, where `MoveDirection` is an
  enum with the variants `FORWARD` and `BACKWARD`.
- `RotationResponse`: Rotate the tank and turret left or right, where
  `RotationDirection` is an enum with the variants `LEFT` and `RIGHT`.
- `ShootResponse`: Shoot a projectile in the direction the turret is facing.

You can modify the mentioned file and create more files in the
`src/main/java/com/github/INIT_SGGW/MonoTanksClient/Agent` directory. Do not
modify any other files, as this may prevent us from running your agent during
the competition.

## Running the Client

You can run this client in three different ways: locally, within a VS Code
development container, or manually using Docker.

### 1. Running Locally

To run the client locally, you must have Java 21 or later installed. Verify your
Java version by running:

```sh
java -version
```

Assuming the game server is running on `localhost:5000` (refer to the server
repository's README for setup instructions), start the client by running:

```sh
mvn exec:java -Dexec.mainClass="com.github.INIT_SGGW.MonoTanksClient.App" -Dexec.args="--nickname java"
```

The `--nickname` argument is required and must be unique. For additional
configuration options, run:

```sh
mvn exec:java -Dexec.mainClass="com.github.INIT_SGGW.MonoTanksClient.App" -Dexec.args="--help"
```

### 2. Running in a VS Code Development Container

To run the client within a VS Code development container, ensure you have Docker
and Visual Studio Code (VS Code) installed, along with the Dev Containers
extension.

Steps:

1. Open the project folder in VS Code.
2. If prompted, choose to reopen the project in a development container and wait
   for the setup to complete.
3. If not prompted, manually reopen the project in a container by:
   - Opening the command palette `F1`
   - Searching for and selecting `>Dev Containers: Reopen in Container`

Once the container is running, you can execute all necessary commands in VS
Code's integrated terminal, as if you were running the project locally.

### 3. Running in a Docker Container (Manual Setup)

To run the client manually in a Docker container, ensure Docker is installed on
your system.

Steps:

1. Build the Docker image:
   ```sh
   docker build -t client .
   ```
2. Run the Docker container:
   ```sh
   docker run --rm client --nickname java --host host.docker.internal
   ```

If the server is running on your local machine, use the
`--host host.docker.internal` flag to connect the Docker container to your local
host.
