import java.util.*;

public class CipherLogic {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // --- 1. CAESAR CIPHER ---
    public static String caesar(String text, int key, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        int shift = encrypt ? key : 26 - (key % 26);
        for (char c : text.toUpperCase().toCharArray()) {
            if (Character.isLetter(c)) {
                result.append((char) ('A' + (c - 'A' + shift) % 26));
            } else result.append(c);
        }
        return result.toString();
    }

    // --- 2. MONOALPHABETIC CIPHER ---
    public static String monoalphabetic(String text, String keyMap, boolean encrypt) {
        String plain = ALPHABET;
        String cipher = keyMap.toUpperCase();
        StringBuilder result = new StringBuilder();
        for (char c : text.toUpperCase().toCharArray()) {
            if (Character.isLetter(c)) {
                if (encrypt) result.append(cipher.charAt(plain.indexOf(c)));
                else result.append(plain.charAt(cipher.indexOf(c)));
            } else result.append(c);
        }
        return result.toString();
    }

    // --- 3. PLAYFAIR CIPHER ---
    public static String playfair(String text, String key, boolean encrypt) {
        char[][] matrix = generatePlayfairMatrix(key);
        String prepared = preparePlayfairText(text, encrypt);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < prepared.length(); i += 2) {
            char a = prepared.charAt(i);
            char b = prepared.charAt(i + 1);
            int[] posA = findPos(matrix, a);
            int[] posB = findPos(matrix, b);
            if (posA[0] == posB[0]) { // Row
                result.append(matrix[posA[0]][(posA[1] + (encrypt ? 1 : 4)) % 5]);
                result.append(matrix[posB[0]][(posB[1] + (encrypt ? 1 : 4)) % 5]);
            } else if (posA[1] == posB[1]) { // Col
                result.append(matrix[(posA[0] + (encrypt ? 1 : 4)) % 5][posA[1]]);
                result.append(matrix[(posB[0] + (encrypt ? 1 : 4)) % 5][posB[1]]);
            } else { // Rect
                result.append(matrix[posA[0]][posB[1]]);
                result.append(matrix[posB[0]][posA[1]]);
            }
        }
        return result.toString();
    }

    // --- 4. HILL CIPHER ---
    public static String hill(String text, String key, boolean encrypt) {
        text = text.toUpperCase().replaceAll("[^A-Z]", "");
        if (text.length() % 2 != 0) text += "X";
        int[][] keyMatrix = new int[2][2];
        int k = 0;
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                keyMatrix[i][j] = key.charAt(k++) - 'A';

        if (!encrypt) keyMatrix = invertHillMatrix(keyMatrix);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i += 2) {
            int p1 = text.charAt(i) - 'A';
            int p2 = text.charAt(i+1) - 'A';
            int c1 = (keyMatrix[0][0] * p1 + keyMatrix[0][1] * p2) % 26;
            int c2 = (keyMatrix[1][0] * p1 + keyMatrix[1][1] * p2) % 26;
            result.append((char)(c1 + 'A')).append((char)(c2 + 'A'));
        }
        return result.toString();
    }

    // --- 5. VIGENERE & 6. OTP ---
    public static String vigenere(String text, String key, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        text = text.toUpperCase();
        key = key.toUpperCase();
        int j = 0;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                int p = c - 'A';
                int k = key.charAt(j % key.length()) - 'A';
                int val = encrypt ? (p + k) % 26 : (p - k + 26) % 26;
                result.append((char) ('A' + val));
                j++;
            } else result.append(c);
        }
        return result.toString();
    }

    // --- 7. RAIL FENCE ---
    public static String railFence(String text, int rails, boolean encrypt) {
        if (encrypt) {
            char[][] fence = new char[rails][text.length()];
            for (int i = 0; i < rails; i++) Arrays.fill(fence[i], '\n');
            boolean down = false; int row = 0;
            for (int i=0; i<text.length(); i++) {
                if (row == 0 || row == rails - 1) down = !down;
                fence[row][i] = text.charAt(i);
                row += down ? 1 : -1;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rails; i++)
                for (int j = 0; j < text.length(); j++)
                    if (fence[i][j] != '\n') sb.append(fence[i][j]);
            return sb.toString();
        } else {
            // Decrypt Logic
            char[][] fence = new char[rails][text.length()];
            for (int i = 0; i < rails; i++) Arrays.fill(fence[i], '\n');
            boolean down = false; int row = 0;
            for (int i = 0; i < text.length(); i++) {
                if (row == 0 || row == rails - 1) down = !down;
                fence[row][i] = '*';
                row += down ? 1 : -1;
            }
            int index = 0;
            for (int i = 0; i < rails; i++)
                for (int j = 0; j < text.length(); j++)
                    if (fence[i][j] == '*' && index < text.length())
                        fence[i][j] = text.charAt(index++);
            StringBuilder sb = new StringBuilder();
            down = false; row = 0;
            for (int i = 0; i < text.length(); i++) {
                if (row == 0 || row == rails - 1) down = !down;
                sb.append(fence[row][i]);
                row += down ? 1 : -1;
            }
            return sb.toString();
        }
    }

    // -- Helpers for Hill/Playfair --
    private static char[][] generatePlayfairMatrix(String key) {
        char[][] matrix = new char[5][5];
        String keyString = key.toUpperCase().replaceAll("[^A-Z]", "").replace("J", "I") + "ABCDEFGHIKLMNOPQRSTUVWXYZ";
        Set<Character> used = new HashSet<>();
        int x = 0, y = 0;
        for (char c : keyString.toCharArray()) {
            if (!used.contains(c)) {
                matrix[x][y++] = c; used.add(c);
                if (y == 5) { y = 0; x++; }
            }
        }
        return matrix;
    }
    private static String preparePlayfairText(String text, boolean encrypt) {
        text = text.toUpperCase().replaceAll("[^A-Z]", "").replace("J", "I");
        if (!encrypt) return text;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (i + 1 < text.length() && text.charAt(i + 1) == c) sb.append(c).append('X');
            else sb.append(c);
        }
        if (sb.length() % 2 != 0) sb.append('X');
        return sb.toString();
    }
    private static int[] findPos(char[][] matrix, char c) {
        for (int i = 0; i < 5; i++) for (int j = 0; j < 5; j++) if (matrix[i][j] == c) return new int[]{i, j};
        return null;
    }
    private static int[][] invertHillMatrix(int[][] matrix) {
        int det = (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]) % 26;
        if (det < 0) det += 26;
        int detInv = -1;
        for (int i = 1; i < 26; i++) if ((det * i) % 26 == 1) { detInv = i; break; }
        int[][] inv = new int[2][2];
        inv[0][0] = (matrix[1][1] * detInv) % 26; inv[0][1] = (-matrix[0][1] * detInv) % 26;
        inv[1][0] = (-matrix[1][0] * detInv) % 26; inv[1][1] = (matrix[0][0] * detInv) % 26;
        for(int i=0; i<2; i++) for(int j=0; j<2; j++) if(inv[i][j] < 0) inv[i][j] += 26;
        return inv;
    }
}