package com.github.INIT_SGGW.MonoTanksClient.utils;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Option;

@Getter
@Setter
public class Args {

    @Option(names = { "-n", "--nickname" }, required = true, description = "Nickname")
    private String nickname;

    @Option(names = { "-h", "--host" }, defaultValue = "localhost", description = "Host address")
    private String host;

    @Option(names = { "-p", "--port" }, defaultValue = "5000", description = "Port number")
    private int port;

    @Option(names = { "-c", "--code" }, description = "Join code needed to join a server with a password")
    private String code;

    @Option(names = {
            "--debug-quick-join" }, defaultValue = "false", description = "Enable debug quick join (as soon as you connect to the server, game will start)")
    private boolean debugQuickJoin;
}