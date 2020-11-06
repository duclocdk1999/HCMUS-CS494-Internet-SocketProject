package client;

import java.io.IOException;

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
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Controller {
    @FXML
    TextField roomTextField, usernameTextField;

    @FXML
    Button registerButton;

    @FXML
    Text currentPlayers, messageText, messageCounter;
    
    String[] sceneNames = {
    	"menu.fxml", "rules.fxml", "register.fxml", "waitroom.fxml", "racing.fxml", "message-scene.fxml"
    };
    
    Connector connector = null;
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
    @FXML
    private void onRegisterBtnClick(ActionEvent event) throws IOException, InterruptedException {
        
    	String ip = roomTextField.getText();
    	String name = usernameTextField.getText();
    	
    	if (this.connector == null) {
    		this.connector = new Connector(ip, 8080);
    	}    	
    	if (connector.register(name) == false) {
    		return;
    	}
    	else {
        	goToGameScene(event);    		
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
