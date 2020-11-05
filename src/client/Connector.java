package client;

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.Scanner;

public class Connector {
	
	private Socket client;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	
	private Player player;
	private String question;
	// ------------------------------------------------------------------------------	
	public Connector(String ip, int port) throws UnknownHostException, IOException {
		
		this.client = new Socket(ip, port);
		this.inputStream = new DataInputStream(this.client.getInputStream());
		this.outputStream = new DataOutputStream(this.client.getOutputStream());
		System.out.println("client is connected to " + ip + " port " + port + "...");
	}
	// ------------------------------------------------------------------------------
	public boolean register(String name) {
		
		try {
			this.outputStream.writeUTF(name);
			String registerStatus = this.inputStream.readUTF();
			if (registerStatus.equals("sucessful")) {
				this.player = new Player(name, 0);
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
	public boolean submitAnswer(String answer) {
		
		// send result back to the server
		try {
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
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		Connector connector = new Connector("localhost", 8080);
		String name = null;
		Scanner scanner = new Scanner(System.in);
		
		// register
		while (name == null || !connector.register(name)) {
			name = scanner.nextLine();
		}

		// playing game
		String answer;
		while (connector.updateScore() && connector.updateQuestion()) {
			// unable to updateScore mean server disconnected
			
			System.out.println("score: " + connector.getCurrentScore());			
			System.out.println("question: " + connector.getCurrentQuestion());
			
			answer = scanner.nextLine();
			connector.submitAnswer(answer);
		}
		scanner.close();
	}
}