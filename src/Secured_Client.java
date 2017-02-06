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

    // number of clients connected to the server
    private static int nbClients;

    // contains all the clients connected to the server
    private ArrayList<Echange> clients;

    private ServerSocket server_socket;
    private Socket client_socket=null;

    private String id;

    private String myIp="172.29.20.197";

    // used to send messages to the server
    private ObjectInputStream in=null;

    // used to receive messages from the server
    private ObjectOutputStream out=null;

    public Secured_Client(){
        try {
            server_socket=new ServerSocket(7031,7031, InetAddress.getByName(myIp));
            launch();
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

    public void launch(){
        try {
            InetAddress IP=InetAddress.getLocalHost();
            System.out.println("IP of my system is := "+IP.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Echange echange;
        SendIp send_ip=new SendIp();
        send_ip.start();
        ReceiveIP trade_ip=new ReceiveIP(this);
        trade_ip.start();
        try {
            echange=new Echange(server_socket.accept(),nbClients,this);
            send_ip.stop();
            clients.add(echange);
            echange.start();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        nbClients++;
    }

    public void connect(String IP){
        try {
            client_socket=new Socket(IP,7031);
            out=new ObjectOutputStream(client_socket.getOutputStream());
            in=new ObjectInputStream(client_socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // send a message from a client to all the others
    public void broadcast(int idSender,String nameSender,String message){
        Echange client;
        for(Iterator<Echange> iterator = clients.iterator(); iterator.hasNext();){
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


    private class ReceiveIP extends Thread{
        DatagramSocket socket;

        Secured_Client client;

        public ReceiveIP(Secured_Client client) {
            this.client=client;
            try {
                socket = new DatagramSocket(7030, InetAddress.getByName(myIp));
                socket.setBroadcast(true);
            } catch (Exception e){
                e.printStackTrace();
            }

        }
        public void run(){
            try {
                //Keep a socket open to listen to all the UDP trafic that is destined for this port
                System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");

                //Receive a packet
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                //Packet received
                //System.out.println(getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
                System.out.println(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));

                //See if the packet holds the right command (message)
                String message = new String(packet.getData()).trim();
                if (message.contains("IP:")) {
                    message = message.replace("IP:", "");
                    client.connect(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendIp extends Thread{
        public void run(){
            while(true){
                try {
                    DatagramSocket c;
                    c = new DatagramSocket();
                    c.setBroadcast(true);
                    //Open a random port to send the package
                    byte[] sendData = ("IP:"+myIp).getBytes();

                    //Try the 255.255.255.255 first
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 7030);
                    c.send(sendPacket);
                    System.out.println(getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");

                    this.sleep(2000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}