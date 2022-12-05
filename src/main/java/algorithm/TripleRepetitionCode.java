package algorithm;

import org.apache.commons.lang.StringUtils;
import utils.Utils;

import java.util.regex.Pattern;

public class TripleRepetitionCode implements Coder, Decoder {
    private static final String correctTriplePattern = "0{3}|1{3}";

    @Override
    public String encode(String message) {
        if (!Utils.searchNonDigit(message)) {
            System.err.println("Message: " + message + " contains non digit. Illegal form, aborting!");
            return null;
        }
        message = Utils.convertToBinaryString(message);
        return tripleEachBit(message);
    }

    private String tripleEachBit(String message) {
        StringBuilder codedMessage = new StringBuilder();
        for (char bit : message.toCharArray()) {
            codedMessage.append(StringUtils.repeat(Character.toString(bit), 3));
        }
        return codedMessage.toString();
    }

    private char decodeTriples(String tripleValue) {
        if (Pattern.compile(correctTriplePattern).matcher(tripleValue).find()) {
            return tripleValue.charAt(0);
        }
        int countZero = 0, countOne = 0;
        for (char bit : tripleValue.toCharArray()) {
            if (bit == '0') {
                countZero++;
            }
            else{
            countOne++;
            }
        }
        if (countZero > countOne) {
            return '0';
        }

        return '1';
    }

    @Override
    public String decode(String message) {
        if (message.length() % 3 != 0 && !Utils.searchNonDigit(message)) {
            return null;
        }
        StringBuilder decodedMessage = new StringBuilder();
        for (int i = 0; i < message.length(); i += 3) {
            decodedMessage.append(decodeTriples(message.substring(i,i+3)));
        }

        return decodedMessage.toString();
    }
}
