import sun.rmi.runtime.Log;

import java.math.BigInteger;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by caminin on 18/01/17.
 */
public class Security {

    private BigInteger number_m=null;
    private BigInteger number_e=null;

    private BigInteger number_n=null;
    private BigInteger number_u=null;

    private Logger monLog;

    public Security() {
        initiateLog();
    }

    public void initiateLog(){
        // création d'un Logger et d'un Handler
        Logger monLog = Logger.getLogger(Security.class.getName());
        monLog.setLevel(Level.ALL); //pour envoyer les messages de tous les niveaux
        monLog.setUseParentHandlers(false); // pour supprimer la console par défaut
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.INFO); // pour n'accepter que les message de niveau &Ge; INFO
        monLog.addHandler(ch);
    }

    void testGenPublicKey() {
        number_m=null;
        number_e=null;
    }

    void genPublicKey(){
        BigInteger number_p=BigInteger.probablePrime(700,new Random());
        BigInteger number_p_reduce=number_p.subtract(BigInteger.ONE);
        System.out.println("first number found");

        BigInteger number_q=BigInteger.probablePrime(700,new Random());
        BigInteger number_q_reduce=number_q.subtract(BigInteger.ONE);
        System.out.println("second number found");

        number_n=number_p.multiply(number_q);
        number_m=number_p_reduce.multiply(number_q_reduce);


        do {
            number_e=BigInteger.probablePrime(5,new Random());
        }
        while(!number_m.gcd(number_e).equals(BigInteger.ONE));

        System.out.println("firsnumber : "+number_p.toString()+"\nsecond number : "+number_q.toString()+"\nn key: "+number_n.toString()+"\n m key : "+number_m+"\n e : "+number_e);
    }

    public void genPrivateKey(){
        if(number_m ==null || number_e == null){
            monLog.log(Level.ALL,"la clé privée doit être générée après la clé publique");
        }
        else{
            BigInteger number_r_0=number_e;
            BigInteger number_r_1=number_m;
            BigInteger new_number_r_1;
            BigInteger temp_number_r_1;

            BigInteger number_u_0=BigInteger.ONE;
            BigInteger number_u_1=BigInteger.ZERO;
            BigInteger new_number_u_1;
            BigInteger temp_number_u_1;
            do{
                temp_number_r_1=number_r_1;//on save le next r
                temp_number_u_1=number_u_1;

                new_number_r_1=number_r_0.subtract(number_r_0.divide(number_r_1)).multiply(number_r_1);
                new_number_u_1=number_u_0.subtract(number_r_0.divide(number_r_1)).multiply(number_u_1);

                number_r_0=temp_number_r_1;
                number_u_0=temp_number_u_1;
                number_r_1=new_number_r_1;
                number_u_1=new_number_u_1;

                System.out.println(number_r_1.bitLength());
            }
            while(!number_r_1.equals(BigInteger.ZERO));

            number_u=number_u_0;

            System.out.println("number_u : "+number_u+"\nnumber_n : "+number_n);

        }

    }

    public static void main(String[] args) {
        Security se=new Security();
        //se.genPublicKey();
        se.testGenPublicKey();
        se.genPrivateKey();
    }
}
