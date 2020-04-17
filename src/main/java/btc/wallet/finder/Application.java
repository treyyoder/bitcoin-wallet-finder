package btc.wallet.finder;

import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;

public class Application {

  public static void main(String[] args) {

    long count = 0;
    while (true) {
      try {
        Security.addProvider(new BouncyCastleProvider());

        KeyPair pair = generateKeys();
        String privateKey = getPrivateKey(pair);
        String publicKey = getPublicKey(pair);

        String json = new JSONObject(
            IOUtils.toString(new URL("https://blockchain.info/balance?active=" + publicKey),
                Charset.forName("UTF-8"))).toString();

        if (!json.contains("{\"final_balance\":0,\"n_tx\":0,\"total_received\":0}")) { // Good luck!
          System.out.println("Private Key: " + privateKey);
          System.out.println("Public Key: " + publicKey);
          System.out.println(json);
        }
        // Trying not get busted by blockchain.info
        Thread.sleep(500);
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.println(++count);
    }
  }

  public static KeyPair generateKeys() throws Exception {
    KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
    g.initialize(new ECGenParameterSpec("secp256k1"));
    return g.generateKeyPair();
  }

  private static String adjustTo64(String kp) {
    switch (kp.length()) {
      case 62:
        return "00" + kp;
      case 63:
        return "0" + kp;
      case 64:
        return kp;
      default:
        throw new IllegalArgumentException("Invalid Key Found: " + kp);
    }
  }

  public static String getPrivateKey(KeyPair pair) {
    return adjustTo64(((ECPrivateKey) pair.getPrivate()).getS().toString(16));
  }

  public static String getPublicKey(KeyPair pair) throws Exception {
    ECPublicKey ecpub = (ECPublicKey) pair.getPublic();
    ECPoint ppt = ecpub.getW();
    String sx = adjustTo64(ppt.getAffineX().toString(16));
    String sy = adjustTo64(ppt.getAffineY().toString(16));
    String bcPub = "04" + sx + sy;

    byte[] bcPubBA = new byte[bcPub.length() / 2];
    for (int i = 0; i < bcPub.length() / 2; i += 2)
      bcPubBA[i / 2] = (byte) ((Character.digit(bcPub.charAt(i), 16) << 4)
          + Character.digit(bcPub.charAt(i + 1), 16));

    MessageDigest sha = MessageDigest.getInstance("SHA-256");
    byte[] s1 = sha.digest(bcPubBA);

    MessageDigest rmd = MessageDigest.getInstance("RipeMD160", "BC");
    byte[] r1 = rmd.digest(s1);

    byte[] r2 = new byte[r1.length + 1];
    r2[0] = 0;
    for (int i = 0; i < r1.length; i++)
      r2[i + 1] = r1[i];

    byte[] s2 = sha.digest(r2);
    byte[] s3 = sha.digest(s2);

    byte[] a1 = new byte[25];
    for (int i = 0; i < r2.length; i++)
      a1[i] = r2[i];
    for (int i = 0; i < 4; i++)
      a1[21 + i] = s3[i];

    return Base58.encode(a1);
  }
}
