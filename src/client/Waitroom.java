package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.util.ResourceBundle;

public class Waitroom extends AnchorPane implements Initializable {
    Scene scene;
    @FXML
    Text currentWaitPlayers, maxPlayers;

    @FXML
    HBox waitPlayerBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public Waitroom() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("waitroom.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        scene = new Scene(this, 800, 575);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initWaitingScene() {
        int numPlayers = 5, maxNumPlayers = 5, co = -1;
        currentWaitPlayers.setText(Integer.toString(numPlayers));
        maxPlayers.setText("/"+maxNumPlayers);
        String[] imgURLs = {"banhmi", "chair", "fin", "toong", "nonla"};
//        Text status  = new Text("Loading");

        for (int i = 0; i < numPlayers; i++) {
            VBox childNode = new VBox();
            childNode.setId("waitPlayer" + (i+1));
            childNode.setAlignment(Pos.CENTER);

            Text textNode = new Text("player0"+(i+1));
            textNode.getStyleClass().addAll("waitroom-player", "beige-text");

            ImageView imageViewNode = new ImageView();
            if (co >= 4) co = -1;
            String urlPath = "resources/player/player-"+imgURLs[++co]+".png";
            URL fxmlURL = getClass().getResource(urlPath);
            Image img = new Image(fxmlURL.toExternalForm(), 50, 50, true, true);
            imageViewNode.setImage(img);

            childNode.getChildren().addAll(textNode, imageViewNode);
            waitPlayerBox.getChildren().add(childNode);
        }
    }
}
