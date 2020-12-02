package client;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Controller {
    @FXML
    TextField roomTextField, usernameTextField;

    @FXML
    Button registerButton;

    @FXML
    Text roomErrorText, usernameErrorText;

    @FXML
    AnchorPane registerScene;
    
    String[] sceneNames = {
    	"menu.fxml", "rules.fxml", "register.fxml", "waitroom.fxml", "racing.fxml", "message-scene.fxml"
    };

    private HashMap<String, String> connected = new HashMap<>();

    // -----------------------------------------------------------------------------------
    private void goToSceneIndicator(int nextScene, ActionEvent event) throws IOException {
        String sceneName = sceneNames[nextScene];
        Parent root = FXMLLoader.load(getClass().getResource(sceneName));
        Scene scene = new Scene(root, 800, 575);
        
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(scene);
    }
    // -----------------------------------------------------------------------------------
    /* isFieldNotEmpty: Check if input is empty */
    private boolean isFieldNotEmpty(String value, Text errorHolder, String errorMessage) {
        if (value.trim().equals("") || value.length() == 0) {
            // field is empty, show error
            errorHolder.setVisible(true);
            errorHolder.setText(errorMessage);
            return false;
        }
        errorHolder.setVisible(false);
        errorHolder.setText("");
        return true;
    }

    /* isRegexValid: Check if input matches the regex pattern */
    private boolean isRegexValid(String value, Text errorHolder, String errorMessage, String regexString) {
        //(?!_)^[A-Za-z0-9_]+
        if (!value.matches(regexString)) {
            errorHolder.setVisible(true);
            errorHolder.setText(errorMessage);
            return false;
        }
        errorHolder.setVisible(false);
        errorHolder.setText("");
        return true;
    }

    /* isLengthValid: Check if input is in between minLength and maxLength */
    private boolean isLengthValid(String value, Text errorHolder, String errorMessage, int minLength, int maxLength) {
        if (value.length() < minLength || value.length() > maxLength) {
            errorHolder.setVisible(true);
            errorHolder.setText(errorMessage);
            return false;
        }
        errorHolder.setVisible(false);
        errorHolder.setText("");
        return true;
    }

    /*
     * Validation for room input and username input
     */
    private boolean checkInputField(String ip, String name) {
        boolean isValid = true;
        isValid &= isFieldNotEmpty(ip, roomErrorText, "(*) Room IP can not be empty")
                && isRegexValid(ip, roomErrorText, "(*) Room IP not available", "localhost");
        isValid &= isFieldNotEmpty(name, usernameErrorText, "(*) Username can not be empty")
                && isRegexValid(name, usernameErrorText, "(*) Name contains A-Z, a-z, 0-9, and _", "[A-Za-z0-9_]+")
                && isLengthValid(name, usernameErrorText, "(*) Length must be between 3 and 10", 3, 10);
        return isValid;
    }

    @FXML
    private void onRegisterBtnClick(ActionEvent event) throws IOException, InterruptedException {
        String ip = roomTextField.getText();
    	String name = usernameTextField.getText();
        boolean isValid = checkInputField(ip, name);

        /* if input is valid, start connecting */
    	if (isValid) {
            CountDownLatch latch = new CountDownLatch(1);
            connected.put("status", "false");

            MainClient.waitScene.initWaitingScene();
            MainClient.stage.setScene(MainClient.waitScene.getScene());

            new Thread(() -> {
                MainClient.raceScene.initPlayer(ip, 8080, name);
                connected = MainClient.raceScene.connectToServer();
                connected.forEach((key, tab) -> System.out.println("key"+key));
                latch.countDown();
            }).start();

            new Thread(() -> {
                try {
                    latch.await();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> connected.forEach((key, tab) -> {
                    if (key.equals("status")) {
                        if (tab.equals("true")) {
                            MainClient.raceScene.initRacingScene();
                            MainClient.stage.setScene(MainClient.raceScene.getScene());
                        } else {
                            System.out.println("not connected");
                        }
                    }
                }));
            }).start();
        }
    }

    // -----------------------------------------------------------------------------------
    @FXML
    private void goToMenuScene(ActionEvent event) throws IOException {
        event.consume();
        goToSceneIndicator(0, event);
    }

    @FXML
    private void goToRulesScene(ActionEvent event) throws IOException {
        event.consume();
        goToSceneIndicator(1, event);
    }

    @FXML
    private void goToRegisterScene(ActionEvent event) throws IOException {
        event.consume();
        goToSceneIndicator(2, event);
    }

    @FXML
    private void quitGame() {
        Platform.exit();
        System.exit(0);
    }
}
