package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class PrimaryController {

	public static PrimaryController instance;

	@FXML private Button btn00, btn01, btn02, btn10, btn11, btn12, btn20, btn21, btn22;
	@FXML private Label resultLabel;

	public static String playerSymbol = "X";
	public static boolean isMyTurn = false;

	private int moveCount = 0;
	private HashMap<String, Button> buttonMap;

	@FXML
	void initialize() {
		instance = this;

		try {
			SimpleClient.getClient().sendToServer("add client");
		} catch (Exception e) {
			e.printStackTrace();
		}

		buttonMap = new HashMap<>();
		buttonMap.put("btn00", btn00); buttonMap.put("btn01", btn01); buttonMap.put("btn02", btn02);
		buttonMap.put("btn10", btn10); buttonMap.put("btn11", btn11); buttonMap.put("btn12", btn12);
		buttonMap.put("btn20", btn20); buttonMap.put("btn21", btn21); buttonMap.put("btn22", btn22);
	}

	@FXML
	void handleButtonClick(ActionEvent event) {
		if (!isMyTurn) return;

		Button clicked = (Button) event.getSource();
		if (clicked.getText().isEmpty()) {
			String id = clicked.getId();
			int row = Character.getNumericValue(id.charAt(3));
			int col = Character.getNumericValue(id.charAt(4));
			try {
				SimpleClient.getClient().sendToServer("MOVE," + row + "," + col + "," + playerSymbol);
				isMyTurn = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void applyMove(int row, int col, String symbol) {
		Button btn = buttonMap.get("btn" + row + col);
		if (btn != null && btn.getText().isEmpty()) {
			btn.setText(symbol);
			btn.setDisable(true);
			moveCount++;

			if (checkWin(symbol)) {
				disableAllButtons();
				resultLabel.setText("🎉 Player " + symbol + " wins!");
			} else if (moveCount == 9) {
				resultLabel.setText("🤝 It's a draw!");
			}
		}
	}

	private boolean checkWin(String symbol) {
		String[][] board = {
				{btn00.getText(), btn01.getText(), btn02.getText()},
				{btn10.getText(), btn11.getText(), btn12.getText()},
				{btn20.getText(), btn21.getText(), btn22.getText()}
		};

		for (int i = 0; i < 3; i++) {
			if (symbol.equals(board[i][0]) && symbol.equals(board[i][1]) && symbol.equals(board[i][2])) return true;
			if (symbol.equals(board[0][i]) && symbol.equals(board[1][i]) && symbol.equals(board[2][i])) return true;
		}

		return (symbol.equals(board[0][0]) && symbol.equals(board[1][1]) && symbol.equals(board[2][2])) ||
				(symbol.equals(board[0][2]) && symbol.equals(board[1][1]) && symbol.equals(board[2][0]));
	}

	private void disableAllButtons() {
		for (Button b : buttonMap.values()) b.setDisable(true);
	}

	public void receiveSymbol(String symbol) {
		playerSymbol = symbol;
		isMyTurn = symbol.equals("X");
		System.out.println("You are player " + symbol);

		Platform.runLater(() -> {
			try {
				Stage stage = (Stage) btn00.getScene().getWindow();
				stage.setTitle("Tic-Tac-Toe - " + symbol + " player");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}