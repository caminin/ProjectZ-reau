import java.math.BigInteger;

/**
 * Created by alex on 26/01/17.
 */
public class PrivateKey extends Key {
    public static boolean DEBUG = true;
    private BigInteger u;

    public PrivateKey(BigInteger n, BigInteger u) {
        super(n);
        this.u = u;
    }

    public String decryption(BigInteger[] message) {
        String out = new String("");
        for (int i = 0; i < message.length; i++) {
            Log.debug(String.valueOf(Character.toChars(message[i].modPow(u, n).intValue())), DEBUG);
            out += String.valueOf(Character.toChars(message[i].modPow(u, n).intValue()));
        }
        System.out.println(out);
        return out;
    }

    /**
     * Prend un string contenant un tableau de biuginteger et le transforme en tableau de biginteger
     * @param bigIntegerArray le tableau mais en string
     * @return le tableau de BigInteger
     */
    public static BigInteger[] splitString(String bigIntegerArray){
        String stringArray[]=bigIntegerArray.split("/") ;
        BigInteger res[]=new BigInteger[stringArray.length];
        for(int i=0;i<res.length;i++){
            res[i]=new BigInteger(stringArray[i].trim());
        }
        return res;
    }


    public static boolean isEncrypted(String bigIntegerArray){
        String stringArray[]=bigIntegerArray.split("/") ;
        BigInteger res;
        boolean isEncrypted=true;
        for(int i=0;i<stringArray.length && i<2;i++){
            try{
                res=new BigInteger(stringArray[i].trim());
            }
            catch (Exception e){
                isEncrypted=false;
                Log.debug("le message n'est pas crypté",DEBUG);
            }
        }
        return isEncrypted;
    }


}
