package server;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {
	
	private static final int port = 8080;
	// ----------------------------------------------------------------------------------
	public static void main(String[] args) throws IOException, InterruptedException {
		
		// create server socket with port
		ServerSocket listener = new ServerSocket(port);
		System.out.println("Server is running on port " + port + "...");
		
		// establish connection
		Socket client01 = listener.accept();
		Socket client02 = listener.accept();
		Socket client03 = listener.accept();

		ClientHandler thread01 = null;
		ClientHandler thread02 = null;
		ClientHandler thread03 = null;
		
		int numOfQuestions = 3;
		for (int questionIndex = 0; questionIndex < numOfQuestions; questionIndex ++) {
			
			// generate thread for 3 players
			thread01 = new ClientHandler(client01);
			thread02 = new ClientHandler(client02);
			thread03 = new ClientHandler(client03);
			
			// register name first before playing
			thread01.register();
			thread02.register();
			thread03.register();
			
			// generate random question
			ClientHandler.generateQuestion();
			
			// send question to 3 players simultaneously
			thread01.start();
			thread02.start();
			thread03.start();
			
			// 3 threads has to be completed before new question
			thread01.join();
			thread02.join();
			thread03.join();	
		}
		
		// close threads
		client01.close();
		client02.close();
		client03.close();
		listener.close();
	}
	
}
