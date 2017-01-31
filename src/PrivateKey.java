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
}
