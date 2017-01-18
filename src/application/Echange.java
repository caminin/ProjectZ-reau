package application;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class Echange extends Thread {
	private int idClient;
	private String nameClient;
	private Serveur serveur;
	
	// input receiving messages from client
	private ObjectInputStream in;
	
	// output sending messages to client
	private ObjectOutputStream out;
	
	public	Echange(Socket socket,int idClient,Serveur serveur){
		this.idClient=idClient;
		this.serveur=serveur;
		try{
			this.in=new ObjectInputStream(socket.getInputStream());
			this.out=new ObjectOutputStream(socket.getOutputStream());
			nameClient=(String)in.readObject();
			System.out.println("new connection client: "+nameClient);
		}catch(Exception e){
		}
	}
	
	public void run(){
		try {
			String msg=new String();
			// read messages sent by client
			while(true){
				msg=(String)in.readObject();
				// display the message in the command prompt
				System.out.println("Client "+nameClient+" sent: "+msg);
				// call the server send method to send message to all the others clients
				serveur.broadcast(idClient,nameClient,msg);
			}
		}catch (ClassNotFoundException e1){

		}
		catch(IOException e2){
			
		}
	}
	
	public void send(String idSender,String message){
		try {
			out.writeObject(idSender+" : "+message);
		} catch (IOException e) {
		}
	}
	
	public String getClientName(){
		return nameClient;
	}
	
	public int getClientId(){
		return idClient;
	}
}