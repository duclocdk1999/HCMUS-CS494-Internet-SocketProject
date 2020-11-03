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
	
	public Connector(String ip, int port) throws UnknownHostException, IOException {
		
		this.client = new Socket(ip, port);		
		this.inputStream = new DataInputStream(this.client.getInputStream());
		this.outputStream = new DataOutputStream(this.client.getOutputStream());
		System.out.println("client is connected to " + ip + " port " + port + "...");
	}
	// -------------------------------------------------------------
	public boolean register(String name) {
		
		try {
			this.outputStream.writeUTF(name);
			String registerStatus = this.inputStream.readUTF();
			if (registerStatus.equals("sucessful")) {
				return true;
			}
			return false;
			
		} catch (IOException e) {

			e.printStackTrace();
			return false;
		}
	}
	// -------------------------------------------------------------
	public void start() throws IOException {
		
		while (true) {
			// get current score from server
			Integer score = Integer.valueOf(this.inputStream.readUTF());
			System.out.println("current score: " + score);

			if (score == 3) {
				this.client.close();
				System.out.println("winning");
				break;
			}
			else {
				// get question from the server
				String question = this.inputStream.readUTF();
				System.out.println(question);

				// send result back to the client
				Scanner scanner = new Scanner(System.in);
				String answer = scanner.nextLine();
				scanner.close();
				outputStream.writeUTF(answer);			
			}			
		}
	}
	// -------------------------------------------------------------
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		Connector connector = new Connector("localhost", 8080);
		String name = null;
		Scanner scanner = new Scanner(System.in);
		while (name == null || !connector.register(name)) {
			name = scanner.nextLine();
		}
		scanner.close();
		connector.start();
	}
}








