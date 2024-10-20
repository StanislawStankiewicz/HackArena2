package com.github.INIT_SGGW.MonoTanksClient;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.INIT_SGGW.MonoTanksClient.utils.Args;
import com.github.INIT_SGGW.MonoTanksClient.websocket.CustomWebSocketClient;

import picocli.CommandLine;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static CustomWebSocketClient client;

    public static URI constructUrl(String host, int port, String code, String nickname)
            throws URISyntaxException {
        StringBuilder url = new StringBuilder(String.format("ws://%s:%d/?nickname=%s", host, port, nickname));

        url.append("&enumSerializationFormat=string");
        url.append("&playerType=hackathonBot");

        if (code != null && !code.isEmpty()) {
            url.append("&joinCode=");
            url.append(code);
        }

        return new URI(url.toString());
    }

    public static void main(String[] args) {
        Args arguments = new Args();
        CommandLine commandLine = new CommandLine(arguments);
        commandLine.parseArgs(args);

        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            return;
        }

        logger.info("🚀 Starting client...");

        String serverAddress;
        try {
            serverAddress = InetAddress.getByName(arguments.getHost()).getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("🚨 Invalid server address: {}", e.getMessage(), e);
            return;
        }

        URI uri;
        try {
            uri = constructUrl(
                    serverAddress,
                    arguments.getPort(),
                    arguments.getCode(),
                    arguments.getNickname());
        } catch (URISyntaxException e) {
            logger.error("🚨 Invalid URI: {}", e.getMessage());
            return;
        }

        try {
            logger.info("📞 Connecting to the server: {}", uri);
            client = new CustomWebSocketClient(uri);

            // Add simplified shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("⚠️ Shutdown signal received");
                if (client != null && client.isOpen()) {
                    client.close();
                }
            }));

            client.run();
        } catch (Exception e) {
            logger.error("🚨 An error occurred: {}", e.getMessage(), e);
        }
    }
}
