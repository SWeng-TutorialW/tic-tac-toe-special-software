// ============================
// 1. ConnectController.java
// ============================
package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;

public class ConnectController {
    @FXML private TextField ipField;
    @FXML private TextField portField;
    @FXML private Label errorLabel;

    @FXML
    void onConnect() {
        String ip = ipField.getText().trim();
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            errorLabel.setText("Port must be a number");
            return;
        }

        SimpleClient.configure(ip, port);
        if (!SimpleClient.getClient().tryOpen()) {
            errorLabel.setText("Cannot connect");
            return;
        }

        Platform.runLater(() -> {
            try {
                App.setRoot("primary");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
