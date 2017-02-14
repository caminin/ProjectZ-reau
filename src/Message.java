import java.net.*;

/**
 * Created by caminin on 07/02/17.
 */
public class Message {
    private static boolean debug=false;
    private static boolean release=true;
    private int port_reception = 7000;
    private int port_envoi= 7000;
    private DatagramSocket mDataGramSocket=null;
    private String ip;

    public Message(String ip) {
        this.ip=ip;
        try {
            mDataGramSocket = new DatagramSocket(null);
            mDataGramSocket.setBroadcast(true);
            mDataGramSocket.bind(new InetSocketAddress(port_reception));
            if(ip.equals("localhost")){//Si on est en local, alors le port d'envoi n'est pas le même que le port de réception
                port_envoi++;
            }
        } catch (SocketException e) {
            Log.debug("Le port_reception est déjà pris",debug);
            port_reception++;
            //on bouge pas port envoi
            try {
                mDataGramSocket = new DatagramSocket(null);
                mDataGramSocket.setBroadcast(true);
                mDataGramSocket.bind(new InetSocketAddress(port_reception));
            } catch (SocketException e1) {
                Log.debug("Plus de ports de libre",debug);
            }
        }

        Log.debug("J'envoie à "+ip+ " au port "+port_envoi+ " et je reçois au port "+ port_reception,release);
    }

    public void send(String message){
        SendThread send=new SendThread(message,ip);
        send.start();
    }

    public void receive(Secured_Client client){
        ReceiveThread receive=new ReceiveThread(client);
        receive.start();
    }

    public class SendThread extends Thread{
        private String message;
        private String ip;
        public SendThread(String message,String ip){
            this.message=message;
            this.ip=ip;

        }
        public void run(){
            try {
                InetAddress local = null;
                local = InetAddress.getByName(ip);
                int msg_length = message.length();
                byte[] message_byte = message.getBytes();
                DatagramPacket p = new DatagramPacket(message_byte, msg_length, local, port_envoi);
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

                byte[] message = new byte[100000];
                DatagramPacket p = new DatagramPacket(message, message.length);
                try {
                    while (true) {
                        try {
                            mDataGramSocket.receive(p);
                            Log.debug("UDP reçu",debug);
                            text = new String(message, 0, p.getLength());
                            client.handleMessage(text);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.debug("socket fermé",release);
                            Thread.sleep(10000);
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