package com.github.INIT_SGGW.MonoTanksClient;

import com.github.INIT_SGGW.MonoTanksClient.utils.Args;
import com.github.INIT_SGGW.MonoTanksClient.websocket.CustomWebSocketClient;
import org.java_websocket.client.WebSocketClient;
import picocli.CommandLine;
import java.net.URI;
import java.net.URISyntaxException;

public class App {

    public static URI constructUrl(String host, int port, String code, String nickname)
            throws URISyntaxException {
        StringBuilder url = new StringBuilder(String.format("ws://%s:%d/?nickname=%s", host, port, nickname));

        url.append("&typeOfPacketType=string");
        url.append("&playerType=hackatonBot");

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

        System.out.println("[System] 🚀 Starting client...");

        try {
            URI uri = constructUrl(
                    arguments.getHost(),
                    arguments.getPort(),
                    arguments.getCode(),
                    arguments.getNickname());

            System.out.println("[System] 📞 Connecting to the server: " + uri);
            try {
                WebSocketClient client = new CustomWebSocketClient(uri);
                client.run();
            } catch (Exception e) {
                System.err.println("[System] 🚨 An error occurred: " + e.getMessage());
            }

        } catch (URISyntaxException e) {
            System.err.println("[System] 🚨 Invalid URI: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}