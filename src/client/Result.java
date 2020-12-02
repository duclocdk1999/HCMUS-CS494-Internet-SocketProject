package client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Result extends AnchorPane {
    Scene scene;
    @FXML
    Text messageCounter, messageText, messageScore;

    private static final Integer STARTTIME = 10;
    private Timeline time;
    private Integer timeSeconds = STARTTIME;

    private void goToSceneIndicator(String nextScene, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(nextScene+".fxml"));
        Stage stage = (Stage) MainClient.stage.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }

    public void doTime() {
        time = new Timeline();
        if (time!=null) {
            time.stop();
        }

        time.setCycleCount(Timeline.INDEFINITE);
        KeyFrame frame = new KeyFrame(Duration.seconds(1), actionEvent -> {
            messageCounter.setText(Integer.toString(--timeSeconds));
            if (timeSeconds <= 0) {
                time.stop();
                try {
                    goToSceneIndicator("menu", actionEvent);
                } catch (IOException e) {
                    e.printStackTrace();
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
        if (isWinner)
            messageText.setText("nhờ ý chí kiên cuờng và luôn vững chãi,\nbạn đã giành chiến thắng!");
        else
            messageText.setText("chúc bạn may mắn lần sau");
    }

    public void setScore(String score) {
        messageScore.setText(score);
    }
}
