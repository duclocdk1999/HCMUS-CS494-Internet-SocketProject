package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;

public class Result extends AnchorPane {
    Scene scene;
    @FXML
    Text currentWaitPlayers, maxPlayers;

    @FXML
    HBox waitPlayerBox;

    public Result() {
        /* Initialize Waitroom */
        FXMLLoader loader = new FXMLLoader(getClass().getResource("message-scene.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        scene = new Scene(this, 800, 575);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
