package algorithm;

import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MultidimensionalParityCodeCoder implements Coder, Decoder {
    private static final int MARGIN = 4;

    private enum Type {ROW, COLUMN}

    @Override
    public String encode(String message) {
        if (message == null || message.length() == 0) {
            System.err.println("Trying to encode empty message!");
            return null;
        }

        int len = message.length();
        int matrixRowLength = (int) Math.ceil(Math.sqrt(len));

        char[] messageArray = message.toCharArray();

        char[][] matrix = new char[matrixRowLength + MARGIN][matrixRowLength + MARGIN];

        for (int i = 0; i < len; i += matrixRowLength) {
            int k = i;
            do {
                matrix[i / matrixRowLength][k % matrixRowLength] = messageArray[k];
                ++k;
            }
            while (k < len && k % matrixRowLength != 0);

            int rowSum = accumulateRow(matrix[i / matrixRowLength]);
            insertRowSum(rowSum, matrix[i / matrixRowLength], matrixRowLength);
        }

        for (int column = 0; column < matrixRowLength; ++column) {
            int columnSum = getColumnSum(matrix, column);
            insertColumnSum(columnSum, matrix, matrixRowLength, column);
        }

        return Utils.convertMessageToBinary(matrixToString(matrix));
    }

    private int accumulateRow(char[] matrixRow) {
        int sum = 0;
        for (char c : matrixRow) {
            if (Utils.isCharEmpty(c)) {
                break;
            }
            sum += Utils.charToInt(c);
        }

        return sum;
    }

    private void insertRowSum(int rowSum, char[] matrix, int matrixRowLength) {
        char[] sum = Utils.intToCharArray(rowSum);
        if (sum.length > MARGIN) {
            System.err.println("DANGEROUS!!!");
        }

        for (int i = 0; i < sum.length && i + matrixRowLength < matrix.length; ++i) {
            matrix[matrixRowLength + i] = sum[i];
        }
    }

    private int getColumnSum(char[][] matrix, int column) {
        int sum = 0;
        for (char[] chArr : matrix) {
            if (column >= chArr.length) {
                System.err.println("DANGEROUSSS");
            }

            if (Utils.isCharEmpty(chArr[column])) {
                break;
            }
            sum += Utils.charToInt(chArr[column]);
        }

        return sum;
    }

    private void insertColumnSum(int columnSum, char[][] matrix, int matrixRowLength, int column) {
        char[] sum = Utils.intToCharArray(columnSum);
        if (sum.length > MARGIN) {
            System.err.println("DANGEROUS!!!");
        }

        for (int i = 0; i < sum.length && i + matrixRowLength < matrix.length; ++i) {
            matrix[matrixRowLength + i][column] = sum[i];
        }
    }

    private String matrixToString(char[][] matrix) {
        StringBuilder builder = new StringBuilder();
        for (char[] matrixRow : matrix) {
            for (char ch : matrixRow) {
                builder.append(ch);
            }
        }

        return builder.toString();
    }

    @Override
    public String decode(String message) {
        message = Utils.convertBinaryToMessage(message);
        System.out.println("MESS " + message);

        int contentLength = getEncodedMessageLength(message);
        System.out.println("Content " + contentLength);

        char[][] matrix = new char[contentLength][contentLength];

        List<Integer> rowSums = new ArrayList<>();
        List<Integer> columnSums = new ArrayList<>();

        int count = 0;
        for (int i = 0; count < contentLength && i + contentLength < message.length(); i += contentLength + MARGIN, ++count) {
            char[] content = message.substring(i, i + contentLength).toCharArray();
            matrix[count] = content;

            char number = message.charAt(i + contentLength);
            if (!Utils.isCharEmpty(number)) {
                rowSums.add(Utils.charToInt(number));
            } else {
                rowSums.add(0);
            }
        }

        for (int i = (contentLength + MARGIN) * contentLength; i < (contentLength + MARGIN) * contentLength + contentLength; ++i) {
            char number = message.charAt(i);
            if (!Utils.isCharEmpty(number)) {
                columnSums.add(Utils.charToInt(number));
            } else {
                columnSums.add(0);
            }
        }

        List<Integer> invalidRows = findInvalidRowOrColumn(Type.ROW, matrix, rowSums);
        List<Integer> invalidColumns = findInvalidRowOrColumn(Type.COLUMN, matrix, columnSums);

        if (invalidColumns.size() == 1 && invalidRows.size() == 1) {
            fixMatrix(matrix, columnSums, rowSums, invalidColumns.get(0), invalidRows.get(0));
        } else if (invalidColumns.size() > 1 || invalidRows.size() > 1) {
            System.err.println("Message corrupted");
            return null;
        }

        return Utils.convertMessageToBinary(matrixToString(matrix).trim());
    }

    private int getEncodedMessageLength(String message) {
        return ((int) Math.sqrt((double) message.length()) - MARGIN);
    }

    private List<Integer> findInvalidRowOrColumn(Type type, char[][] matrix, List<Integer> sumsInRowOrColumn) {
        List<Integer> invalid = new ArrayList<>();

        for (int i = 0; i < matrix.length; ++i) {
            int sum = 0;

            for (int j = 0; j < matrix[i].length; ++j) {
                char charToCheck = type == Type.ROW ? matrix[i][j] : matrix[j][i];
                if (Utils.isCharEmpty(charToCheck))
                    break;
                sum += Utils.charToInt(charToCheck);
            }

            if (sum != sumsInRowOrColumn.get(i)) {
                invalid.add(i);
            }
        }

        return invalid;
    }

    private void fixMatrix(char[][] matrix, List<Integer> columnSums, List<Integer> rowSums, int invalidColumn, int invalidRow) {
        int rowSum = accumulateRow(matrix[invalidRow]);
        int validValue = rowSums.get(invalidRow) - (rowSum - Utils.charToInt(matrix[invalidRow][invalidColumn]));

        matrix[invalidRow][invalidColumn] = Utils.intToCharArray(validValue)[0];
    }
}
