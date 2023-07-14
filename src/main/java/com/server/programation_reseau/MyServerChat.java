package com.server.programation_reseau;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.ls.LSInput;


public class MyServerChat extends Thread {
	
	//Le nombre de port
	public static final int portNumber = 1234;
	
	private int nombreClient;
	
	private List<Conversation> clients = new ArrayList<MyServerChat.Conversation>();
	
    public static void main( String[] args ) {
    	new MyServerChat().start();
    }
    
    @Override
    public void run() {
    	 try {
 			ServerSocket serverSocket  = new ServerSocket(portNumber);
 			System.out.println("Le serveur est en attente de connexions sur le port " + portNumber + "...");
 			
 			while (true) {
 				
 				// Le serveur est prêt à accepter les connexions entrantes
 				Socket clientSocket = serverSocket.accept();
 				 System.out.println("Nouvelle connexion établie : " + clientSocket.getInetAddress());
 				 ++nombreClient;
 				 
 				 Conversation client = new Conversation(clientSocket, nombreClient);
 				 clients.add(client);
 				 client.start();
 			    
 			}
 			
 			
 		} catch (IOException  e) {
 			System.out.println("probléme sur le serveur.");
 			e.printStackTrace();
 		} 
    }
    
    class Conversation extends Thread {
    	private Socket clientSocket;
    	private int numero;
    	
    	public Conversation(Socket clientSocket, int numero) {
    		this.clientSocket = clientSocket;
    		this.numero = numero;
		}
    	
    	public void broadcastMessage(String message, Socket socket) {
    		try {
    			for(Conversation client : clients) {
    				if(client.clientSocket!=socket) {
    					PrintWriter printWriter = new PrintWriter(client.clientSocket.getOutputStream(),true);
					    printWriter.println(message);
    				}
    			
    			}
    		} catch (IOException e) {
				e.printStackTrace();
			}
    	}

    	
    	@Override
    	public void run() {
    		
			try {
				// Flux d'entrée du client
				InputStream input = clientSocket.getInputStream();

				//Pour lire chaine de caractére envoyé par le client
				InputStreamReader isr = new InputStreamReader(input);
				BufferedReader br = new BufferedReader(isr);
				

				// Flux de sortie vers le client
				OutputStream output = clientSocket.getOutputStream();
				PrintWriter pw =  new PrintWriter(output,true);
				String ipClient = clientSocket.getRemoteSocketAddress().toString();
				pw.println("Bien venue, vous le client numéo "+numero);
				System.out.println("Connexion du client numéro "+numero+" , IP="+ipClient);
				
				while (true) {
					String req = br.readLine();
					broadcastMessage(req, clientSocket);
					
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    }
    
}
