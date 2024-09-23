package com.github.INIT_SGGW.MonoTanksClient;

import com.github.INIT_SGGW.MonoTanksClient.utils.Args;
import com.github.INIT_SGGW.MonoTanksClient.websocket.CustomWebSocketClient;
import org.java_websocket.client.WebSocketClient;
import picocli.CommandLine;

import java.net.URI;
import java.net.URISyntaxException;

public class App {

    public static URI constructUrl(String host, int port, String code, String nickname, boolean debugQuickJoin) throws URISyntaxException {
        StringBuilder url = new StringBuilder(String.format("ws://%s:%d/?nickname=%s", host, port, nickname));

        url.append("&typeOfPacketType=string");

        if (debugQuickJoin) {
            url.append("&quickJoin=true");
        }

        if (code != null && !code.isEmpty()) {
            url.append("&joinCode=");
            url.append(code);
        }

        return new URI(url.toString());
    }

    public static void main(String[] args) {
        System.out.println("🚀 Starting client...");

        Args arguments = new Args();
        CommandLine commandLine = new CommandLine(arguments);
        commandLine.parseArgs(args);

        try {
            URI uri = constructUrl(arguments.getHost(), arguments.getPort(), arguments.getCode(), arguments.getNickname(), arguments.isDebugQuickJoin());

            System.out.println("📞 Connecting to the server: " + uri);
            WebSocketClient client = new CustomWebSocketClient(uri);
            client.run();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
