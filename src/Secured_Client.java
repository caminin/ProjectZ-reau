import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import java.math.BigInteger;

import static com.sun.org.apache.xalan.internal.lib.ExsltStrings.split;

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
    @FXML private TextFlow msgHistory;
    @FXML private TextField msgField;
    @FXML private Button nameButton;
    @FXML private Button sendButton;
    @FXML private CheckBox localCheck;
    @FXML private TextField ipField;

    private String client_name;
    private Message message;
    private String other_ip ="localhost";

    private PublicKey publicKey;   // clé publique de l'interlocuteur utilisée pour crypter les messages que l'on envoit
    private PrivateKey privateKey; // clé privé servant à décrypter les messages envoyés par l'interlocuteur


    public Secured_Client(){
    }

    public static void main(String args[]){
        Application.launch(args);
    }

    @FXML protected void handleSendButton(ActionEvent event){
        if(nameField.getText()!= null) {
            client_name = nameField.getText();
            sendMessage(client_name);
            mainStage.setScene(chatScene);
        }
    }

    public void newClient(String ip){
        other_ip=ip;
        message=new Message(other_ip);
        message.receive(this);
    }

    public void sendMessage(String message){
        this.message.send(message);
    }

    public void sendPublicKey(){
        Security security = new Security();
        security.genKeys();
        privateKey = security.getPrivateKey();
        sendMessage(":init://"+ client_name +":"+security.getPublicKey().getN().toString()+"/"+security.getPublicKey().getE());
    }

    public void askPublicKey(){
        sendMessage(":ask://");
    }

    /**
     * Réceptionne l'ensemble des messages reçus et les traite
     * @param message, chaine de caractères contenant un message reçu
     */
    public void handleMessage(String message){
        /* réception du message d'initialisation contenant la clé publique de l'interlocuteur */
        if(message.contains(":init://")){
            String name_and_stringKey = message.replace(":init://","");
            String array_string[]=name_and_stringKey.split(":");
            String stringKey=array_string[1];
            String[] splitedKey = stringKey.split("/");
            publicKey = new PublicKey(new BigInteger(splitedKey[0].trim()), new BigInteger(splitedKey[1].trim()));
        }
        else if(message.contains(":ask://")){
            sendPublicKey();
            Log.debug("Je lui envoie ma public key",debug);
        }

        /* décrypte les autres messages et les affiche */
        else{
            String name=message.substring(0,message.indexOf(":"));
            String crypted_message=message.substring(message.indexOf(":")+1);
            String uncrypted_message;
            if(PrivateKey.isEncrypted(crypted_message)){
                uncrypted_message= privateKey.decryption(PrivateKey.splitString(crypted_message));
            }
            else{
                Log.debug("le message n'est pas encrypté",debug);
                sendPublicKey();
                uncrypted_message=crypted_message;
            }
            Text txt = new Text(name+": "+uncrypted_message+"\n");
            txt.setFill(Color.GREEN);
            txt.setFont(Font.font(14));
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    msgHistory.getChildren().add(txt);
                }
            });
        }

    }

    /**
     * Créé l'interface et implémente les handler
     * @param mainStage, fenêtre principale
     * @throws Exception
     */
    @Override
    public void start(final Stage mainStage) throws Exception {

        mainStage.setTitle("chat room");
        Scene nameScene = new Scene((Parent) FXMLLoader.load(getClass().getResource("enterName.fxml")));
        final Scene chatScene = new Scene((Parent) FXMLLoader.load(getClass().getResource("Chatroom.fxml")));
        mainStage.setScene(nameScene);
        mainStage.sizeToScene();
        mainStage.show();

        nameField = (TextField)nameScene.lookup("#nameField");
        nameButton = (Button)nameScene.lookup("#nameButton");
        localCheck = (CheckBox)nameScene.lookup("#local");
        ipField = (TextField)nameScene.lookup("#ipField");

        msgHistory  = (TextFlow) chatScene.lookup("#msgHistory");
        msgField = (TextField)chatScene.lookup("#msgField");
        sendButton = (Button)chatScene.lookup("#sendButton");

        mainStage.setOnCloseRequest(e -> Platform.exit());

        nameButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event){
                /* génération des clés et envoie du message d'initilisation contenant la clé publique */
                if(nameField.getText()!= null) {
                    client_name = nameField.getText();
                    newClient(ipField.getText());
                    Log.debug(other_ip,debug);
                    sendPublicKey();
                    mainStage.setScene(chatScene);
                }
            }
        });

        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String message = msgField.getText();
                if(!message.isEmpty()){
                    if(publicKey!=null){
                        Text txt = new Text("me: "+message+"\n");
                        txt.setFont(Font.font(14));
                        txt.setFill(Color.BLUE);
                        msgHistory.getChildren().add(txt);
                        message = PublicKey.BigIntergerToString(publicKey.encryption(message));
                        msgField.setText("");
                        sendMessage(client_name +":"+message);
                    }
                    else{//Si on n'a pas la clé, on la demande
                        askPublicKey();
                        //TODO tell the man that you can't send a message, so he needs to wait (add a while(publickey==null){Thread.sleep(1000);}) ?
                    }

                }

            }
        });

        localCheck.setOnAction((event)->{
            if(localCheck.isSelected()){
                ipField.setText("localhost");
            }
            else{
                ipField.setText("");
            }
        });
    }


}