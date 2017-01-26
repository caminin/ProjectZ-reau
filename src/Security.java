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
    private static boolean DEBUG=false;

    private BigInteger number_m=null;
    private BigInteger number_e=null;

    private BigInteger number_n=null;
    private BigInteger number_u=null;


    /**
     * N'affiche que si DEBUG est à vrai
     * @param message
     */
    private void debug(String message){
        if(DEBUG){
            System.out.println(message);
        }
    }

    public void genKeys(){
        genPublicKey();
        genPrivateKey();
    }

    /**
     * Permet de lancer les tests pour vérifier les algo
     */
    public void genTestKeys(){
        DEBUG=true;
        testGenPublicKey();
        genPrivateKey();
        checkTestGenKeys();
    }

    /**
     * Permet de tester avec les valeurs du prof
     * Cela doit toujours être utilisé avec checkTestGenKeys
     */
    void testGenPublicKey() {
        number_n=BigInteger.valueOf(5141);
        number_m=BigInteger.valueOf(4992);
        number_e=BigInteger.valueOf(7);
    }

    /**
     * Permet de vérifier les valeurs après avoir générer les clés avec les valeurs du prof
     */
    void checkTestGenKeys(){
        if(number_u.compareTo(BigInteger.valueOf(4279))==0){
            debug("La clé est bonne");
        }
        else{
            debug("La clé n'est pas bonne");
        }
    }


    /**
     * Permet de générer la clé publique
     */
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

        debug("firsnumber : "+number_p.toString()+"\nsecond number : "+number_q.toString()+"\nn key: "+number_n.toString()+"\n m key : "+number_m+"\n e : "+number_e);
    }

    /**
     * Permet de générer la clé privée en fonction de la clé publique.
     * Il ne doit donc être lancé qu'après le genPublicKey
     */
    public void genPrivateKey(){
        // Si jamais ces valeurs sont null, alors la clé publique n'a pas été générée donc pas de possibilités de faire la clé privée
        if(number_m ==null || number_e == null){
            debug("la clé privée doit être générée après la clé publique");
        }
        else{
            //On utilise la fonction définie par le prof,
            /*
            r0= e
            r1=m
            ri+1=ri-1-(ri-1/ri)*ri

            u0=1
            u1=0
            ui+1=ui-1-(ri-1/ri)*ui

            On s'arrête quand ri=0 et on prend ui-1
             */
            BigInteger number_r_0=number_e;//pour initialiser
            BigInteger number_r_1=number_m;//pour initialiser
            BigInteger new_number_r_1;//Le nouveau nombre
            BigInteger temp_number_r_1;//un conteneur pour échanger les nombres

            BigInteger number_u_0=BigInteger.ONE;//pour initialiser
            BigInteger number_u_1=BigInteger.ZERO;//pour initialiser
            BigInteger new_number_u_1;//Le nouveau nombre
            BigInteger temp_number_u_1;//un conteneur pour échanger les nombres

            //Début du calcul, fait un tour avanr de vérifier
            do{
                debug("R0 : "+number_r_0.toString()+"|R1 : "+number_r_1.toString()+"|u0 : "+number_u_0.toString()+"|U1 : "+number_u_1.toString());
                temp_number_r_1=number_r_1;//on garde ri dans l'algo de coté
                temp_number_u_1=number_u_1;

                //le calcul en lui-même
                new_number_r_1=number_r_0.subtract(number_r_0.divide(number_r_1).multiply(number_r_1));
                new_number_u_1=number_u_0.subtract(number_r_0.divide(number_r_1).multiply(number_u_1));

                //on change les valeurs des nombres pour passer à la phase suivante
                number_r_0=temp_number_r_1;
                number_u_0=temp_number_u_1;
                number_r_1=new_number_r_1;
                number_u_1=new_number_u_1;

            }
            while(!number_r_1.equals(BigInteger.ZERO));

            //On prend le nombre U qu'on va utiliser ensuite
            number_u=number_u_0;

            //now you need to find out if U is perfect or if we need to fix it
            // IF 2<u<m THEN u=u-k*m, k<0
            long i=1;
            BigInteger new_u;
            do{
                new_u=number_u;
                //Si U est supérieur à m alors on le réduit de m puis on continue
                if(new_u.compareTo(number_m)==1){
                    new_u=new_u.add(number_m.multiply(BigInteger.valueOf(i)));
                    i++;
                }
                //Si U est inférieur à 2 alors on l'augmente de m et on continue
                else if(new_u.compareTo(BigInteger.valueOf(2))==-1){
                    new_u=new_u.add(number_m.multiply(BigInteger.valueOf(i)));
                    i--;
                }
            }
            while(new_u.compareTo(BigInteger.valueOf(2))==-1 || new_u.compareTo(number_m)==1);

            //maintenant on sait que le nouveau U valide : 2<u<m donc on le donne

            number_u=new_u;

            debug("number_u : "+number_u+"\nnumber_n : "+number_n);
        }

    }

    public static void main(String[] args) {
        Security se=new Security();
        //se.genTestKeys();

        //Security.DEBUG=true;//utile si on veut debug la version gen aléatoire
        se.genKeys();
    }
}
