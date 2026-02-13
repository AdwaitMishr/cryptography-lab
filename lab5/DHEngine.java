import java.math.BigInteger;

public class DHEngine {
    public static BigInteger p = new BigInteger("23"); 
    public static BigInteger g = new BigInteger("5");

    public static String xorCipher(String data, BigInteger key) {
        StringBuilder result = new StringBuilder();
        String k = key.toString();
        for (int i = 0; i < data.length(); i++) {
            result.append((char) (data.charAt(i) ^ k.charAt(i % k.length())));
        }
        return result.toString();
    }
}