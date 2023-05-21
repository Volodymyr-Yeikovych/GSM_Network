package org.example.enc;

public class SmsEncryptionManager {
    public static String decrypt(String encryptedMessage) {

        return encryptedMessage;
    }

    public static String encrypt(String message, String senderNum, String receiverNum) {
        int smsC = 0x00;
        int smsSubmit = 0x11;
        int tpMR = 0x00;
        int tpDA = receiverNum.length();
        int typeOfAddress = 0x91;
        String phNum = getEncReadyPhoneNum(receiverNum);
        int tpPID = 0x00;
        int tpDCS = 0x00;
        int tpVP = 0x00;
        int tpUDL = message.length();

        return message;
    }

    private static String getEncReadyPhoneNum(String receiverNum) {
        return receiverNum.length() % 2 == 1 ? receiverNum += "F" : receiverNum;
    }
}
