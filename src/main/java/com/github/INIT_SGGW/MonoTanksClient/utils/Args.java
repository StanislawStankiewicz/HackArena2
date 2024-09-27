package com.github.INIT_SGGW.MonoTanksClient.utils;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Getter
@Setter
@Command(name = "Args", mixinStandardHelpOptions = true, description = "\nJava client written for Hackathon 2024 H2 organized by KN inIT. The client is used to communicate with the server using the WebSocket protocol. Your task is to implement bot logic. Each time the game state updates on the server, it is sent to you and you have to respond with your move. The game is played on a 2D grid. The player with the most points at the end of the game wins. Let the best bot win!\n")
public class Args {

    @Option(names = { "-n", "--nickname" }, required = true, description = "The nickname to use for the client.")
    private String nickname;

    @Option(names = { "-h",
            "--host" }, defaultValue = "localhost", description = "The host address of the server. Default is 'localhost'.")
    private String host;

    @Option(names = { "-p",
            "--port" }, defaultValue = "5000", description = "The port number to connect to. Default is 5000.")
    private int port;

    @Option(names = { "-c",
            "--code" }, description = "The join code required to connect to a password-protected server.")
    private String code;

    @Option(names = { "--help" }, usageHelp = true, description = "Display this help message and exit.")
    private boolean help;
}