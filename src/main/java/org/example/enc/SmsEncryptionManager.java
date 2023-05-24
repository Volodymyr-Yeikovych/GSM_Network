package org.example.enc;

import org.example.exception.InvalidGsmMessageFormatException;

import java.util.ArrayList;
import java.util.List;

public class SmsEncryptionManager {
//    public synchronized static void decrypt(Message message) {
//        String smsDeliveryMsg = message.getMessage();
//        String receiverPhone = message.getReceiverPhone();
//        String senderPhone = message.getSenderPhone();
//
//        if (notSmsDeliverMsg(smsDeliveryMsg))
//            throw new InvalidGsmMessageFormatException("Invalid sms format. Expected SMS-DELIVERY");
//        if (notMatchingReceiverPhone(smsDeliveryMsg, receiverPhone))
//            throw new InvalidGsmMessageFormatException("Invalid receiver phone.");
//        if (notMatchingSenderPhone(smsDeliveryMsg, senderPhone))
//            throw new InvalidGsmMessageFormatException("Invalid sender phone.");
//
//        String getMessageData = getMessageUD(smsDeliveryMsg);
//        message.setMessage(getMessageData);
//    }

    private static String getMessageUD(String smsDeliveryMsg) {
        int otherOctets = 7 * 2;
        int recPhLen = getReceiverPhoneLength(smsDeliveryMsg);
        int senPhLen = getSenderPhoneLength(smsDeliveryMsg);
        int timeStampLen = 7 * 2;
        int udl = getMessageUDL(smsDeliveryMsg);
        int elementsTillUD = otherOctets + timeStampLen + recPhLen + senPhLen + 2;
        String messageUD = smsDeliveryMsg.substring(elementsTillUD, elementsTillUD + udl);
        return unpackOctetsToString(parseHexToByteArr(messageUD));
    }

    private static int getMessageUDL(String smsDeliveryMsg) {
        int otherOctets = 7 * 2;
        int recPhLen = getReceiverPhoneLength(smsDeliveryMsg);
        int senPhLen = getSenderPhoneLength(smsDeliveryMsg);
        int timeStampLen = 7 * 2;
        int elementsTillUDL = otherOctets + timeStampLen + recPhLen + senPhLen;
        return Integer.parseInt(smsDeliveryMsg.substring(elementsTillUDL, elementsTillUDL + 2), 16) * 2;
    }

    private static int getReceiverPhoneLength(String smsDeliveryMsg) {
        return (Integer.parseInt(smsDeliveryMsg.substring(0, 2), 16) * 2) - 2;
    }

    private static boolean notMatchingReceiverPhone(String smsDeliveryMsg, String receiverPhone) {
        int recPhLen = getReceiverPhoneLength(smsDeliveryMsg);
        String senderPh = smsDeliveryMsg.substring(4, recPhLen + 4);
        String senderPhDec = decryptPhoneNum(senderPh);
        return !senderPhDec.equals(receiverPhone);
    }

    private static boolean notMatchingSenderPhone(String smsDeliveryMsg, String senderPhone) {
        int recPhLen = getReceiverPhoneLength(smsDeliveryMsg);
        int senPhLen = getSenderPhoneLength(smsDeliveryMsg);
        String senPh = smsDeliveryMsg.substring(10 + recPhLen, 10 + recPhLen + senPhLen);
        String senPhDec = decryptPhoneNum(senPh);
        return !senPhDec.equals(senderPhone);
    }

    private static int getSenderPhoneLength(String smsDeliveryMsg) {
        int recPhLen = getReceiverPhoneLength(smsDeliveryMsg);
        int senPhLen = (Integer.parseInt(smsDeliveryMsg.substring(recPhLen + 6, recPhLen + 8), 16));
        if (senPhLen % 2 == 1) senPhLen++;
        return senPhLen;
    }

    private static boolean notSmsDeliverMsg(String smsDeliveryMsg) {
        int recPhLen = getReceiverPhoneLength(smsDeliveryMsg);
        String smsSubmit = smsDeliveryMsg.substring(recPhLen + 4, recPhLen + 6);
        return !smsSubmit.equals("04");
    }

//    public synchronized static void translateToSmsDeliverMessage(Message message) {
//        String receiverNum = message.getReceiverPhone();
//        String smsSubmitMsg = message.getMessage();
//
//        String smsClen;
//        String senderAddType = "91";
//        String receiverPh = getEncReadyPhoneNum(receiverNum);
//        smsClen = Integer.toHexString((senderAddType + receiverPh).length() / 2);
//        if (smsClen.length() > 2) smsClen = smsClen.substring(smsClen.length() - 2);
//        if (smsClen.length() < 2) smsClen = "0".repeat(2 - smsClen.length()) + smsClen;
//        String smsDeliver = "04";
//
//        List<String> tpData = getDataFromEncMessage(smsSubmitMsg);
//
//        String tpDA = tpData.get(0);
//        String receiverAddType = tpData.get(1);
//        String senderNum = tpData.get(2);
//        String tpPID = tpData.get(3);
//        String tpDCS = tpData.get(4);
//        String tpSCTs = getSemiOctetsTime();
//        String tpUDL = tpData.get(5);
//        String tpUD = tpData.get(6);
//
//        String smsDeliverMsg = smsClen + senderAddType + receiverPh + smsDeliver + tpDA + receiverAddType + senderNum
//                + tpPID + tpDCS + tpSCTs + tpUDL + tpUD;
//        message.setMessage(smsDeliverMsg);
//    }

    private static List<String> getDataFromEncMessage(String encryptedMessage) {
        if (!encryptedMessage.startsWith("11", 2)) throw new InvalidGsmMessageFormatException("Not a SMS-SUBMIT");
        List<String> tpData = new ArrayList<>();

        String tpDA = encryptedMessage.substring(6, 8);
        int phLen = Integer.parseInt(tpDA, 16);
        if (phLen % 2 == 1) phLen++;
        tpData.add(tpDA); // 0

        String senderAddrType = encryptedMessage.substring(8, 10);
        tpData.add(senderAddrType); // 1

        String senderNum = encryptedMessage.substring(10, 10 + phLen);
        tpData.add(senderNum); // 2

        String tpPid = encryptedMessage.substring(phLen, phLen + 2);
        tpData.add(tpPid); // 3

        String tpDCS = encryptedMessage.substring(phLen + 2, phLen + 4);
        tpData.add(tpDCS); // 4

        String tpUDL = encryptedMessage.substring(phLen + 6, phLen + 8);
        tpData.add(tpUDL); // 5

        String tpUD = encryptedMessage.substring(phLen + 8);
        tpData.add(tpUD); // 6

        return tpData;
    }

    private static String getSemiOctetsTime() {
        return "99309251619580";
    }

//    public synchronized static void encrypt(Message message) {
//        String senderNum = message.getSenderPhone();
//        String msg = message.getMessage();
//        String smsC = "00";
//        String smsSubmit = "11";
//        String tpMR = Integer.toHexString(message.getTimesPassed());
//        if (tpMR.length() > 2) tpMR = tpMR.substring(tpMR.length() - 2);
//        if (tpMR.length() < 2) tpMR = "0".repeat(2 - tpMR.length()) + tpMR;
//        String tpDA = Integer.toHexString(senderNum.length());
//        if (tpDA.length() > 2) tpDA = tpDA.substring(tpDA.length() - 2);
//        if (tpDA.length() < 2) tpDA = "0".repeat(2 - tpDA.length()) + tpDA;
//        String receiverAddType = "91";
//        String phNum = getEncReadyPhoneNum(senderNum);
//        String tpPID = "00";
//        String tpDCS = "02";
//        String tpVP = "00";
//        String tpUDL = Integer.toHexString(msg.length());
//        if (tpUDL.length() > 2) tpUDL = tpUDL.substring(tpUDL.length() - 2);
//        if (tpUDL.length() < 2) tpUDL = "0".repeat(2 - tpUDL.length()) + tpUDL;
//        byte[] packedMessage = packToOctets(msg);
//        String tpUD = toHex(packedMessage);
//
//        String encryptedMsg = smsC + smsSubmit + tpMR + tpDA + receiverAddType + phNum + tpPID + tpDCS + tpVP + tpUDL + tpUD;
//        message.setMessage(encryptedMsg);
//    }

    private static String toHex(byte[] packedMessage) {
        StringBuilder builder = new StringBuilder(packedMessage.length);
        for (byte b : packedMessage) {
            String hex = Integer.toHexString(b).toUpperCase();
            if (hex.length() > 2) hex = hex.substring(hex.length() - 2);
            builder.append(hex);
        }
        return builder.toString();
    }

    private static byte[] parseHexToByteArr(String hex) {
        byte[] octets = new byte[hex.length() / 2];
        for (int i = 0; i <= hex.length() - 2; i += 2) {
            int last = i + 2;
            int octet = Integer.parseInt(hex.substring(i, last), 16);
            octets[i / 2] = (byte) octet;
        }
        return octets;
    }

    private static byte[] packToOctets(String message) {
        char[] msgChars = message.toCharArray();
        List<String> strSeptets = new ArrayList<>(msgChars.length);
        for (char c : msgChars) {
            String bit = Integer.toBinaryString(c);
            if (bit.length() > 7) bit = bit.substring(bit.length() - 7);
            if (bit.length() < 7) bit = "0".repeat(7 - bit.length()) + bit;
            strSeptets.add(bit);
        }
        List<List<String>> subSeptets = new ArrayList<>();
        for (int i = 0; i < strSeptets.size(); i += 8) {
            int last = Math.min((i + 8), strSeptets.size());
            subSeptets.add(strSeptets.subList(i, last));
        }
        List<String> strOctets = new ArrayList<>();
        for (List<String> septet : subSeptets) {
            if (septet.size() == 1) {
                strOctets.add(septet.get(0) + "0");
            }
            for (int i = 0; i < septet.size() - 1; i++) {
                String current = septet.get(i);
                int toTrim = 8 - current.length();
                String nextSeptet = septet.get(i + 1);
                String trim = nextSeptet.substring(nextSeptet.length() - toTrim);
                septet.set(i + 1, nextSeptet.substring(0, nextSeptet.length() - toTrim));
                String octet = trim + current;
                if (i + 1 == septet.size() - 1) {
                    if (octet.length() < 8)
                        octet = "0".repeat(8 - octet.length()) + octet;
                    if (septet.size() != 8) {
                        String newNext = septet.get(i + 1);
                        String nextOctet = "0".repeat(8 - newNext.length()) + newNext;
                        strOctets.add(octet);
                        strOctets.add(nextOctet);
                        break;
                    }
                }
                strOctets.add(octet);
            }
        }
        byte[] octets = new byte[strOctets.size()];
        int i = 0;
        for (String octet : strOctets) {
            int bin = Integer.parseInt(octet, 2);
            octets[i] = (byte) bin;
            i++;
        }
        return octets;
    }

    private static String unpackOctetsToString(byte[] octetsArr) {
        List<String> strOctets = new ArrayList<>(octetsArr.length);
        for (byte b : octetsArr) {
            String binOctet = Integer.toBinaryString(b);
            if (binOctet.length() > 8) binOctet = binOctet.substring(binOctet.length() - 8);
            if (binOctet.length() < 8) binOctet = "0".repeat(8 - binOctet.length()) + binOctet;
            strOctets.add(binOctet);
        }
        List<List<String>> subOctets = new ArrayList<>();
        for (int i = 0; i < strOctets.size(); i += 7) {
            int last = Math.min(i + 7, strOctets.size());
            subOctets.add(strOctets.subList(i, last));
        }
        List<String> septets = new ArrayList<>();
        String save = "";
        for (List<String> octet : subOctets) {
            for (int i = 0; i < octet.size() - 1; i++) {
                if (i == 0 && !save.isEmpty()) octet.add(0, save);
                String current = octet.get(i);
                int toSub = -(7 - current.length());
                String nextOctet = octet.get(i + 1);
                String subToNext = current.substring(0, toSub);
                current = current.substring(toSub);
                nextOctet = nextOctet + subToNext;
                octet.set(i + 1, nextOctet);
                septets.add(current);
                if (i + 1 == octet.size() - 1) {
                    if (nextOctet.length() > 7) {
                        save = nextOctet.substring(0, nextOctet.length() - 7);
                        nextOctet = nextOctet.substring(nextOctet.length() - 7);
                    }
                    septets.add(nextOctet);
                }
            }
        }
        List<Character> septetsChar = new ArrayList<>();
        septets.forEach(septet -> septetsChar.add((char) Integer.parseInt(septet, 2)));
        StringBuilder result = new StringBuilder();
        septetsChar.forEach(result::append);
        return result.toString();
    }

    private static String getEncReadyPhoneNum(String receiverNum) {
        String evenNum = receiverNum.length() % 2 == 1 ? receiverNum += "F" : receiverNum;
        return toSemiOctets(evenNum);
    }

    private static String toSemiOctets(String sequence) {
        StringBuilder swappedNum = new StringBuilder();
        for (int i = 0; i < sequence.length(); i += 2) {
            char first = sequence.charAt(i);
            char second = sequence.charAt(i + 1);
            swappedNum.append(second).append(first);
        }
        return swappedNum.toString();
    }

    private static String decryptPhoneNum(String senderPh) {
        String reversed = toSemiOctets(senderPh);
        if (reversed.endsWith("F")) return reversed.substring(0, reversed.length() - 1);
        return reversed;
    }
}
