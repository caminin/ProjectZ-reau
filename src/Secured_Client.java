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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.math.BigInteger;
import static com.sun.org.apache.xalan.internal.lib.ExsltStrings.split;

/**
 * Created by caminin on 31/01/17.
 */
public class Secured_Client extends Application {
    private static boolean debug=false;
    private static boolean release=true;

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


    private Security security;
    private String client_name;
    private Message message;
    private String other_ip ="localhost";

    private PublicKey publicKey;   // clé publique de l'interlocuteur utilisée pour crypter les messages que l'on envoit
    private PrivateKey privateKey; // clé privé servant à décrypter les messages envoyés par l'interlocuteur

    public static void main(String args[]){
        Application.launch(args);
    }


    public void newClient(String ip){
        other_ip=ip;
        message=new Message(other_ip);
        message.receive(this);
    }

    public void sendMessage(String message){
        this.message.send(message);
    }

    /**
     * Envoie la clé publique à l'adresse ip sauvegardé au départ dans other_ip
     */
    public void sendPublicKey(){

        privateKey = security.getPrivateKey();
        sendMessage(":init://"+ client_name +":"+security.getPublicKey().getN().toString()+"/"+security.getPublicKey().getE());
    }

    /**
     * Envoie la chaîne de caractère pour demander la clé publique du destinataire
     */
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
            Log.debug("J'ai reçu une clé publique\n",release);
            String name_and_stringKey = message.replace(":init://","");
            String array_string[]=name_and_stringKey.split(":");
            String stringKey=array_string[1];
            String[] splitedKey = stringKey.split("/");
            publicKey = new PublicKey(new BigInteger(splitedKey[0].trim()), new BigInteger(splitedKey[1].trim()));
        }
        //Si c'est une demande pour la clé publique, on lui envoie
        else if(message.contains(":ask://")){
            Log.debug("J'ai reçu une demande de clé publique",release);
            sendPublicKey();
            Log.debug("Je lui envoie ma public key\n",release);
        }

        /* décrypte les autres messages et les affiche */
        else{
            String name=message.substring(0,message.indexOf(":"));
            String crypted_message=message.substring(message.indexOf(":")+1);
            String uncrypted_message;
            if(PrivateKey.isEncrypted(crypted_message)){
                Log.debug("J'ai reçu un message qui ressemble à ça : "+crypted_message.substring(0,50)+"... (coupé à 50 caractères)",release);
                uncrypted_message= privateKey.decryption(PrivateKey.splitString(crypted_message));
                Log.debug("Après décryptage, le message ressemble à ça : " + uncrypted_message+"\n",release);
            }
            else{
                Log.debug("Le message n'est pas encrypté",debug);
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

    public void sendHandler(){
        String message = msgField.getText();
        if(!message.isEmpty()){
            Log.debug("J'envoie le message : "+message,release);
            if(publicKey!=null){
                Log.debug("J'ai la clé publique, je commence l'encryptage",release);
                Text txt = new Text("me: "+message+"\n");
                txt.setFont(Font.font(14));
                txt.setFill(Color.BLUE);
                msgHistory.getChildren().add(txt);
                message = PublicKey.BigIntergerToString(publicKey.encryption(message));
                Log.debug("J'ai fini l'encryptage, j'envoie le message\n",release);
                msgField.setText("");
                sendMessage(client_name +":"+message);
            }
            else{//Si on n'a pas la clé, on la demande
                Log.debug("Je n'ai pas la clé publique, j'en fais la demande\n",release);
                askPublicKey();
            }

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
                    Log.debug("Génération des clés publiques et privées en cours",release);
                    newClient(ipField.getText());
                    Log.debug(other_ip,debug);

                    security= new Security();
                    security.genKeys();
                    sendPublicKey();

                    Log.debug("Génération finie et envoi de la clé effectué\n",release);
                    mainStage.setScene(chatScene);
                }
            }
        });

        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                sendHandler();
            }
        });

        chatScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER){
                    sendHandler();
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