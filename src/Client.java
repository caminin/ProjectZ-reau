import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Client extends Application {
		
	// chat GUI components
	private Stage mainStage;
	private Scene nameScene;
	private Scene chatScene;
	
	@FXML private TextField nameField;
	@FXML private TextArea msgHistory;
	@FXML private TextField msgField;
	@FXML private Button nameButton;
	@FXML private Button sendButton;
	
	private Socket socket;
	private String id;

	private String myIp="172.29.8.130";

	// used to send messages to the server
	private ObjectInputStream in=null;
	
	// used to receive messages from the server
	private ObjectOutputStream out=null;
	
	public Client(){
		try {
			socket=new Socket(myIp,7030);
			out=new ObjectOutputStream(socket.getOutputStream());
			in=new ObjectInputStream(socket.getInputStream());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String message){
		try {
			out.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]){
		Application.launch(args);	
	}
	
	@FXML protected void handleSendButton(ActionEvent event){
		if(nameField.getText()!= null) {
			id = nameField.getText();
			sendMessage(id);
			mainStage.setScene(chatScene);
       }
	}

	@Override
	public void start(final Stage mainStage) throws Exception {
		mainStage.setTitle("chat room");
		Scene nameScene = new Scene((Parent) FXMLLoader.load(getClass().getResource("enterName.fxml")));
		final Scene chatScene = new Scene((Parent) FXMLLoader.load(getClass().getResource("Chatroom.fxml")));
		mainStage.setScene(nameScene);
		
		mainStage.show();
		
		nameField = (TextField)nameScene.lookup("#nameField");
		nameButton = (Button)nameScene.lookup("#nameButton");
		
		msgHistory  = (TextArea)chatScene.lookup("#msgHistory");
		msgField = (TextField)chatScene.lookup("#msgField");
		sendButton = (Button)chatScene.lookup("#sendButton");
		
		mainStage.setOnCloseRequest(e -> Platform.exit());
		
		nameButton.setOnAction(new EventHandler<ActionEvent>(){
		public void handle(ActionEvent event){
			if(nameField.getText()!= null) {
				id = nameField.getText();
				sendMessage(id);
				mainStage.setScene(chatScene);
	        }
		}			
		});
		
		
		sendButton.setOnAction(new EventHandler<ActionEvent>() {
	        public void handle(ActionEvent event) {
	        	String message = msgField.getText();
	        	if(!message.isEmpty()){
	                sendMessage(message);
	                msgHistory.appendText("me :"+message+"\n");
	        	}
	            msgField.setText("");
	        }
    	});
		

		
	    // Thread in charge of messages reception
		ThreadReceptionMessage reception = new ThreadReceptionMessage(in,msgHistory);
		reception.start();	
	}
}