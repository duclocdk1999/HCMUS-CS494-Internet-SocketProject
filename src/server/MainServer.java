package server;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {
	
	private static final int port = 8080;
	private static final int maxNumQuestions = 2;
	private static final int maxNumPlayers = 2;
	private static Socket[] clients = new Socket[maxNumPlayers];
	private static ClientHandler[] clientHandlers = new ClientHandler[maxNumPlayers];
	
	// ----------------------------------------------------------------------------------
	public static void main(String[] args) throws IOException, InterruptedException {
		
		// create server socket with port
		ServerSocket listener = new ServerSocket(port);
		System.out.println("Server is running on port " + port + "...");
		
		// establish connection
		for (int i = 0; i < maxNumPlayers; i ++) {
			clients[i] = (listener.accept());
		}
		
		for (int questionIndex = 0; questionIndex < maxNumQuestions; questionIndex ++) {
			
			// generate thread for all players
			for (int i = 0; i < maxNumPlayers; i ++) {
				clientHandlers[i] = new ClientHandler(clients[i], MainServer.maxNumPlayers, MainServer.maxNumQuestions);
			}
			
			// register name first before playing
			for (int i = 0; i < maxNumPlayers; i++) {
				clientHandlers[i].register();
			}
						
			// generate random question
			ClientHandler.generateQuestion();
			
			// send question to 3 players simultaneously
			for (int i = 0; i < maxNumPlayers; i++) {
				clientHandlers[i].start();
			}
			
			// 3 threads has to be completed before new question
			for (int i = 0; i < maxNumPlayers; i++) {
				clientHandlers[i].join();
			}
		}
		
		// close threads
		for (int i = 0; i < maxNumPlayers; i++) {
			clients[i].close();
		}

		listener.close();
	}
	
}
