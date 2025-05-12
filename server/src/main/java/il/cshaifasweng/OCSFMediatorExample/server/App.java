package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;

public class App {

    private static SimpleServer server;

    public static void main(String[] args) throws IOException {
        server = new SimpleServer(3000);

        // Graceful shutdown on exit (e.g., IntelliJ red square or Ctrl+C)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🛑 Shutdown signal received. Closing server...");
            server.shutdown();
        }));

        System.out.println("✅ Server is now listening on port 3000...");
        server.listen();
    }
}
