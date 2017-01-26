import java.math.BigInteger;

/**
 * Created by alex on 26/01/17.
 */
public class PublicKey extends Key {
    public PublicKey(BigInteger n1, BigInteger n2) {
        super(n1, n2);
    }

    public String encryption(String message){
        byte[] in = message.getBytes();
        byte[] out = new byte[in.length];
        for(int i =0; i < in.length; i++){
            out[i] = new BigInteger(in).pow(n2.intValue()).mod(n1).byteValueExact();
        }
        for(int i = 0; i < out.length; i++) {
            System.out.print(out[i]+" ");
        }
        return message;
    }

    public static void main(String[] args) {
        PublicKey pkey = new PublicKey(BigInteger.valueOf(5141), BigInteger.valueOf(7));
        pkey.encryption("bonjour");
    }



}
