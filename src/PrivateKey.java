import java.math.BigInteger;

/**
 * Created by alex on 26/01/17.
 */
public class PrivateKey extends Key {
    public PrivateKey(BigInteger n1, BigInteger n2) {
        super(n1, n2);
    }

    public String encryption(BigInteger[] message){
        String out = "";
        byte[] temp = new byte[1];
        for(int i = 0; i < message.length; i++){
           out += new BigInteger(temp).pow(n2.intValue()).mod(n1).toString();
        }
//        for(int i = 0; i < out.length; i++) {
//            System.out.print(out[i]+" ");
//        }
        return out;
    }


}
