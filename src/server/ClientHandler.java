package server;

import java.io.DataInputStream;


import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shared.Player;

import java.util.Random;

public class ClientHandler extends Thread {
		
	private final DataInputStream inputStream;
	private final DataOutputStream outputStream;
	private Socket client;
	private Player player;
	private int maxNumPlayers;
	private int maxNumQuestions;
	
	private static Map<Socket, Player> socketPlayerMap;						// client (Socket) --> name (String)
	private static int number01 = 0, number02 = 0, operatorIndex = 0;
	private static String operators;	
	private static Integer numPlayers;										// number of available successful registered players
	// -----------------------------------------------------------------------------
	public ClientHandler(Socket client, int maxNumPlayers, int maxNumQuestions) throws IOException {
		
		this.client = client;
		this.inputStream = new DataInputStream(client.getInputStream());
		this.outputStream = new DataOutputStream(client.getOutputStream());
		this.maxNumPlayers = maxNumPlayers;
		this.maxNumQuestions = maxNumQuestions;
		
		if (socketPlayerMap == null || operators == null) {
			socketPlayerMap = new HashMap<>();
			operators = "+-*/%";
		}
		else {
			this.player = socketPlayerMap.get(this.client);
		}
		
		if (numPlayers == null) {
			numPlayers = 0;
		}
	}
	// -----------------------------------------------------------------------------
	private boolean isRegistered(String name) {
		// check if a name is registered by other players
		
		for (Entry<Socket, Player> entry: ClientHandler.socketPlayerMap.entrySet()) {
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
					ClientHandler.socketPlayerMap.put(this.client, this.player);
					
					ClientHandler.numPlayers ++;
					this.outputStream.writeUTF("successful " + this.maxNumPlayers + " " + this.maxNumQuestions + " " + numPlayers);
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
	private boolean test(Integer num01, Integer num02, Integer operatorIndex, String answerString) {
		
		try {
			Integer answer = Integer.parseInt(answerString);
			Integer expectedAnswer = 0;
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
			
			System.out.println("expected aswer: " + expectedAnswer);
			return (expectedAnswer.equals(answer));
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	// -----------------------------------------------------------------------------
	public static void generateQuestion() {

		Random random = new Random();
		ClientHandler.number01 = random.nextInt(20000) - 10000;
		ClientHandler.number02 = random.nextInt(20000) - 10000;
		ClientHandler.operatorIndex = random.nextInt(5);
	}
	// -----------------------------------------------------------------------------
	@Override
	public void run() {
		
		try {
			// send current score from server to client
			this.outputStream.writeUTF(ClientHandler.socketPlayerMap.get(this.client).getScore().toString());
			this.outputStream.writeUTF(ClientHandler.number01 + " " + operators.charAt(operatorIndex) + " " + ClientHandler.number02);
			
			// get the answer from the client
			String answer = this.inputStream.readUTF();
			System.out.println("answer from " + this.player.getName() + ": " + answer);
			
			// check result, modify score
			if (this.test(ClientHandler.number01, ClientHandler.number02, operatorIndex, answer)) {
				
				this.player.setScore(this.player.getScore() + 1);
			}
			else {
				
				this.player.setScore(this.player.getScore() - 1);
			}
			ClientHandler.socketPlayerMap.put(this.client, this.player);
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ClientHandler.run() failed! - " + this.player.getName());
		}
	}
	// -----------------------------------------------------------------------------
	public static List<Player> getResult() {
		
		List<Player> players = new ArrayList<>(ClientHandler.socketPlayerMap.values());
		Collections.sort(players);
		return players;
	}
}
