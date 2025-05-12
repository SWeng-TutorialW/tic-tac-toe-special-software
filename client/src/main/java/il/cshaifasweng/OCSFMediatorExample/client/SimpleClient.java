package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;

public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;
	private static String host = "localhost";
	private static int port = 3000;

	public static void configure(String h, int p) {
		host = h;
		port = p;
		client = null;
	}

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg instanceof Warning) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		} else {
			String message = msg.toString();
			System.out.println("Server message: " + message);

			if (message.equals("FULL")) {
				Platform.runLater(() -> {
					Alert alert = new Alert(Alert.AlertType.ERROR, "❌ Game is full. Only 2 players are allowed.");
					alert.showAndWait();
					Platform.exit();
				});
				return;
			}

			if (message.startsWith("SYMBOL")) {
				String[] parts = message.split(",");
				String symbol = parts[1];
				Platform.runLater(() -> {
					PrimaryController.playerSymbol = symbol;
					PrimaryController.isMyTurn = symbol.equals("X");
					System.out.println("Assigned symbol: " + symbol);
				});

			} else if (message.startsWith("MOVE")) {
				String[] parts = message.split(",");
				int row = Integer.parseInt(parts[1]);
				int col = Integer.parseInt(parts[2]);
				String symbol = parts[3];

				Platform.runLater(() -> {
					if (PrimaryController.instance != null) {
						PrimaryController.instance.applyMove(row, col, symbol);
						if (!symbol.equals(PrimaryController.playerSymbol)) {
							PrimaryController.isMyTurn = true;
						}
					}
				});
			}
		}
	}

	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient(host, port);
		}
		return client;
	}

	public boolean tryOpen() {
		try {
			super.openConnection();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
