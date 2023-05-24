package org.example.model.sms;

public class SmsUtils {
    public static byte[] toPrimitive(Byte[] bytes) {
        byte[] primitive = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            primitive[i] = bytes[i];
        }
        return primitive;
    }

    public static Byte[] toObject(byte[] bytes) {
        Byte[] object = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            object[i] = bytes[i];
        }
        return object;
    }

    public static String getStringFromBytes(byte[] receiverPhoneBytes) {
        StringBuilder text = new StringBuilder(receiverPhoneBytes.length);
        for (byte receiverPhoneByte : receiverPhoneBytes) {
            char c = parseByteDigitToChar(receiverPhoneByte);
            text.append(c);
        }
        return text.toString();
    }

    public static byte parseCharDigitToByte(char c) {
        if (c == '1') return 1;
        if (c == '2') return 2;
        if (c == '3') return 3;
        if (c == '4') return 4;
        if (c == '5') return 5;
        if (c == '6') return 6;
        if (c == '7') return 7;
        if (c == '8') return 8;
        if (c == '9') return 9;
        if (c == 'F') return 15;
        return 0;
    }

    public static char parseByteDigitToChar(byte b) {
        if (b == 1) return '1';
        if (b == 2) return '2';
        if (b == 3) return '3';
        if (b == 4) return '4';
        if (b == 5) return '5';
        if (b == 6) return '6';
        if (b == 7) return '7';
        if (b == 8) return '8';
        if (b == 9) return '9';
        if (b == -1) return 'F';
        return '0';
    }
}
