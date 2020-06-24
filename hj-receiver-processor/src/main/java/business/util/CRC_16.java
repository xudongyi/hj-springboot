package business.util;

public class CRC_16 {
    public CRC_16() {
    }

    public static String CRC16(String data) {
        byte[] temp = data.getBytes();
        int por = 65535;

        for(int j = 0; j < temp.length; ++j) {
            por >>= 8;
            por ^= temp[j];

            for(int i = 0; i < 8; ++i) {
                if ((por & 1) == 1) {
                    por >>= 1;
                    por ^= 40961;
                } else {
                    por >>= 1;
                }
            }
        }

        return String.format("%04X", por);
    }
}