package utils;

import java.util.regex.Pattern;

public class Utils {
    private static final String nonABinary = "[^01]";
    private static String SPACE_BIT_REPRESENTATION = "1111";

    public static String convertToBinaryString(String message) throws NumberFormatException {
        String returnMessage = message;

        if (Pattern.compile(nonABinary).matcher(message).find()) {
            returnMessage = Integer.toBinaryString(Integer.parseInt(returnMessage));
        }
        return returnMessage;
    }

    public static String convertMessageToBinary(String message) {
        StringBuilder builder = new StringBuilder();

        for (char ch : message.toCharArray()) {
            if (ch == '0' || ch == '1') {
                builder.append("000" + ch);
            } else if (isCharEmpty(ch)) {
                builder.append(SPACE_BIT_REPRESENTATION);
            } else {
                StringBuilder binaryString = new StringBuilder(Integer.toBinaryString(charToInt(ch)));
                while (binaryString.length() < 4) {
                    binaryString.insert(0, "0");
                }

                builder.append(binaryString);
            }
        }

        return builder.toString();
    }

    public static String convertBinaryToMessage(String message) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < message.length(); i += 4) {
            String number = message.substring(i, i + 4);
            if (number.equals(SPACE_BIT_REPRESENTATION)) {
                builder.append(" ");
            } else {
                builder.append(Integer.parseInt(number, 2));
            }
        }

        return builder.toString();
    }

    public static boolean isCharEmpty(char ch) {
        return ch == '\u0000' || ch == ' ';
    }

    public static char[] intToCharArray(int i) {
        return ("" + i).toCharArray();
    }

    public static int charToInt(char ch) {
        return Integer.parseInt("" + ch);
    }

    public static Boolean searchNonDigit(String message) {
        return !Pattern.compile("\\D").matcher(message).find();
    }
}
