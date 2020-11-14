package client;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import client.connection.Connector;
import com.sun.tools.javac.Main;
import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//import javax.swing.text.html.ImageView;

public class Controller {
    @FXML
    TextField roomTextField, usernameTextField, inputResult, questionResult, scoreResult;

    @FXML
    Button registerButton;

    @FXML
    Text currentPlayers, messageText, messageCounter, waiting;

    @FXML
    AnchorPane registerScene;
    
    String[] sceneNames = {
    	"menu.fxml", "rules.fxml", "register.fxml", "waitroom.fxml", "racing.fxml", "message-scene.fxml"
    };
    
    Connector connector = null;

    private boolean connected;
    private boolean updateScore;
    private boolean updateQuestion;

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

        CountDownLatch latch = new CountDownLatch(1);
    	String ip = roomTextField.getText();
    	String name = usernameTextField.getText();
    	connected = false;

        registerScene.setOpacity(0.6);
        waiting.setVisible(true);

    	new Thread(() -> {
                MainClient.raceScene.initPlayer(ip, 8080, name);
                connected = MainClient.raceScene.connectToServer();
                latch.countDown();
        }).start();

    	new Thread(() -> {
    	    try {
    	        latch.await();
            } catch(InterruptedException e) {
    	        e.printStackTrace();
            }

    	    Platform.runLater(() -> {
                if (connected) {
                        registerScene.setOpacity(1);
                        waiting.setVisible(false);
                        MainClient.raceScene.initRacingScene();
                        MainClient.stage.setScene(MainClient.raceScene.getScene());
                } else {
                    System.out.println("not connected");
                }
            });
        }).start();
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
//        event.consume();
//        goToSceneIndicator(4, event);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		VBox root = new VBox();
		Canvas c = new Canvas(100, 100);
		GraphicsContext gc = c.getGraphicsContext2D();
		root.getChildren().add(c);
		
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, 100, 100);			// (0, 0) is top-left position, 30 is width, 40 is height
		
		Scene scene = new Scene(root, 600, 500);
		currentStage.setScene(scene);
		currentStage.show();
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
