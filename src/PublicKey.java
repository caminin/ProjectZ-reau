import java.math.BigInteger;

/**
 * Created by alex on 26/01/17.
 */
public class PublicKey extends Key {
    public PublicKey(BigInteger n1, BigInteger n2) {
        super(n1, n2);
    }

    public BigInteger[] encryption(String message){
        byte[] in = message.getBytes();
        BigInteger[] out = new BigInteger[in.length];
        byte[] temp = new byte[1];
        for(int i = 0; i < in.length; i++){
            temp[0] = in[i];
            out[i] = new BigInteger(temp).pow(n2.intValue()).mod(n1);
        }
//        for(int i = 0; i < out.length; i++) {
//            System.out.print(out[i]+" ");
//        }
        return out;
    }

    public static void main(String[] args) {
        PublicKey pkey = new PublicKey(BigInteger.valueOf(5141), BigInteger.valueOf(7));
        pkey.encryption("bonjour");
    }
}
