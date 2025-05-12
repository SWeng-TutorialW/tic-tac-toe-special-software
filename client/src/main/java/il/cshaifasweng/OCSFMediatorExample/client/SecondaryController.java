package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.application.Platform;


public class SecondaryController {

    @FXML
    private Label messageLabel;

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    @FXML
    void onRestart(ActionEvent event) {
        try {
            // Close the popup
            Stage popupStage = (Stage) messageLabel.getScene().getWindow();
            popupStage.close();

            // Fully restart the application
            Platform.exit();  // closes JavaFX
            new Thread(() -> {
                try {
                    // Wait a little so exit finishes before relaunching
                    Thread.sleep(500);
                    String javaBin = System.getProperty("java.home") + "/bin/java";
                    String jarPath = new java.io.File(
                            App.class.getProtectionDomain().getCodeSource().getLocation().toURI()
                    ).getPath();

                    // Relaunch the same JAR
                    new ProcessBuilder(javaBin, "-jar", jarPath).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void onClose(ActionEvent event) {
        javafx.application.Platform.exit();
    }
}
