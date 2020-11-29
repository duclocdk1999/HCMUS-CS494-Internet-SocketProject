package client;

import com.sun.tools.javac.Main;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Race extends AnchorPane implements Initializable {
    public PlayerConnector connector;

    @FXML
    private Text messageText, messageCounter, questionResult, scoreResult, racingUILength, racingUIRoom;

    @FXML
    private Text playerSmallText1, playerSmallText2, playerSmallText3, playerSmallText4,
            playerSmallScore1, playerSmallScore2, playerSmallScore3, playerSmallScore4;

    @FXML
    AnchorPane anchorPaneContainer;

    @FXML
    private GridPane gridPaneSmallScore, gridPaneTop, gridPaneBottom;

    @FXML
    private VBox notiBoard;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField inputResult;

    @FXML
    private Button submitResult;

    @FXML
    private ImageView imageRace;

    int questionCounter = 0;

    Scene scene;

    // -----------------------------------------------------------------------------------
    String[] sceneNames = {
            "menu.fxml", "rules.fxml", "register.fxml", "waitroom.fxml", "racing.fxml", "message-scene.fxml"
    };

    // -----------------------------------------------------------------------------------
    private ImageView createImageViewNode(String urlPath, int sizeValue) {
        URL fxmlURL = getClass().getResource(urlPath);
        Image img = new Image(fxmlURL.toExternalForm(), sizeValue, sizeValue, true, true);
        ImageView imageViewNode = new ImageView();
        imageViewNode.setImage(img);
        return imageViewNode;
    }

    private GridPane initializeGridPane(double anchorLeft, double anchorTop) {
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("racing-grid");
        gridPane.setPrefHeight(145.0);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPrefWidth(690.0);
        AnchorPane.setLeftAnchor(gridPane, anchorLeft);
        AnchorPane.setTopAnchor(gridPane, anchorTop);
        return gridPane;
    }

    private void setRoadLength(String maxLength) {
        System.out.println("debugger02");
//        <Line endY="145.0" stroke="WHITE" strokeWidth="2.0" styleClass="racing-dashed-line"
//        GridPane.columnIndex="0" GridPane.halignment="LEFT" GridPane.rowSpan="2147483647" />
//        <ColumnConstraints hgrow="NEVER" minWidth="10.0" prefWidth="138.0" />

//         fx:id="gridPaneTop" minWidth="-Infinity" prefHeight="145.0" prefWidth="690.0"
//         styleClass="racing-grid" AnchorPane.leftAnchor="55.0" AnchorPane.topAnchor="0.0"

//        fx:id="gridPaneBottom" alignment="CENTER" layoutX="65.0" layoutY="10.0"
//        maxWidth="-Infinity" minWidth="-Infinity" prefHeight="145.0" prefWidth="690.0"
//        styleClass="racing-grid" AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="55.0"

        GridPane gridTop = initializeGridPane(55.0, 0.0);
        GridPane gridBottom = initializeGridPane(55.0, 0.0);

        for (int i = 0; i < Integer.parseInt(maxLength); i++) {
            System.out.println("debugger03");
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.NEVER);
            column.setPrefWidth((double)690.0/Double.parseDouble(maxLength));
            gridTop.getColumnConstraints().add(column);
            gridBottom.getColumnConstraints().add(column);
        }

        for (int j = 0; j < 2; j++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.NEVER);
            row.setPrefHeight(73.5);;
            gridTop.getRowConstraints().add(row);
            gridBottom.getRowConstraints().add(row);
        }

        for (int y = 0; y < Integer.parseInt(maxLength); y++) {
            Line divider = new Line();
            divider.getStyleClass().add("racing-dashed-line");
            divider.setEndY(145.0);
            divider.setStroke(Paint.valueOf("WHITE"));
            divider.setStrokeWidth(2.0);
            gridTop.setRowSpan(divider, GridPane.REMAINING);
            gridTop.setHalignment(divider, HPos.LEFT);

            gridTop.getChildren().add(divider);
            gridBottom.getChildren().add(divider);
        }
        System.out.println("debugger04");
        anchorPaneContainer.getChildren().add(gridTop);
        anchorPaneContainer.getChildren().add(gridBottom);
    }

    // -----------------------------------------------------------------------------------
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

    public HashMap connectToServer() {
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
        HashMap<String, String> connected = new HashMap<String, String>();

        ArrayList<String> updatedScoreList = new ArrayList<String>();
        String userName;
        String host;
        int port;

        String maxNumberQuestion;
        String numPlayers;
        String score;
        String question;
        String otherScores;


        public PlayerConnector(String host, int port, String userName) {
            this.host = host;
            this.port = port;
            this.userName = userName;
        }

        public HashMap connectToServer() {
            try {
                System.out.println("userName:"+userName);
                socket = new Socket(host, port);
                System.out.println("client is connected to " + host + " port " + port + "...");
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());

                outputStream.writeUTF(userName);
                String info = this.inputStream.readUTF();
                connected.put("info", info);

                thread = new Thread(this);
                thread.start();

                String status = info.split(" ")[0];
                String maxNumQuestions = info.split(" ")[3];
                String numPlayers = info.split(" ")[4];

                if (status.equals("successful")) {
                    System.out.println(userName + " logged in successfully");
                    connected.put("status", "true");

                    this.maxNumberQuestion = maxNumQuestions;
                    this.numPlayers = numPlayers;
                    System.out.println("debugger01");
                    Platform.runLater(() -> {
                        setRoadLength(this.maxNumberQuestion);
                    });

                    racingUIRoom.setText(this.host+":"+this.port);
                    racingUILength.setText(this.maxNumberQuestion);

                    return connected;
                }
                System.out.println("Logged in failed");
                connected.put("status", "false");
                return connected;
            } catch (UnknownHostException e) {
                System.out.println("Logged in failed");
                e.printStackTrace();
                connected.put("status", "false");
                return connected;
            } catch (IOException e) {
                System.out.println("Logged in failed");
                e.printStackTrace();
                connected.put("status", "false");
                return connected;
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
                ++questionCounter;
                System.out.println("finalQuesition: "+questionCounter);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        // ---------------------------------------------------------------------------------
        public boolean updateOtherScores() {
        	/*
        	 * written by: Loc
        	 * contact me through this profile: www.facebook.com/duclockd1999
        	 * */
        	
        	try {
        		otherScores = this.inputStream.readUTF();
        		System.out.println("hello:"+otherScores);
        		updateOtherPlayerUI(otherScores);
        		return true;
        	}
        	catch (IOException e) {
        		e.printStackTrace();
        		return false;
        	}
        }

        private void updateOtherPlayerUI(String otherScores) {
            int co = 0;
            String[] imgURLs = {"banhmi", "chair", "nonla", "toong", "fin"};
            String[] playersScore = otherScores.split(" ");
            ArrayList<HBox> scoreboardHBox = new ArrayList<HBox>();
            for (int i = 0; i < playersScore.length; i++) {
                String userName = playersScore[i].split(":")[0];
                String score = playersScore[i].split(":")[1];
                HBox hbox = new HBox();
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setPrefHeight(100.0);
                hbox.setPrefWidth(200.0);
                hbox.setSpacing(8.0);

                Text textNode = new Text(userName);
                textNode.getStyleClass().add("player-small-text");

                Text scoreNode = new Text(score);
                scoreNode.getStyleClass().add("player-small-score");


                if (co >= 4) co = -1;
                String urlPath = "resources/player/player-"+imgURLs[++co]+".png";
                ImageView imageViewNode = createImageViewNode(urlPath, 25);

                hbox.getChildren().addAll(imageViewNode, textNode, scoreNode);
                scoreboardHBox.add(hbox);
            }

            Platform.runLater(() -> {
                int counter = 0;
                gridPaneSmallScore.getChildren().clear();

                for (int x = 0; x < 2; x++) {
                    if (counter > Integer.parseInt(this.numPlayers)) break;
                    for (int y = 0; y < 2; y++) {
                        gridPaneSmallScore.add(scoreboardHBox.get(counter), x, y);
                        System.out.println("counter"+counter);
                        counter++;
                        if (counter > Integer.parseInt(this.numPlayers)) break;
                    }
                }
            });
        }
        // ---------------------------------------------------------------------------------
        public String getCurrentScore() {
            return score;
        }

        public String getCurrentQuestion() {
            return question;
        }

        public void submitAnswer(String answer) {
            try {
                submitResult.setDisable(true);

                submitResult.getStyleClass().add("button-black-disabled");

                questionResult.getStyleClass().clear();
                questionResult.getStyleClass().add("racing-numbers");
                questionResult.getStyleClass().add("disabled-text");

                this.outputStream.writeUTF(answer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (thread != null) {
                System.out.println("Run..."+ questionCounter + "-" + maxNumberQuestion);
                if (questionCounter == Integer.parseInt(maxNumberQuestion)) {
                    boolean tmp = updateScore() && updateOtherScores();
                    // chúc bạn may mắn lần sau!
                    // nhờ ý chí kiên cuờng và luôn vững chãi /n bạn đã giành chiến thắng!

                    System.out.println("finalscore: " + score + ": " + tmp);
                    return;
                }
                if (updateScore() && updateQuestion() && updateOtherScores()) {
                    Platform.runLater(() -> {
                        submitResult.setDisable(false);

                        submitResult.getStyleClass().clear();
                        submitResult.getStyleClass().add("button-rounded");

                        questionResult.getStyleClass().clear();
                        questionResult.getStyleClass().add("racing-numbers");

                        inputResult.clear();

                        int prevScore = Integer.parseInt(scoreResult.getText());
                        int nextScore = Integer.parseInt(getCurrentScore());
                        String updatedScore = Integer.toString(nextScore-prevScore);
                        System.out.println("Curent:" + updatedScore);
                        if (Integer.parseInt(updatedScore) > 0) {
                            updatedScore = "+" + updatedScore;
                        }
                        if (!updatedScore.equals("0")) {
                            updatedScore += " bước";
                            updatedScoreList.add(updatedScore);

                            HBox notiMessage = new HBox();
                            Text textUsername = new Text(this.userName);
                            Text textScore = new Text(updatedScore);
                            textUsername.getStyleClass().add("noti-text-username");
                            textScore.getStyleClass().add("noti-text");

                            notiMessage.getChildren().addAll(textUsername, textScore);
                            notiBoard.getChildren().add(notiMessage);
                            scrollPane.setVvalue(1D);
                        }
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
