package client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.Stage;
import javafx.util.Duration;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class Race extends AnchorPane implements Initializable {
    public PlayerConnector connector;

    @FXML
    Text questionResult, scoreResult, racingUILength, racingUIRoom,
            racingCounter, questionCounterText, notiMessage;

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
    Button submitResult, goHomeBtn;

    Scene scene;

    GridPane gridTop, gridBottom;

    private static final Integer STARTTIME = 70;
    private Timeline time;
    private Integer timeSeconds = STARTTIME;

    public void doTime() {
//        Integer STARTTIME = 60;
//        Timeline timeline;
//        Integer timeSeconds = STARTTIME;
        time = new Timeline();
        if (time != null) {
            time.stop();
        }
        
        time.setCycleCount(Timeline.INDEFINITE);
        KeyFrame frame = new KeyFrame(Duration.seconds(1), actionEvent -> {
            racingCounter.setText(Integer.toString(--timeSeconds));
            if (timeSeconds <= 0) {
                time.stop();
            }

        });
        time.getKeyFrames().add(frame);
        time.playFromStart();

//        // Configure the Label
//        racingCounter.setText(timeSeconds.toString());
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
        resetSet();
        goHomeBtn.setOnAction((event) -> {
            try {
                goToSceneIndicator("menu");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // -----------------------------------------------------------------------------------
    private void goToSceneIndicator(String nextScene) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(nextScene+".fxml"));
        Stage stage = (Stage) MainClient.stage.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }
    private void goToMessageScene(String gameStatus, String score, int maxScore) {
        Platform.runLater(() -> {
            MainClient.messageScence.setScore(score);
            if (Integer.parseInt(score) < maxScore)
                MainClient.messageScence.setMessage(false);
            else {
                MainClient.messageScence.setMessage(true);
            }
            MainClient.messageScence.doTime();
            MainClient.stage.setScene(MainClient.messageScence.getScene());
        });
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
        gridPane.setPrefWidth(800.0);
        AnchorPane.setLeftAnchor(gridPane, 0.0);
        if (anchorX > 0) {
            AnchorPane.setTopAnchor(gridPane, 0.0);
        } else {
            AnchorPane.setBottomAnchor(gridPane, 0.0);
        }
        return gridPane;
    }

    private Line createLine(int colIndex) {
        Line divider = new Line();
        divider.getStyleClass().add("racing-dashed-line");
        divider.setEndY(145.0);
        divider.setStroke(Paint.valueOf("WHITE"));
        divider.setStrokeWidth(2.0);
        GridPane.setColumnIndex(divider, colIndex);
        GridPane.setRowSpan(divider, GridPane.REMAINING);
        GridPane.setHalignment(divider, HPos.LEFT);
        return divider;
    }

    private void clearGrid(GridPane gridPane) {
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        gridPane.getChildren().clear();
    }

    private void resetSet() {
        Platform.runLater(()->{
            clearGrid(gridTop);
            clearGrid(gridBottom);
            clearGrid(gridPaneSmallScore);
            notiBoard.getChildren().clear();
        });
    }

    private void setRoadLength(String maxLength) {
        gridTop = initializeGridPane(1);
        gridBottom = initializeGridPane(-1);
        double initialLen = 55.0;
        double remainder = 800.0-initialLen*2;

        for (int i = 0; i < Integer.parseInt(maxLength)+1; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.SOMETIMES);
            if (i == 0 || i == Integer.parseInt(maxLength)) {
                column.setPrefWidth(initialLen);
            } else {
                column.setPrefWidth(remainder/(Double.parseDouble(maxLength)-1));
            }

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

        for (int y = 1; y <= Integer.parseInt(maxLength); y++) {
            Line dividerTop = createLine(y);
            Line dividerBottom = createLine(y);

            gridTop.getChildren().add(dividerTop);
            gridBottom.getChildren().add(dividerBottom);
        }

        anchorPaneContainer.getChildren().add(gridTop);
        anchorPaneContainer.getChildren().add(gridBottom);
    }
    // -----------------------------------------------------------------------------------
    /* Functions to find the resulting array for differences between firstArray and secondArray */
    private String[] findIndividualValues(String[] firstArray, String[] secondArray) {
        String[] filteredArray = Arrays.stream(firstArray)
                .filter(e -> {
                    return Arrays.stream(secondArray)
                            .filter(e2 -> {
                                return e.split(":")[0].equals(e2.split(":")[0]);
                            }).count() == 0;
                }).toArray(String[]::new);
        return filteredArray;
    }
    private String[] findDifferencesInArrays(String[] firstArray, String[] secondArray) {
        String[] onlyInFirst = findIndividualValues(firstArray, secondArray);
        String[] onlyInSecond = findIndividualValues(secondArray, firstArray);
        String[] both = Stream.concat(Arrays.stream(onlyInFirst), Arrays.stream(onlyInSecond))
                .toArray(String[]::new);
        return both;
    }
    // -----------------------------------------------------------------------------------

    public class PlayerConnector implements Runnable {
        Socket socket;
        Thread thread;
        DataInputStream inputStream;
        DataOutputStream outputStream;
        HashMap<String, String> connected = new HashMap<>();

        String userName;
        String host;
        int port;

        int questionCounter = 0;

        String maxScore;
        String score;
        String question;
        String otherScores = null;
        String gameStatus;

        // "EndGame_WinnerFound", "EndGame_Lose", "ContinueGame"

        public PlayerConnector(String host, int port, String userName) {
            this.host = host;
            this.port = port;
            this.userName = userName;
        }

        public HashMap connectToServer() {
            try {
                System.out.println("userName: "+userName);
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


                if (status.equals("successful")) {
                    String maxNumQuestions = info.split(" ")[3];

                    System.out.println(userName + " logged in successfully");
                    connected.put("status", "true");

                    this.maxScore = maxNumQuestions;
                    doTime();
                    Platform.runLater(() -> setRoadLength(this.maxScore));

                    racingUIRoom.setText(this.host+":"+this.port);
                    racingUILength.setText(this.maxScore);

                    return connected;
                }
                System.out.println("Logged in failed");
                connected.put("status", "false");
                return connected;
            } catch (IOException e) {
                System.out.println("Logged in failed");
                e.printStackTrace();
                connected.put("status", "false");
                return connected;
            }
        }
        // ------------------------------------------------------------------------------------
        public boolean updateScore() {
            try {
                score = this.inputStream.readUTF();
                timeSeconds = STARTTIME;
                return true;
            } catch (NumberFormatException | IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean updateQuestion() {
            try {
                question = this.inputStream.readUTF();
                ++this.questionCounter;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        // ------------------------------------------------------------------------------------
        public boolean updateOtherScores() {
        	/*
        	 * written by: Loc
        	 * contact me through this profile: www.facebook.com/duclockd1999
        	 * */
        	
        	try {
        	    String otherPlayerScores = this.inputStream.readUTF();
        	    if (this.otherScores == null) this.otherScores = otherPlayerScores;
        		updateOtherPlayerUI(otherPlayerScores);
        		return true;
        	}
        	catch (IOException e) {
        		e.printStackTrace();
        		return false;
        	}
        }
        // ---------------------------------------------------------------------------------
        public boolean updateGameStatus() {
        	/*
        	 * return true if game is continue, false if game is over (winner found, player lose)
        	 * 3 types of gameStatus:
        	 * 			"ContinueGame"
        	 * 			"EndGame_Lose"
        	 * 			"Endgame_WinnerFound"
        	 * */
        	
        	try {
        		this.gameStatus = this.inputStream.readUTF();

        		if (!this.gameStatus.equals("ContinueGame")) {
        		    // Chuyển sang trang message kèm câu lệnh thua
                    // Hiện noti để biết ai mới left
        			return false;
        		}
        		return true;
        	}
        	catch (IOException e) {
        		e.printStackTrace();
        		return false;
        	}
        }
        // ---------------------------------------------------------------------------------
        private void updateOtherPlayerUI(String otherScores) {
            String[] imgURLs = {"banhmi", "chair", "nonla", "toong", "fin"};
            String[] playersScore = otherScores.split(" ");
            String[] oldPlayersScore = this.otherScores.split(" ");
            if (playersScore.length == 0) return;

            if (oldPlayersScore.length != playersScore.length) {
                // find players not in oldPlayersScore
                String[] both = findDifferencesInArrays(oldPlayersScore, playersScore);
                String playersString = "";
                for (int i = 0; i < both.length; i++) {
                    if (i == both.length-1)
                        playersString += both[i].split(":")[0];
                    else
                        playersString += both[i].split(":")[0]+", ";
                }
                notiMessage.setText(playersString+" đã thua và rời phòng");
                this.otherScores = otherScores;
            } else {
                notiMessage.setText("");
            }

            int maxScore = Integer.parseInt(this.maxScore);
            ArrayList<HBox> scoreboardHBox = new ArrayList<>();

            Platform.runLater(()-> {
                int co = 0;
                String urlPathRoad;
                ImageView playerImageRoad;

                clearGrid(gridTop);
                clearGrid(gridBottom);
                setRoadLength(this.maxScore);

                for (int i = 0; i < playersScore.length; i++) {
                    String userName = playersScore[i].split(":")[0];
                    String score = playersScore[i].split(":")[1];
                    int tmpScore = Integer.parseInt(score);
                    urlPathRoad = "resources/player/player-" + imgURLs[i] + ".png";
                    playerImageRoad = createImageViewNode(urlPathRoad, 34);

                    // Update mini scoreboard
                    HBox hbox = new HBox();
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.setPrefHeight(100.0);
                    hbox.setPrefWidth(200.0);
                    hbox.setSpacing(8.0);

                    Text textNode = new Text(userName);
                    textNode.getStyleClass().add("player-small-text");

                    Text scoreNode = new Text(score);
                    scoreNode.getStyleClass().add("player-small-score");

                    ImageView imageViewNode = createImageViewNode(urlPathRoad, 28);

                    hbox.getChildren().addAll(imageViewNode, textNode, scoreNode);
                    scoreboardHBox.add(hbox);

                    // Update racing board
                    HBox imageHBox = new HBox();
                    imageHBox.setAlignment(Pos.CENTER);
                    imageHBox.getChildren().add(playerImageRoad);

                    if (tmpScore >= maxScore) {
                        timeSeconds = STARTTIME;
                    }

                    if (tmpScore <= 0)
                        GridPane.setColumnIndex(imageHBox, 0);
                    else
                        GridPane.setColumnIndex(imageHBox, Math.min(tmpScore, maxScore));

                    GridPane.setRowSpan(imageHBox, GridPane.REMAINING);
                    GridPane.setHalignment(imageHBox, HPos.CENTER);

                    switch (i) {
                        case 0:
                            GridPane.setRowIndex(imageHBox, 0);
                            gridTop.getChildren().add(imageHBox);
                            break;
                        case 1:
                            GridPane.setRowIndex(imageHBox, 1);
                            gridTop.getChildren().add(imageHBox);
                            break;
                        case 2:
                            GridPane.setRowIndex(imageHBox, 0);
                            gridBottom.getChildren().add(imageHBox);
                            break;
                        case 3:
                            GridPane.setRowIndex(imageHBox, 1);
                            gridBottom.getChildren().add(imageHBox);
                            break;
                    }
                }
            });

            Platform.runLater(()->{
                // Update mini scoreboard
                gridPaneSmallScore.getChildren().clear();
                int counter = 0;
                for (int x = 0; x < 2; x++) {
                    if (counter > scoreboardHBox.size()-1) break;
                    for (int y = 0; y < 2; y++) {
                        gridPaneSmallScore.add(scoreboardHBox.get(counter), x, y);
                        counter++;
                        if (counter > scoreboardHBox.size()-1) break;
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
                if (updateScore() && updateQuestion() && updateOtherScores()) {
                        if (updateGameStatus()) {
                            time.playFromStart();
                            timeSeconds = STARTTIME;
                            
                            String questionCounterString = "Câu " + Integer.toString(this.questionCounter) + ":";
                            if (this.questionCounter < 10) {
                                questionCounterString = "Câu 0" + Integer.toString(this.questionCounter) + ":";
                            }
                            questionCounterText.setText(questionCounterString);

                            Platform.runLater(() -> {
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
                                scoreResult.setText(getCurrentScore());
                                questionResult.setText(getCurrentQuestion());
                                if (this.questionCounter-1 == 0) {
                                    return;
                                }

                                String updatedScore = Integer.toString(nextScore - prevScore);

                                if (Integer.parseInt(updatedScore) > 0) {
                                    updatedScore = "+" + updatedScore;
                                }
                                if (!updatedScore.equals("0")) {
                                    updatedScore += " bước";

                                    HBox notiMessage = new HBox();
                                    Text textUsername = new Text(this.userName);
                                    Text textQuestionCount = new Text(Integer.toString(this.questionCounter-1)+". ");
                                    Text textScore = new Text(updatedScore);

                                    textUsername.getStyleClass().add("noti-text-username");
                                    textQuestionCount.getStyleClass().add("noti-text");
                                    textScore.getStyleClass().add("noti-text");

                                    notiMessage.getChildren().addAll(textQuestionCount, textUsername, textScore);
                                    notiBoard.getChildren().add(notiMessage);
                                    scrollPane.setVvalue(1D);
                                }
                            });
                        } else {
                            resetSet();
                            goToMessageScene(this.gameStatus, this.score, Integer.parseInt(this.maxScore));
                            break;
                        }
                    }
                else {
                	break;
                }
            }


        }
    }
}
