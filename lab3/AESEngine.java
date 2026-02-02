
public class AESEngine {
    private static final int[] sbox = {
        0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
        0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
        0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
        0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
        0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
        0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
        0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
        0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
        0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
        0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
        0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
        0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
        0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
        0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
        0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
        0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16
    };

    private static final int[] rsbox = new int[256];
    static { for (int i = 0; i < 256; i++) rsbox[sbox[i]] = i; }

    private static final int[] Rcon = {
        0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36
    };

    public static byte[] encrypt(byte[] input, byte[] key) {
        byte[][] state = toState(input);
        byte[][] roundKeys = expandKey(key);

        addRoundKey(state, getRoundKey(roundKeys, 0));

        for (int i = 1; i < 10; i++) {
            subBytes(state);
            shiftRows(state);
            mixColumns(state);
            addRoundKey(state, getRoundKey(roundKeys, i));
        }

        subBytes(state);
        shiftRows(state);
        addRoundKey(state, getRoundKey(roundKeys, 10));

        return fromState(state);
    }

    public static byte[] decrypt(byte[] input, byte[] key) {
        byte[][] state = toState(input);
        byte[][] roundKeys = expandKey(key);

        addRoundKey(state, getRoundKey(roundKeys, 10));

        for (int i = 9; i > 0; i--) {
            invShiftRows(state);
            invSubBytes(state);
            addRoundKey(state, getRoundKey(roundKeys, i));
            invMixColumns(state);
        }

        invShiftRows(state);
        invSubBytes(state);
        addRoundKey(state, getRoundKey(roundKeys, 0));

        return fromState(state);
    }

    private static void subBytes(byte[][] s) {
        for (int i=0; i<4; i++) for (int j=0; j<4; j++) s[i][j] = (byte) sbox[s[i][j] & 0xFF];
    }

    private static void invSubBytes(byte[][] s) {
        for (int i=0; i<4; i++) for (int j=0; j<4; j++) s[i][j] = (byte) rsbox[s[i][j] & 0xFF];
    }

    private static void shiftRows(byte[][] s) {
        byte[] temp = new byte[4];
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 4; j++) temp[j] = s[i][(j + i) % 4];
            System.arraycopy(temp, 0, s[i], 0, 4);
        }
    }

    private static void invShiftRows(byte[][] s) {
        byte[] temp = new byte[4];
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 4; j++) temp[(j + i) % 4] = s[i][j];
            System.arraycopy(temp, 0, s[i], 0, 4);
        }
    }

    private static void mixColumns(byte[][] s) {
        for (int j = 0; j < 4; j++) {
            byte a = s[0][j], b = s[1][j], c = s[2][j], d = s[3][j];
            s[0][j] = (byte) (mul(2, a) ^ mul(3, b) ^ c ^ d);
            s[1][j] = (byte) (a ^ mul(2, b) ^ mul(3, c) ^ d);
            s[2][j] = (byte) (a ^ b ^ mul(2, c) ^ mul(3, d));
            s[3][j] = (byte) (mul(3, a) ^ b ^ c ^ mul(2, d));
        }
    }

    private static void invMixColumns(byte[][] s) {
        for (int j = 0; j < 4; j++) {
            byte a = s[0][j], b = s[1][j], c = s[2][j], d = s[3][j];
            s[0][j] = (byte) (mul(14, a) ^ mul(11, b) ^ mul(13, c) ^ mul(9, d));
            s[1][j] = (byte) (mul(9, a) ^ mul(14, b) ^ mul(11, c) ^ mul(13, d));
            s[2][j] = (byte) (mul(13, a) ^ mul(9, b) ^ mul(14, c) ^ mul(11, d));
            s[3][j] = (byte) (mul(11, a) ^ mul(13, b) ^ mul(9, c) ^ mul(14, d));
        }
    }

    private static void addRoundKey(byte[][] s, byte[][] key) {
        for (int i=0; i<4; i++) for (int j=0; j<4; j++) s[i][j] ^= key[i][j];
    }

    private static byte mul(int c, byte b) {
        int res = 0, a = b & 0xFF;
        for (int i = 0; i < 8; i++) {
            if ((c & 1) != 0) res ^= a;
            boolean highBit = (a & 0x80) != 0;
            a <<= 1;
            if (highBit) a ^= 0x1b;
            c >>= 1;
        }
        return (byte) (res & 0xFF);
    }

    private static byte[][] expandKey(byte[] key) {
        byte[][] w = new byte[44][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) w[i][j] = key[4 * i + j];
        }
        for (int i = 4; i < 44; i++) {
            byte[] temp = w[i - 1].clone();
            if (i % 4 == 0) {
                // RotWord
                byte t = temp[0];
                System.arraycopy(temp, 1, temp, 0, 3);
                temp[3] = t;
                // SubWord
                for (int j=0; j<4; j++) temp[j] = (byte) sbox[temp[j] & 0xFF];
                // XOR Rcon
                temp[0] ^= Rcon[i / 4];
            }
            for (int j = 0; j < 4; j++) w[i][j] = (byte) (w[i - 4][j] ^ temp[j]);
        }
        return w;
    }

    private static byte[][] getRoundKey(byte[][] w, int round) {
        byte[][] rk = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) rk[j][i] = w[round * 4 + i][j];
        }
        return rk;
    }

    private static byte[][] toState(byte[] input) {
        byte[][] s = new byte[4][4];
        for (int i=0; i<16; i++) s[i % 4][i / 4] = input[i];
        return s;
    }

    private static byte[] fromState(byte[][] s) {
        byte[] b = new byte[16];
        for (int i=0; i<16; i++) b[i] = s[i % 4][i / 4];
        return b;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        return bytes;
    }

    public static byte[] pad(String s) {
        byte[] b = s.getBytes();
        byte[] padded = new byte[16];
        System.arraycopy(b, 0, padded, 0, Math.min(b.length, 16));
        return padded;
    }
}