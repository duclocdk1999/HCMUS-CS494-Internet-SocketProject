package server;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

public class MainServer extends Thread {
	
	private static final int port = 8080;
	private static final int maxScore = 26;						// maximum score to win game
	private static final int maxNumPlayers = 2;					// maximum number of players per room
	private static final int maxNumRooms = 3;					// maximum number of room concurrently
	private static final int limitedAnswerTime = 25;			// 60 seconds waiting for each question
	private static ServerSocket listener;
	
	private int roomId;
	private Socket[] clients = new Socket[maxNumPlayers];
	private ClientHandler[] clientHandlers = new ClientHandler[maxNumPlayers];
	// ----------------------------------------------------------------------------------
	public MainServer(int roomId) throws IOException {
		
		this.roomId = roomId;
		if (listener == null) {
			listener = new ServerSocket(port); 
		}
		System.out.println("Server room " + roomId + " is running on port " + port + "...");
	}
	// ----------------------------------------------------------------------------------
	public void establishConnection() throws IOException {
		
		for (int i = 0; i < maxNumPlayers; i ++) {
			clients[i] = (listener.accept());
		}
	}
	// ----------------------------------------------------------------------------------
	private void generatePlayersThread() throws IOException {
		
		for (int i = 0; i < maxNumPlayers; i ++) {
			clientHandlers[i] = new ClientHandler(clients[i], roomId, maxNumPlayers, maxScore, maxNumRooms, limitedAnswerTime);
		}		
	}
	// ----------------------------------------------------------------------------------
	private void registerBeforeGame() throws IOException {
		
		for (int i = 0; i < maxNumPlayers; i++) {
			clientHandlers[i].register();
		}
	}
	// ----------------------------------------------------------------------------------
	private void generateQuestionsForPlayersInRoom() {
		
		ClientHandler.generateQuestion(this.roomId);
	}
	// ----------------------------------------------------------------------------------
	private void sendQuestionToPlayers() {
		
		for (int i = 0; i < maxNumPlayers; i++) {
			clientHandlers[i].start();
		}
	}
	// ----------------------------------------------------------------------------------
	private void waitForPlayersAnswers() throws InterruptedException {
		
		for (int i = 0; i < maxNumPlayers; i++) {
			
			clientHandlers[i].join();
		}
	}
	// ----------------------------------------------------------------------------------
	public boolean foundWinningPlayer() {
		
		return ClientHandler.foundWinningPlayer(roomId, maxScore);
	}
	// ----------------------------------------------------------------------------------
	public boolean allPlayersLose() {
		
		return ClientHandler.allPlayersLose(roomId);
	}
	// ----------------------------------------------------------------------------------
	public void closePlayersThreads() throws IOException {

		ClientHandler.clearRegisteredNames(roomId);
		for (int i = 0; i < maxNumPlayers; i++) {
			clients[i].close();
		}
	}
	// ----------------------------------------------------------------------------------
	public void closeServerThread() throws IOException {
		
		listener.close();
	}
	// ----------------------------------------------------------------------------------
	@Override
	public void run() {
		
		try {
			
			boolean foundWinner = false, allLose = false;
			
			for (int i = 0; i < 2; i++) {					// loop 2 times to send last result to client
				do {
					if (!foundWinner) {
						System.out.println("found no winner...");
					}
					
					if (!allLose) {
						System.out.println("all players have not losed yet...");
					}
					
					generatePlayersThread();
					registerBeforeGame();
					generateQuestionsForPlayersInRoom();
					sendQuestionToPlayers();
					waitForPlayersAnswers();
					
				}	
				while (!(foundWinner = foundWinningPlayer()) && !(allLose = allPlayersLose()));
			}
			closePlayersThreads();
		}
		catch (IOException | InterruptedException e) {
			
			e.printStackTrace();
		}
	}
	// ----------------------------------------------------------------------------------
	public static void main(String[] args) throws IOException, InterruptedException {
		
		MainServer[] rooms = new MainServer[maxNumRooms];
		while (true) {
			for (int roomId = 0; roomId < maxNumRooms; roomId ++) {
				if (rooms[roomId] == null || !rooms[roomId].isAlive()) { 
					rooms[roomId] = new MainServer(roomId);
					rooms[roomId].establishConnection();
					rooms[roomId].start();					
				}
			}
		}
		
		// listener.close();
	}
}

