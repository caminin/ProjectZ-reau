/**
 * Created by caminin on 30/01/17.
 * Le principe est le suivant :
 * Si une classe
 */
public class Log {

    /**
     * N'affiche que si DEBUG est à vrai
     * Le debug est défini dans la classe qui appelle Log, afin d'avoir un meilleur contrôle par classe
     * @param message
     */
    public static void debug(String message, boolean DEBUG){
        if(DEBUG){
            System.out.println(message);
        }
    }

}
