# MonoTanks API wrapper in Java for HackArena 2.0

This API wrapper for MonoTanks game for the HackArena 2.0, organized by
KN init. It is implemented as a WebSocket client written in Java and can be
used to create bots for the game.

To fully test and run the game, you will also need the game server and GUI
client, as the GUI provides a visual representation of gameplay. You can find
more information about the server and GUI client in the following repository:

- [Server and GUI Client Repository](https://github.com/INIT-SGGW/HackArena2.0-MonoTanks)

## Development

Clone this repo using git:
```sh
git clone https://github.com/INIT-SGGW/HackArena2.0-MonoTanks-Java.git
```

or download the [zip file](https://github.com/INIT-SGGW/HackArena2.0-MonoTanks-Java/archive/refs/heads/main.zip)
and extract it.

The bot logic you are going to implement is located in
`src/main/java/com/github/INIT_SGGW/MonoTanksBot/Bot/MyBot.java`:

```java
public class MyBot extends Bot {

    private final String myId;

    public MyBot(LobbyData lobbyData) {
        super(lobbyData);
        this.myId = lobbyData.playerId();
    }

    @Override
    public void onSubsequentLobbyData(LobbyData lobbyData) {

    }

    @Override
    public BotResponse nextMove(GameState gameState) {
        double random = Math.random();

        if (random < 0.25) {
            if (Math.random() < 0.5) {
                return BotResponse.createMoveResponse(MoveDirection.FORWARD);
            } else {
                return BotResponse.createMoveResponse(MoveDirection.BACKWARD);
            }
        } else if (random < 0.5) {
            double tankRandom = Math.random();
            RotationDirection tankRotation = tankRandom < 0.33 ? RotationDirection.LEFT
                    : tankRandom < 0.66 ? RotationDirection.RIGHT : null;

            double turretRandom = Math.random();
            RotationDirection turretRotation = turretRandom < 0.33 ? RotationDirection.LEFT
                    : turretRandom < 0.66 ? RotationDirection.RIGHT : null;

            return BotResponse.createRotationResponse(Optional.ofNullable(tankRotation),
                    Optional.ofNullable(turretRotation));
        } else if (random < 0.75) {
            return BotResponse.createShootResponse();
        } else {
            return BotResponse.createPassResponse();
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

The `MyBot` class extends the abstract `Bot` class, which defines the bot's
behavior. The constructor is called when the bot is created, and the
`nextMove` method is called every game tick to determine the bot's next move.
The `onGameEnd` method is called when the game ends to provide the final game
state.

`nextMove` returns an `BotResponse` object, which can be one of the following:

- `MoveResponse`: Move the tank forward or backward, where `MoveDirection` is an
  enum with the variants `FORWARD` and `BACKWARD`.
- `RotationResponse`: Rotate the tank and turret left or right, where
  `RotationDirection` is an enum with the variants `LEFT` and `RIGHT`.
- `AbilityUseResponse`: Use an ability, where `AbilityType` is an enum with
  variants such as `FIRE_BULLET`, `FIRE_DOUBLE_BULLET`, `USE_LASER`, `USE_RADAR`,
  and `DROP_MINE`.
- `PassResponse`: Do nothing for this turn.

You can create these responses using the following static methods in `BotResponse`:

You can modify the mentioned file and create more files in the
`src/main/java/com/github/INIT_SGGW/MonoTanksBot/Bot` directory. Do not
modify any other files, as this may prevent us from running your bot during
the competition.

### Including Static Files

If you need to include static files that your program should access during
testing or execution, place them in the `data` folder. This folder is copied
into the Docker image and will be accessible to your application at runtime.
For example, you could include configuration files, pre-trained models, or any
other data your bot might need.

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
mvn exec:java -Dexec.mainClass="com.github.INIT_SGGW.MonoTanksBot.App" -Dexec.args="--nickname TEAM_NAME"
```

The `--nickname` argument is required and must be unique. For additional
configuration options, run:

```sh
mvn exec:java -Dexec.mainClass="com.github.INIT_SGGW.MonoTanksBot.App" -Dexec.args="--help"
```

> **Note:** In case of java, the server must be started with flag `--host *` or `--host \*`, whatever works.

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
Code's integrated terminal, as if you were running the project locally. However,
when server is running on local machine, you need to use `host.docker.internal`
as a host. So the command to run the client would be:

```sh
mvn exec:java -Dexec.mainClass="com.github.INIT_SGGW.MonoTanksBot.App" -Dexec.args="--host host.docker.internal --nickname TEAM_NAME"
```

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
   docker run --rm client --host host.docker.internal --nickname TEAM_NAME
   ```

If the server is running on your local machine, use the
`--host host.docker.internal` flag to connect the Docker container to your local
host.
