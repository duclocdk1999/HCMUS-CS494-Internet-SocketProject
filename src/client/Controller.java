package client;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import client.connection.Connector;
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
    TextField roomTextField, usernameTextField, inputResult, questionResult, scoreResult;

    @FXML
    Button registerButton;

    @FXML
    Text messageText, messageCounter, roomErrorText, usernameErrorText, waiting;

    @FXML
    AnchorPane registerScene, waitingScene;
    
    String[] sceneNames = {
    	"menu.fxml", "rules.fxml", "register.fxml", "waitroom.fxml", "racing.fxml", "message-scene.fxml"
    };
    
    Connector connector = null;

    private boolean updateScore;
    private boolean updateQuestion;
    private HashMap<String, String> connected = new HashMap<String, String>();

    // -----------------------------------------------------------------------------------
    private void goToSceneIndicator(int nextScene, ActionEvent event) throws IOException {
        System.out.println(nextScene);
        
        String sceneName = sceneNames[nextScene];
        Parent root = FXMLLoader.load(getClass().getResource(sceneName));
        Scene scene = new Scene(root, 800, 575);
        
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(scene);
    }
    // -----------------------------------------------------------------------------------
    private void setTextResult(String text) {
        System.out.println(text);
    }
    // -----------------------------------------------------------------------------------

    private boolean isFieldNotEmpty(String value, Text errorHolder, String errorMessage) {
        if (value.trim().equals("") || value == null || value.length() == 0) {
            // field is empty, show error
            errorHolder.setVisible(true);
            errorHolder.setText(errorMessage);
            return false;
        }
        errorHolder.setVisible(false);
        errorHolder.setText("");
        return true;
    }

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

    	if (isValid) {
            CountDownLatch latch = new CountDownLatch(1);
            connected.put("status", "false");

            MainClient.waitScene.initWaitingScene();
            MainClient.stage.setScene(MainClient.waitScene.getScene());

            new Thread(() -> {
                MainClient.raceScene.initPlayer(ip, 8080, name);
                connected = MainClient.raceScene.connectToServer();
                connected.forEach((key, tab) -> {
                    System.out.println("key"+key);
                });
                latch.countDown();
            }).start();

            new Thread(() -> {
                try {
                    latch.await();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> {
                    connected.forEach((key, tab) -> {
                        if (key == "status") {
                            if (tab == "true") {
                                MainClient.raceScene.initRacingScene();
                                MainClient.stage.setScene(MainClient.raceScene.getScene());
                            } else {
                                System.out.println("not connected");
                            }
                        }
                    });
                });
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
    private void goToWaitingScene(ActionEvent event) throws IOException {
        event.consume();
        goToSceneIndicator(3, event);
    }

    @FXML
    private void goToGameScene(ActionEvent event) throws IOException {
        event.consume();
        goToSceneIndicator(4, event);
    }

    @FXML
    private void goToResultScene(ActionEvent event) throws IOException {
        event.consume();
        goToSceneIndicator(5, event);
        setTextResult("Chúc bạn may mắn lần sau");
    }

    @FXML
    private void quitGame() {
        Platform.exit();
        System.exit(0);
    }
}
