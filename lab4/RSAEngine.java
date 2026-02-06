import java.math.BigInteger;

public class RSAEngine {
    public static BigInteger gcd(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) return a;
        return gcd(b, a.mod(b));
    }

    public static BigInteger encrypt(BigInteger message, BigInteger e, BigInteger n) {
        return message.modPow(e, n);
    }

    public static BigInteger decrypt(BigInteger ciphertext, BigInteger d, BigInteger n) {
        return ciphertext.modPow(d, n);
    }
}