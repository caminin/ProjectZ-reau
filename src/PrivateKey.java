import java.math.BigInteger;

/**
 * Created by alex on 26/01/17.
 */
public class PrivateKey extends Key {
    public PrivateKey(BigInteger n1, BigInteger n2) {
        super(n1, n2);
    }

    public String decryption(String message){

        return message;
    }


}
