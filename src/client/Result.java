package client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class Result extends AnchorPane {
    Scene scene;
    @FXML
    Text messageCounter, messageText;

    private static final Integer STARTTIME = 10;
    private Timeline time;
    private Integer timeSeconds = STARTTIME;

    private void goToSceneIndicator(String nextScene, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(nextScene+".fxml"));
        Stage stage = (Stage) MainClient.stage.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }

    public void doTime(boolean isWinner) {
        time = new Timeline();
        if (time!=null) {
            time.stop();
        }

        time.setCycleCount(Timeline.INDEFINITE);
        System.out.println("hú xong r");
        KeyFrame frame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                messageCounter.setText(Integer.toString(--timeSeconds));
                if (timeSeconds <= 0) {
                    time.stop();
                    try {
                        goToSceneIndicator("menu", actionEvent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        time.getKeyFrames().add(frame);
        time.playFromStart();
    }


    public Result() {
        /* Initialize Message Scene */
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

    public void setMessage(boolean isWinner) {
        System.out.println("isWinner: "+Boolean.toString(isWinner));
        if (isWinner)
            messageText.setText("nhờ ý chí kiên cuờng và luôn vững chãi /n bạn đã giành chiến thắng!");
        else
            messageText.setText("chúc bạn may mắn lần sau");
    }
}
