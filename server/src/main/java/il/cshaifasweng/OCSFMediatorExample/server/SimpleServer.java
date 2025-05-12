package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	public SimpleServer(int port) {
		super(port);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();

		if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Warning from server!");
			try {
				client.sendToClient(warning);
				System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (msgString.equals("add client")) {
			if (SubscribersList.size() >= 2) {
				try {
					client.sendToClient("FULL");  // notify client that game is full
					client.close();               // disconnect the extra client
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}

			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);

			String assignedSymbol = (SubscribersList.size() == 1) ? "X" : "O";
			try {
				client.sendToClient("SYMBOL," + assignedSymbol);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (msgString.startsWith("remove client")) {
			if (!SubscribersList.isEmpty()) {
				SubscribersList.removeIf(subscribedClient -> subscribedClient.getClient().equals(client));
			}
		} else if (msgString.startsWith("MOVE")) {
			for (SubscribedClient subscribedClient : SubscribersList) {
				try {
					subscribedClient.getClient().sendToClient(msgString);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void sendToAllClients(String message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	public void shutdown() {
		try {
			close(); // from AbstractServer
			System.out.println("🔴 Server socket closed.");
		} catch (IOException e) {
			System.err.println("❌ Failed to close server: " + e.getMessage());
		}
	}
}
