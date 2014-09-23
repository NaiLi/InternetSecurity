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
            p = probablePrime(2048, rnd);
            q = probablePrime(2048, rnd);
            e = probablePrime(2048, rnd);
            n = p.multiply(q);
            d = e.modInverse((p.subtract(one)).multiply(q.subtract(one)));
        }

        public BigInteger encrypt(String originalMessage) {

            BigInteger m = new BigInteger(originalMessage.getBytes());
            return m.modPow(d, n);
        }

        public String decrypt(BigInteger c) {

            BigInteger decryptNumber = c.modPow(e, n);
            return new String(decryptNumber.toByteArray());
        }
    }
}
