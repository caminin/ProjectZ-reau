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
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by caminin on 31/01/17.
 */
public class Secured_Client extends Application {
    private static boolean debug=true;

    // chat GUI components
    private Stage mainStage;
    private Scene nameScene;
    private Scene chatScene;

    // public and private key to secure communication with the other client
    private PrivateKey privateKey; // decrypt messages received
    private PublicKey publicKey; // encrypt messages sent

    @FXML
    private TextField nameField;
    @FXML private TextArea msgHistory;
    @FXML private TextField msgField;
    @FXML private Button nameButton;
    @FXML private Button sendButton;

    private String id;
    private Message message;

    public Secured_Client(){
        message=new Message();
        message.receive(this);

        //TODO create the private and public key
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

    public void sendMessage(String message){
        this.message.send(message);
    }

    public void handleMessage(String message){
        // message d'initialisation de la connexion, contient la clé publique de l'interlocuteur
        if(message.contains(":init://")){
            String initMessage = message.replace(":init://","");//TODO splt the two biginteger and store the public key
            String[] stringKey = initMessage.split("/");
            publicKey = new PublicKey(new BigInteger(stringKey[0]), new BigInteger(stringKey[1]));
        }

        // Decryptage et affichage du message envoyé par l'interlocuteur
        else{//message dans le chat, avec un truc crypté
            String name = message.substring(0,message.indexOf(":"));
            String crypted_message = message.substring(message.indexOf(":")+1);//TODO uncrypt the message
//            for(int i = 0; i < crypted_message.)
//                String decrypted_message = privateKey.decryption();
//
//            msgHistory.appendText(name+":"+decrypted_message+"\n");
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
                    Security security = new Security();
                    security.genKeys();
                    privateKey = security.getPrivateKey();
                    sendMessage(":init://"+id+":"+security.getPublicKey().getN()+"/"+security.getPublicKey().getE());//TODO Add the public key
                    mainStage.setScene(chatScene);
                }
            }
        });


        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String message = msgField.getText();
                if(!message.isEmpty()){
                    sendMessage(id+":"+publicKey.encryption(message).toString());//TODO crypter message, /!\ envoyer un tableau de BigIntegers
                    msgHistory.appendText("me :"+message+"\n");
                }
                msgField.setText("");
            }
        });

    }


}