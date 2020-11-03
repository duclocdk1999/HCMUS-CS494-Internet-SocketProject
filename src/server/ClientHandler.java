package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ClientHandler extends Thread {
	
	private final DataInputStream inputStream;
	private final DataOutputStream outputStream;
	private Socket client;
	
	private String name;
	private Integer score;
	
	private static Map<String, Integer> nameScoreMap;						// name (String) --> score (Integer)	
	private static Map<Socket, String> clientNameMap;						// client (Socket) --> name (String)
	private static int number01 = 0, number02 = 0, operatorIndex = 0;
	private static String operators;	
	// --------------------------------------------------------------------
	public ClientHandler(Socket client) throws IOException {
		
		this.client = client;
		this.inputStream = new DataInputStream(client.getInputStream());
		this.outputStream = new DataOutputStream(client.getOutputStream());
		this.score = 0;
		
		if (clientNameMap == null || nameScoreMap == null || operators == null) {
			clientNameMap = new HashMap<>();
			nameScoreMap = new HashMap<>();
			operators = "+-*/%";
		}
		else {
			this.name = clientNameMap.get(this.client);
			this.score = nameScoreMap.get(this.name);
			if (this.score == null) {
				this.score = 0;
			}
		}
	}
	// --------------------------------------------------------------------
	public void register() throws IOException {
		
		if (this.name != null) {
			return;
		}
		do {
			// get name from the client, push it to server queue
			this.name = this.inputStream.readUTF();
			
			// check if name is already registered
			if (!ClientHandler.clientNameMap.containsKey(this.client)) {
				ClientHandler.clientNameMap.put(this.client, this.name);
				ClientHandler.nameScoreMap.put(this.name, 0);

				this.outputStream.writeUTF("sucessful");
				break;
			}
			else {
				this.outputStream.writeUTF("failed");
			}
		}
		while (ClientHandler.clientNameMap.containsKey(this.client));
	}
	// -------------------------------------------------------------
	private boolean test(Integer num01, Integer num02, Integer operatorIndex, Integer answer) {
		
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
	// -------------------------------------------------------------
	public static void generateQuestion() {

		Random random = new Random();
		ClientHandler.number01 = random.nextInt(20000) - 10000;
		ClientHandler.number02 = random.nextInt(20000) - 10000;
		ClientHandler.operatorIndex = random.nextInt(5);
	}
	// -------------------------------------------------------------
	@Override
	public void run() {
		
		try {
			// send current score from server to client
			this.outputStream.writeUTF(ClientHandler.nameScoreMap.get(this.name).toString());

			// when the users complete journey
			if (this.score >= 3) {
				this.outputStream.writeUTF("winning!");
				this.inputStream.close();
				this.outputStream.close();
			}
			
			this.outputStream.writeUTF(ClientHandler.number01 + " " + operators.charAt(operatorIndex) + " " + ClientHandler.number02);
			
			// get the answer from the client
			String answer = this.inputStream.readUTF();
			System.out.println("answer from " + this.name + ": " + answer);
			
			// check result, modify score
			if (this.test(ClientHandler.number01, ClientHandler.number02, operatorIndex, Integer.valueOf(answer))) {
				this.score ++;
			}
			else {
				this.score --;
			}
			ClientHandler.nameScoreMap.put(this.name, this.score);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
