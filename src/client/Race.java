package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
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
    Text questionResult, scoreResult, racingUILength, racingUIRoom;

    @FXML
    AnchorPane anchorPaneContainer;

    @FXML
    GridPane gridPaneSmallScore;

    @FXML
    VBox notiBoard;

    @FXML
    ScrollPane scrollPane;

    @FXML
    TextField inputResult;

    @FXML
    Button submitResult;

    int questionCounter = 0;

    Scene scene;

    GridPane gridTop, gridBottom;

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


    // -----------------------------------------------------------------------------------
    private ImageView createImageViewNode(String urlPath, int size) {
        URL fxmlURL = getClass().getResource(urlPath);
        Image img = new Image(fxmlURL.toExternalForm(), size, size, true, true);
        ImageView imageViewNode = new ImageView();
        imageViewNode.setImage(img);
        return imageViewNode;
    }

    private GridPane initializeGridPane(int anchorX) {
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("racing-grid");
        gridPane.setPrefHeight(145.0);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPrefWidth(690.0);
        AnchorPane.setLeftAnchor(gridPane, 55.0);
        if (anchorX > 0) {
            AnchorPane.setTopAnchor(gridPane, 0.0);
        } else {
            AnchorPane.setBottomAnchor(gridPane, 0.0);
        }
        return gridPane;
    }

    private Line createLine(GridPane gridPane, int colIndex) {
        Line divider = new Line();
        divider.getStyleClass().add("racing-dashed-line");
        divider.setEndY(145.0);
        divider.setStroke(Paint.valueOf("WHITE"));
        divider.setStrokeWidth(2.0);
        GridPane.setColumnIndex(divider, colIndex);
        gridPane.setRowSpan(divider, GridPane.REMAINING);
        gridPane.setHalignment(divider, HPos.LEFT);
        return divider;
    }

    private void setRoadLength(String maxLength) {
        gridTop = initializeGridPane(1);
        gridBottom = initializeGridPane(-1);

        for (int i = 0; i < Integer.parseInt(maxLength); i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.NEVER);
            column.setPrefWidth(690.0/Double.parseDouble(maxLength));

            gridTop.getColumnConstraints().add(column);
            gridBottom.getColumnConstraints().add(column);
        }

        for (int j = 0; j < 2; j++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.NEVER);
            row.setPrefHeight(73.5);

            gridTop.getRowConstraints().add(row);
            gridBottom.getRowConstraints().add(row);
        }

        for (int y = 0; y <= Integer.parseInt(maxLength); y++) {
            Line dividerTop = createLine(gridTop, y);
            Line dividerBottom = createLine(gridBottom, y);

            gridTop.getChildren().add(dividerTop);
            gridBottom.getChildren().add(dividerBottom);
        }

        gridTop.setAlignment(Pos.CENTER);
        gridBottom.setAlignment(Pos.CENTER);
        anchorPaneContainer.getChildren().add(gridTop);
        anchorPaneContainer.getChildren().add(gridBottom);
    }
    // -----------------------------------------------------------------------------------
    public class PlayerConnector implements Runnable {
        Socket socket;
        Thread thread;
        DataInputStream inputStream;
        DataOutputStream outputStream;
        HashMap<String, String> connected = new HashMap<>();

        ArrayList<String> updatedScoreList = new ArrayList<>();
        String userName;
        String host;
        int port;

        String imgName;
        String maxNumberQuestion;
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

                if (status.equals("successful")) {
                    System.out.println(userName + " logged in successfully");
                    connected.put("status", "true");

                    this.maxNumberQuestion = maxNumQuestions;
                    System.out.println("debugger01");
                    Platform.runLater(() -> setRoadLength(this.maxNumberQuestion));

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
            String[] imgURLs = {"banhmi", "chair", "nonla", "toong", "fin"};
            String[] playersScore = otherScores.split(" ");

            ArrayList<HBox> scoreboardHBox = new ArrayList<>();

            Platform.runLater(()-> {
                int co = 0;
                String urlPathRoad;
                ImageView playerImageRoad;

                gridTop.getColumnConstraints().clear();
                gridTop.getRowConstraints().clear();
                gridBottom.getColumnConstraints().clear();
                gridBottom.getRowConstraints().clear();

                gridTop.getChildren().clear();
                gridBottom.getChildren().clear();

                setRoadLength(this.maxNumberQuestion);

                for (int i = 0; i < playersScore.length; i++) {
                    String userName = playersScore[i].split(":")[0];
                    String score = playersScore[i].split(":")[1];
                    int tmpScore = Integer.parseInt(score);
                    urlPathRoad = "resources/player/player-" + imgURLs[i] + ".png";
                    playerImageRoad = createImageViewNode(urlPathRoad, 20);

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
                    String urlPath = "resources/player/player-" + imgURLs[++co] + ".png";
                    ImageView imageViewNode = createImageViewNode(urlPath, 25);


                    if (tmpScore > 0)
                        GridPane.setColumnIndex(playerImageRoad, tmpScore - 1);
                    else
                        GridPane.setColumnIndex(playerImageRoad, 0);

                    switch (i) {
                        case 0:
                            GridPane.setRowIndex(playerImageRoad, 0);
                            gridTop.setRowSpan(playerImageRoad, GridPane.REMAINING);
                            gridTop.setHalignment(playerImageRoad, HPos.CENTER);
                            gridTop.getChildren().add(playerImageRoad);
                            break;
                        case 1:
                            GridPane.setRowIndex(playerImageRoad, 1);
                            gridTop.setRowSpan(playerImageRoad, GridPane.REMAINING);
                            gridTop.setHalignment(playerImageRoad, HPos.CENTER);
                            gridTop.getChildren().add(playerImageRoad);
                            break;
                        case 2:
                            GridPane.setRowIndex(playerImageRoad, 0);
                            gridBottom.setRowSpan(playerImageRoad, GridPane.REMAINING);
                            gridBottom.setHalignment(playerImageRoad, HPos.CENTER);
                            gridBottom.getChildren().add(playerImageRoad);
                            break;
                        case 3:
                            GridPane.setRowIndex(playerImageRoad, 1);
                            gridBottom.setRowSpan(playerImageRoad, GridPane.REMAINING);
                            gridBottom.setHalignment(playerImageRoad, HPos.CENTER);
                            gridBottom.getChildren().add(playerImageRoad);
                            break;
                    }

                    if (userName.equals(this.userName)) {
                        this.imgName = imgURLs[co];
                    }
                    hbox.getChildren().addAll(imageViewNode, textNode, scoreNode);
                    scoreboardHBox.add(hbox);
                }
            });


            Platform.runLater(()->{
                gridPaneSmallScore.getChildren().clear();
                int counter = 0;
                for (int x = 0; x < 2; x++) {
                    if (counter > playersScore.length) break;
                    for (int y = 0; y < 2; y++) {
                        gridPaneSmallScore.add(scoreboardHBox.get(counter), x, y);
                        counter++;
                        if (counter > playersScore.length) break;
                    }
                }
            });


            // update Road
//            Line divider = new Line();
//            divider.getStyleClass().add("racing-dashed-line");
//            divider.setEndY(145.0);
//            divider.setStroke(Paint.valueOf("WHITE"));
//            divider.setStrokeWidth(2.0);
//
//            //<ImageView fitHeight="58.0" fitWidth="40.0" pickOnBounds="true"
//// preserveRatio="true" styleClass="player-small-icon"
//// GridPane.columnIndex="2" GridPane.halignment="CENTER">
//            String urlPath = "resources/player/player-" + imgURLs[++co] + ".png";
//            ImageView playerImage = createImageViewNode("")
//            GridPane.setColumnIndex(divider, colIndex);
//            gridPane.setRowSpan(divider, GridPane.REMAINING);
//            gridTop.setHalignment(divider, HPos.LEFT);
//            gridTop.getChildren().add(dividerTop);
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
//                    boolean tmp = updateScore() && updateOtherScores();
                    // chúc bạn may mắn lần sau!
                    // nhờ ý chí kiên cuờng và luôn vững chãi /n bạn đã giành chiến thắng!
                    // chuyển trang
                    System.out.println("hú final");
                    System.out.println("finalscore: " + score);
                    return;
                }
                if (updateScore() && updateQuestion() && updateOtherScores()) {
                    Platform.runLater(()->{
                        submitResult.setDisable(false);

                        submitResult.getStyleClass().clear();
                        submitResult.getStyleClass().add("button-rounded");

                        questionResult.getStyleClass().clear();
                        questionResult.getStyleClass().add("racing-numbers");

                        inputResult.clear();
                    });

                    int prevScore = Integer.parseInt(scoreResult.getText());
                    int nextScore = Integer.parseInt(getCurrentScore());

                    Platform.runLater(() -> {
                        // nextScore: 1, đi tới index 0
                        // nextScore: 5, đi tới index 4
                        // ban đầu tất cả đều ở ngoài Bãi đậu

//               <Image url="@resources/player/player-fin.png" />
//            </ImageView>
                        String updatedScore = Integer.toString(nextScore-prevScore);
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
