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

    public String decryption(BigInteger[] message){
        String out = "";
        for(int i = 0; i < message.length; i++){
            Log.debug(String.valueOf(Character.toChars(message[i].pow(u.intValue()).mod(n).intValue())),DEBUG);
           out += Character.toChars(message[i].pow(u.intValue()).mod(n).intValue()).toString();
        }
        return out;
    }


}
