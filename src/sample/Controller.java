package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class Controller {
    @FXML
    TextField roomTextField, usernameTextField;

    @FXML
    Button registerButton;

    @FXML
    Text currentPlayers, messageText, messageCounter;

    private void goToSceneIndicator(int nextScene) {
        System.out.println(nextScene);
    }

    private void setTextResult(String text) {
        System.out.println(text);
    }

    @FXML
    private void goToMenuScene(ActionEvent event) {
        event.consume();
        goToSceneIndicator(0);
    }

    @FXML
    private void goToRulesScene(ActionEvent event) {
        event.consume();
        goToSceneIndicator(1);
    }

    @FXML
    private void goToRegisterScene(ActionEvent event) {
        event.consume();
        goToSceneIndicator(2);
    }

    @FXML
    private void goToWaitingScene(ActionEvent event) {
        event.consume();
        goToSceneIndicator(3);
    }

    @FXML
    private void goToGameScene(ActionEvent event) {
        event.consume();
        goToSceneIndicator(4);
    }

    @FXML
    private void goToResultScene(ActionEvent event) {
        event.consume();
        goToSceneIndicator(5);
        setTextResult("Chúc bạn may mắn lần sau");
    }

    @FXML
    private void quitGame() {
        Platform.exit();
        System.exit(0);
    }
}
