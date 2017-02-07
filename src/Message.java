import java.net.*;

/**
 * Created by caminin on 07/02/17.
 */
public class Message {
    private int port= 7000;
    private int port2= 7001;
    private boolean debug=true;
    private DatagramSocket mDataGramSocket=null;

    public Message() {
        try {
            mDataGramSocket = new DatagramSocket(null);
            mDataGramSocket.setBroadcast(true);
            mDataGramSocket.bind(new InetSocketAddress(port));
        } catch (SocketException e) {
            Log.debug("Le port est déjà pris",debug);
            port++;
            port2--;
            try {
                mDataGramSocket = new DatagramSocket(null);
                mDataGramSocket.setBroadcast(true);
                mDataGramSocket.bind(new InetSocketAddress(port));
            } catch (SocketException e1) {
                Log.debug("Plus de ports de libre",debug);
            }
        }
    }

    public void send(String message){
        SendThread send=new SendThread(message);
        send.start();
    }

    public void receive(Secured_Client client){
        ReceiveThread receive=new ReceiveThread(client);
        receive.start();
    }

    public class SendThread extends Thread{
        private String message;
        public SendThread(String message){
            this.message=message;

        }
        public void run(){
            try {
                InetAddress local = null;
                local = InetAddress.getByName("localhost");
                int msg_length = message.length();
                byte[] message_byte = message.getBytes();
                DatagramPacket p = new DatagramPacket(message_byte, msg_length, local, port2);
                mDataGramSocket.send(p);
                Log.debug("UDP envoyé",debug);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ReceiveThread extends Thread {
        private Secured_Client client;


        public ReceiveThread(Secured_Client client) {
            this.client = client;
        }

        public void run() {
            while (true) {
                String text;

                byte[] message = new byte[1500];
                DatagramPacket p = new DatagramPacket(message, message.length);
                try {
                    while (true) {  // && counter < 100 TODO
                        try {
                            mDataGramSocket.receive(p);
                            Log.debug("UDP reçu",debug);
                            text = new String(message, 0, p.getLength());
                            client.handleMessage(text+"\n");
                        } catch (SocketTimeoutException | NullPointerException e) {
                            e.printStackTrace();
                            Thread.sleep(5000);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // return "error:" + e.getMessage();
                }
            }
        }
    }
}