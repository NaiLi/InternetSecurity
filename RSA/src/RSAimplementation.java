import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Random;

import static java.math.BigInteger.probablePrime;

/**
 * Created by Linnéa on 2014-09-22.
 */
public class RSAimplementation {

    public static void main(String[] args) {

        System.out.println("Please, enter message:");

        // Start new session
        Session session = new Session();
        session.generateKeys();

        // Get input message
        String input = null;
        try {
            input = (new BufferedReader(new InputStreamReader(System.in))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Encrypt message
        BigInteger encryptedMessage = session.encrypt(input);

        // Decrypt message
        String decryptedMessage = session.decrypt(encryptedMessage);
        System.out.println("Decrypted message is: " + decryptedMessage);

    }

    protected static class Session {

        public Session(){}

        BigInteger e, d, n, p, q;
        Random rnd = new Random();

        //Generate public and private keys
        public void generateKeys() {
            BigInteger one = new BigInteger(String.valueOf(1));
            BigInteger big = new BigInteger("14193271253374151972958469534510288556627600194327889185878254140945207034309646900712652187981588847065261417485607509238580017513218694713127236093041786064730865944174478451080703918517796282551137776591691299133320298558888954350895488253763125863765897072964407474976920035810975736766563780127516846454368111473100255624663443431342692737816993681136324614448777966063404104845778367601496346067902499952034623206730283355322862330281534867578000433833659447746102227666343864531849978320607667643323280798462394574718620671242962445571372095099692139038681406352897179305953077690421071495629851244515267477329");
            e = new BigInteger("65537");//probablePrime(2048, rnd);

            while(true) {
                p = probablePrime(1024, rnd);
                q = probablePrime(1024, rnd);
                n = p.multiply(q);
                if (n == big) {
                    System.out.println(p + "            " + q);
                    d = e.modInverse((p.subtract(one)).multiply(q.subtract(one)));
                    break;
                }
            }
        }

        public BigInteger encrypt(String originalMessage) {

            BigInteger m = new BigInteger(originalMessage.getBytes());
            return m.modPow(d, n);
        }

        public String decrypt(BigInteger c) {
            c = new BigInteger("9391223845601135193359379922961000465684229889173902322846594012488893009034611926372567284753273996888801289091213189400304697230794293723907570205614658499715368402310356703485028842534867216267959847784489949547568681050711745205885312580766723023580754578574027018520028174181555135052274443874516142556298377320392813888869140304130350931712010686572475855384066110598971782437611309513129208245556754433056047950053388334249013523822399272634575049404087852938782586340422753694099875774251457324190291775285737958565712747212351192477540515908377034691090531902743556205771320567093801907793038972818998278127");
            BigInteger decryptNumber = c.modPow(e, n);
            return new String(decryptNumber.toByteArray());
        }
    }
}
