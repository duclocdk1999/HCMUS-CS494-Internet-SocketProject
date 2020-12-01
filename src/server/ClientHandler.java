package server;

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shared.Player;
import shared.Question;
import shared.WaitTime;

public class ClientHandler extends Thread {
	
	private final DataInputStream inputStream;
	private final DataOutputStream outputStream;
	private final int limitedAnswerTime;							// seconds
	private Socket client;
	private Player player;
	private int maxNumPlayers;
	private int maxScore;
	private int roomId;
	
	private static List<Map<Socket, Player>> socketPlayerMap;		// client (Socket) --> name (String)
	private static List<Player> fasttestPlayers;						// fastestPlayers.get(roomId)
	private static List<Integer> losePoints;						// losePoints.get(roomId): wrong answer point will be added to fastest player
	
	private static Question[] questions;
	private static String operators;	
	private static Integer numPlayers;								// number of available successful registered players
	// -----------------------------------------------------------------------------
	public ClientHandler(
			Socket client, 
			int roomId,
			int maxNumPlayers, 
			int maxScore, 
			int maxNumRooms, 
			int limitedAnswerTime) throws IOException {
		
		this.client = client;
		this.roomId = roomId;
		this.inputStream = new DataInputStream(client.getInputStream());
		this.outputStream = new DataOutputStream(client.getOutputStream());
		this.maxNumPlayers = maxNumPlayers;
		this.maxScore = maxScore;
		this.limitedAnswerTime = limitedAnswerTime;
		
		if (socketPlayerMap == null || operators == null || fasttestPlayers == null) {
			socketPlayerMap = new ArrayList<>();
			fasttestPlayers = new ArrayList<>();
			losePoints = new ArrayList<>();
			

			for (int i = 0; i < maxNumRooms; i++) {
				socketPlayerMap.add(new HashMap<>());
				fasttestPlayers.add(null);
				losePoints.add(0);
			}			
			operators = "+-*/%";
//			operators = "+";
		}
		else {
			this.player = socketPlayerMap.get(roomId).get(this.client);
		}
		
		if (numPlayers == null) {
			numPlayers = 0;
		}
		
		if (questions == null) {
			questions = new Question[maxNumRooms];
			for (int i = 0; i < maxNumRooms; i++) {
				questions[i] = new Question();
			}
		}
	}
	// -----------------------------------------------------------------------------
	private boolean isRegistered(String name) {
		// check if a name is registered by other players
		
		for (Entry<Socket, Player> entry: ClientHandler.socketPlayerMap.get(roomId).entrySet()) {
			if (entry.getValue().getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	// -----------------------------------------------------------------------------
	public void register() throws IOException {
		// this is called when a user want to register a name
		
		if (this.player == null) {			
			
			String name = null;
			do {
				// get name from the client, push it to server queue
				name = this.inputStream.readUTF();
				
				// check if name is already registered
				if (!isRegistered(name)) {
					this.player = new Player(name, 0);
					ClientHandler.socketPlayerMap.get(roomId).put(this.client, this.player);
					
					ClientHandler.numPlayers ++;
					this.outputStream.writeUTF("successful " 
											+ this.roomId + " " 
											+ this.maxNumPlayers + " " 
											+ this.maxScore + " " 
											+ numPlayers);
					break;
				}
				else {
					this.outputStream.writeUTF("failed");
				}
			}
			while (isRegistered(name));
		}
	}
	// -----------------------------------------------------------------------------
	private boolean test(Question question, String answerString) {
		
		Integer expectedAnswer = 0;
		try {
			Integer answer = Integer.parseInt(answerString);
			
			Integer num01 = question.getNumber01();
			Integer num02 = question.getNumber02();
			Integer operatorIndex = question.getOperatorIndex();

			switch (operatorIndex) {
			case 0:
				expectedAnswer = num01 + num02;
				break;
			case 1:
				expectedAnswer = num01 - num02;
				break;
			case 2:
				expectedAnswer = num01 * num02;
				break;
			case 3:
				expectedAnswer = num01 / num02;
				break;
			case 4:
				expectedAnswer = num01 % num02;
				break;
			}
			
			return (expectedAnswer.equals(answer));
		}
		catch (NumberFormatException e) {
			// e.printStackTrace();
			return false;
		}
		finally {
			System.out.println("expected aswer: " + expectedAnswer);
		}
	}
	// -----------------------------------------------------------------------------
	public static void generateQuestion(int roomId) {

		ClientHandler.questions[roomId].generateRandom();
	}
	// -----------------------------------------------------------------------------
	public static boolean allPlayersLose(int roomId) {
		/*
		 * If all players were banned --> no one left in room 
		 * */
		
		List<Player> players = new ArrayList<>(ClientHandler.socketPlayerMap.get(roomId).values());
		for (Player player: players) {
			if (!player.isLose()) {
				return false;
			}
		}
		return false;
	}
	// -----------------------------------------------------------------------------
	public static boolean foundWinningPlayer(int roomId, int maxScore) {
		
		/*
		 * Check if this roomId had a player with maximum score
		 * */
		
		List<Player> players = new ArrayList<>(ClientHandler.socketPlayerMap.get(roomId).values());
		for (Player player: players) {
			
			if (player.getScore().equals(maxScore)) {
				
				return true;
			}
		}
		return false;
	}
	// -----------------------------------------------------------------------------
	private void sendCurrentPlayerScoreAndQuestion() throws IOException {
	
		/*
		 * Format: 10 1 + 4
		 * where: 
		 * 		10 		=> current score of current player
		 * 		1 + 4	=> current question 
		 * */
		
		this.outputStream.writeUTF(ClientHandler.socketPlayerMap.get(roomId).get(this.client).getScore().toString());
		this.outputStream.writeUTF(questions[roomId].getNumber01() + " " 
								+ operators.charAt(questions[roomId].getOperatorIndex()) + " " 
								+ questions[roomId].getNumber02());
	}
	// -----------------------------------------------------------------------------
	private void sendOtherPlayerScores() throws IOException {
		
		/*
		 * Format: Loc:1 Nam:2 Chau:3 Minh:4
		 * 
		 * */
		
		List<Player> players = new ArrayList<>(ClientHandler.socketPlayerMap.get(roomId).values());
		String records = "";
		for (Player player: players) {
			records += player.getName() + ":" + player.getScore() + " ";
		}
		this.outputStream.writeUTF(records);
	}
	// -----------------------------------------------------------------------------
	private void sendGameStatus() throws IOException {
		
		if (ClientHandler.foundWinningPlayer(roomId, maxScore)) {
			
			this.outputStream.writeUTF("EndGame_WinnerFound");
		}
		else 
		if (this.player.isLose()){
			
			this.outputStream.writeUTF("EndGame_Lose");
		}
		else {
			
			this.outputStream.writeUTF("ContinueGame");
		}
	}
	// -----------------------------------------------------------------------------
	private String getAnswerFromPlayer() throws IOException {
		
		String answer = "";
		if (this.inputStream.available() > 0) {
			answer = this.inputStream.readUTF();
			System.out.println("answer from " + this.player.getName() + ": " + answer);		
		}
		else {
			System.out.println("answer from " + this.player.getName() + ": no answer...");								
		}
		return answer;
	}
	// -----------------------------------------------------------------------------
	private void checkAnswerAndUpdateScore(String answer) {
		
		if (this.test(questions[roomId], answer)) {

			this.player.addScore(1);
			
			if (fasttestPlayers.get(roomId) == null) {
				fasttestPlayers.set(roomId, this.player);
			}
		}
		else {
			this.player.addScore(-1);
			
			// update lost point (bonus for fastest player latter)
			int currentLosePoint = losePoints.get(roomId);
			losePoints.set(roomId, currentLosePoint + 1);
		}
	}
	// -----------------------------------------------------------------------------
	private long waitForAnswer() throws IOException {
		
		return new WaitTime().wait(limitedAnswerTime, inputStream);
	}
	// -----------------------------------------------------------------------------
	private void bonusPointForFastestPlayer() {
		
		Player fastestPlayer = fasttestPlayers.get(roomId);
		Integer bonus = losePoints.get(roomId);
		if (fastestPlayer != null) {
			
			if (fastestPlayer.getName().equals(this.player.getName())) {
				this.player.addScore(bonus);
				fasttestPlayers.set(roomId, null);
				losePoints.set(roomId, 0);
			}
		}
		else {
			losePoints.set(roomId, 0);
		}
	}
	// -----------------------------------------------------------------------------
	@Override
	public void run() {
		
		try {
			// User who's late for sending answer, clear these answer first
			if (this.inputStream.available() > 0) {
				this.inputStream.readUTF();
			}
			
			bonusPointForFastestPlayer();			
			sendCurrentPlayerScoreAndQuestion();
			sendOtherPlayerScores();
			sendGameStatus();
			
			this.player.display();
			if (!ClientHandler.foundWinningPlayer(roomId, maxScore) && !this.player.isLose()) {
				waitForAnswer();
				
				String answer = getAnswerFromPlayer();
				checkAnswerAndUpdateScore(answer);
			}
			
			socketPlayerMap.get(roomId).put(this.client, this.player);											
			
		} catch (IOException e) {
			
			e.printStackTrace();
			System.out.println("ClientHandler.run() failed! - " + this.player.getName());
		}
	}
	// -----------------------------------------------------------------------------
	public static void clearRegisteredNames(int roomId) {
		
		/*
		 * When the game's over, 
		 * new players participate the room and set nickname without worrying about their previous duplicated name
		 */
		
		socketPlayerMap.get(roomId).clear();
		fasttestPlayers.set(roomId, null);
		losePoints.set(roomId, 0);
	}
}

