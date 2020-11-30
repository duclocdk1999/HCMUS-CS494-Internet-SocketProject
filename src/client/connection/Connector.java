package client.connection;

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.Scanner;

import shared.Player;
import shared.WaitTime;

public class Connector {
	
	private int limitedAnswerTime = 10;						// limit the time for player to answer, in second
	
	private Socket client;									// socket used to communicate with server
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	
	private Player player;									
	private String question;
	private String otherScores;
	
	private Integer maxNumPlayers;							// maximum number of players
	private Integer maxNumQuestions;						// maximum number of questions
	private Integer roomId;									
	// ------------------------------------------------------------------------------	
	public Connector(String ip, int port) throws UnknownHostException, IOException {
		
		this.client = new Socket(ip, port);
		this.inputStream = new DataInputStream(this.client.getInputStream());
		this.outputStream = new DataOutputStream(this.client.getOutputStream());
		this.maxNumPlayers = 0;
		this.maxNumQuestions = 0;
		this.roomId = 0;

		System.out.println("client is connected to " + ip + " port " + port + "...");
	}
	// ------------------------------------------------------------------------------
	public boolean register(String name) {
		
		try {
			System.out.println("me-name: "+name);
			this.outputStream.writeUTF(name);
			String info = this.inputStream.readUTF();			// info format:	"successful roomId maxNumPlayers maxNumQuestions" | "failed"
			String status = info.split(" ")[0];
			
			if (status.equals("successful")) {
				this.player = new Player(name, 0);
				this.roomId = Integer.valueOf(info.split(" ")[1]);
				this.maxNumPlayers = Integer.valueOf(info.split(" ")[2]);
				this.maxNumQuestions = Integer.valueOf(info.split(" ")[3]);
				
				System.out.println(name + " register sucessfully, room " + this.roomId);
				return true;
			}
			return false;
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}	
	}
	// ------------------------------------------------------------------------------
	public boolean updateScore() {
		
		// get current score from server
		try {
			String stringScore = this.inputStream.readUTF();
			this.player.setScore(Integer.valueOf(stringScore));
			
			return true;
		} catch (NumberFormatException | IOException e) {
			return false;
		}
	}
	// ------------------------------------------------------------------------------
	public boolean updateQuestion() {
		
		// get current question from server
		try {
			this.question = this.inputStream.readUTF();
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}
	// ------------------------------------------------------------------------------
	public boolean updateOtherScores() {
		
		try {
			this.otherScores = this.inputStream.readUTF();
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}
	// ------------------------------------------------------------------------------
	public boolean updateGameStatus() {
		
    	/*
    	 * return true if game is continue, false if game is over (winner found)
    	 * */
    	
    	try {
    		
    		String status = this.inputStream.readUTF();
    		System.out.println("game status: " + status);
    		
    		if (status.equals("winnerNotFound")) {
    			return true;        			
    		}
    		return false;
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    		return false;
    	}
	}
	// ------------------------------------------------------------------------------
	public boolean submitAnswer(String answer) {
		
		// send result back to the server
		try {
			this.outputStream.flush();
			this.outputStream.writeUTF(answer);
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Connector.submitAnswer() failed!");
			return false;
		}
	}
	// ------------------------------------------------------------------------------
	public String getCurrentQuestion() {
		
		return this.question;
	}
	// ------------------------------------------------------------------------------
	public Integer getCurrentScore() {
		
		return this.player.getScore();
	}
	// ------------------------------------------------------------------------------
	public String getOtherScores() {
		
		return this.otherScores;
	}
	// ------------------------------------------------------------------------------
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		Connector connector = new Connector("localhost", 8080);
		String name = null;
		Scanner scanner = new Scanner(System.in);
		
		// register
		while (name == null || connector.register(name) == false) {
			System.out.print("Pls enter your name: ");
			name = scanner.nextLine();
		}

		// playing game
		String answer;
		while (connector.updateScore() 
				&& connector.updateQuestion() 
				&& connector.updateOtherScores()
				&& connector.updateGameStatus()) {
			// unable to updateScore means server disconnected
			
			System.out.println("score: " + connector.getCurrentScore());			
			System.out.println("question: " + connector.getCurrentQuestion());
			System.out.println("other player score: " + connector.getOtherScores());
			
			long answerTime = new WaitTime().wait(connector.limitedAnswerTime, System.in);
			if (System.in.available() > 0) {
				answer = scanner.nextLine();
			}
			else {
				answer = "no answer";
			}
			
			connector.submitAnswer(answer);				
		}
		scanner.close();
	}
}
