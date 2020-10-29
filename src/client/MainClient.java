package client;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// get the functionalities for JavaFX through Application
public class MainClient extends Application {

    Scene scene1, scene2, scene3, scene4, scene5, scene6;
    // The entire window is called stage, content inside the stage is the scene, in scene we will put the GUI => Stage and Scene methodlogy
    // method whenever our app is first launched
    // launch(args): method inside Application class, so whenever we starts our program, we start this method and this method will go into Application and prepare all the things needed
    public static void main(String[] args) {
        launch(args);
    }

    // after setting up through launch, we go into start
    @Override
    public void start(Stage primaryStage) throws Exception{
        // primaryStage: main window
        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        Parent rootRules = FXMLLoader.load(getClass().getResource("rules.fxml"));
        Parent rootRegister = FXMLLoader.load(getClass().getResource("register.fxml"));
        Parent rootWaitroom = FXMLLoader.load(getClass().getResource("waitroom.fxml"));
        Parent rootRacing = FXMLLoader.load(getClass().getResource("racing.fxml"));
        Parent rootMessage = FXMLLoader.load(getClass().getResource("message-scene.fxml"));
        primaryStage.setTitle("RACING ARENA - DAU TRUONG XE CO");
        primaryStage.setResizable(false);

        // Layout 1
        root.getStylesheets().add(getClass().getResource("css/root.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource("css/menu.css").toExternalForm());
        scene1 = new Scene(root, 800, 575);

        // Layout 2
        rootRules.getStylesheets().add(getClass().getResource("css/root.css").toExternalForm());
        rootRules.getStylesheets().add(getClass().getResource("css/rules.css").toExternalForm());
        scene2 = new Scene(rootRules, 800, 575);

        // Layout 3
        rootRegister.getStylesheets().add(getClass().getResource("css/root.css").toExternalForm());
        rootRegister.getStylesheets().add(getClass().getResource("css/register.css").toExternalForm());
        scene3 = new Scene(rootRegister, 800, 575);

        // Layout 4
        rootWaitroom.getStylesheets().add(getClass().getResource("css/root.css").toExternalForm());
        rootWaitroom.getStylesheets().add(getClass().getResource("css/waitroom.css").toExternalForm());
        scene4 = new Scene(rootWaitroom, 800, 575);

        // Layout 5
        rootRacing.getStylesheets().add(getClass().getResource("css/root.css").toExternalForm());
        rootRacing.getStylesheets().add(getClass().getResource("css/racing.css").toExternalForm());
        scene5 = new Scene(rootRacing, 800, 575);

        // Layout 6
        rootMessage.getStylesheets().add(getClass().getResource("css/root.css").toExternalForm());
        rootMessage.getStylesheets().add(getClass().getResource("css/message-scene.css").toExternalForm());
        // chúc bạn may mắn lần sau!
        // nhờ ý chí kiên cuờng và luôn vững chãi /n bạn đã giành chiến thắng!
        scene6 = new Scene(rootMessage, 800, 575);

        primaryStage.setScene(scene1);
        primaryStage.show();
    }
}
