import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;

public class Serveur {
	private ServerSocket socket;
	
	// number of clients connected to the server
	private static int nbClients;
	
	// contains all the clients connected to the server
	private ArrayList<Echange> clients;

	public Serveur(){
		try {
			socket=new ServerSocket(7030,7030, InetAddress.getByName("192.168.99.169"));
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
	
	public static void main(String args[]){
		Serveur serveur=new Serveur();
		serveur.launch();
	}
}