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

    @FXML
    private TextField nameField;
    @FXML private TextArea msgHistory;
    @FXML private TextField msgField;
    @FXML private Button nameButton;
    @FXML private Button sendButton;

    private String id;
    private Message message;

    public Secured_Client(){
        try {

            launch();
        }
        catch (Exception e) {
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

    public void launch(){
        message=new Message();
        message.receive(this);
    }

    public void sendMessage(String message){
        this.message.send(message);
    }

    public void handleMessage(String message){
        msgHistory.appendText(message+"\n");

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

    }


}