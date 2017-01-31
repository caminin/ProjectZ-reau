import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Serveur {
	private ServerSocket socket;
	
	// number of clients connected to the server
	private static int nbClients;
	
	// contains all the clients connected to the server
	private ArrayList<Echange> clients;

	private String myIp="172.29.8.130 ";

	public Serveur(){
		try {
			socket=new ServerSocket(7030,7030, InetAddress.getByName(myIp));
			clients=new ArrayList<Echange>();

		} 
		catch (IOException e) {
		} 
	}
	
	public void launch(){
		Echange echange;
		while(true){
			try {
				echange=new Echange(socket.accept(),nbClients,this);
				clients.add(echange);
				echange.start();
		
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			nbClients++;
		}

	}
	

	// send a message from a client to all the others
	public void broadcast(int idSender,String nameSender,String message){
		Echange client;
		for(Iterator<Echange> iterator=clients.iterator();iterator.hasNext();){
			client=iterator.next();
			if(client.isAlive()){
				if(client.getClientId()!=idSender){
					System.out.println("server sent to client "+client.getClientName()+" message : "+message);
					client.send(nameSender,message);
				}
			}
			// client is no longer connected
			else{
				iterator.remove();
			}
			
		}	
	}

	private Client getNewClient(){


		return null;
	}
	
	public static void main(String args[]){
		Serveur serveur=new Serveur();
		serveur.launch();
	}
}