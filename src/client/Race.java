package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class Race extends AnchorPane implements Initializable {
    public PlayerConnector connector;

    @FXML
    private Text questionResult, scoreResult;

    @FXML
    private TextField inputResult;

    @FXML
    private Button submitResult;

    @FXML
    private ImageView imageRace;



    Scene scene;

    public Race() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("racing.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        scene = new Scene(this, 800, 575);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initPlayer(String host, int port, String username) {
        connector = new PlayerConnector(host, port, username);
    }

    public void initRacingScene() {
        submitResult.setOnAction(ac -> {
            String result = inputResult.getText();
            connector.submitAnswer(result);
        });
    }

    public boolean connectToServer() {
        return connector.connectToServer();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Racing Scene Initialized");
    }

    public class PlayerConnector implements Runnable {
        Socket socket;
        Thread thread;
        DataInputStream inputStream;
        DataOutputStream outputStream;

        String userName;
        String host;
        int port;

        String score;
        String question;


        public PlayerConnector(String host, int port, String userName) {
            this.host = host;
            this.port = port;
            this.userName = userName;
        }

        public boolean connectToServer() {
            try {
                System.out.println(userName);
                socket = new Socket(host, port);
                System.out.println("client is connected to " + host + " port " + port + "...");
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());

                outputStream.writeUTF(userName);
                String info = this.inputStream.readUTF();

                thread = new Thread(this);
                thread.start();

                String status = info.split(" ")[0];

                if (status.equals("successful")) {
                    System.out.println(userName + " logged in successfully");
                    return true;
                }

                System.out.println("Logged in failed");
                return false;
            } catch (UnknownHostException e) {
                System.out.println("Logged in failed");
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                System.out.println("Logged in failed");
                e.printStackTrace();
                return false;
            }
        }

        public boolean updateScore() {
            try {
                score = this.inputStream.readUTF();
                System.out.println(score);
                return true;
            } catch (NumberFormatException | IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean updateQuestion() {
            try {
                question = this.inputStream.readUTF();
                System.out.println(question);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        public String getCurrentScore() {
            return score;
        }

        public String getCurrentQuestion() {
            return question;
        }

        public void submitAnswer(String answer) {
            try {
                imageRace.setOpacity(0.5);
                this.outputStream.writeUTF(answer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (thread != null) {
                System.out.println("Run...");
                if (updateScore() && updateQuestion()) {
                    Platform.runLater(() -> {
                        imageRace.setOpacity(1);
                        inputResult.clear();
                        scoreResult.setText(getCurrentScore());
                        questionResult.setText(getCurrentQuestion());
                    });
                }
                else {
                	break;
                }
            }
        }
    }
}
