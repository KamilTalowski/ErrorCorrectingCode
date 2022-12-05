package communication;

import java.util.Random;

public class Channel {
    private String inMessage = "";

    public void startTransmission(String message) {
        inMessage = message;
    }

    public void negateRandomBit() {
        int bitToNegate = getRandomBit();
        negateBitAtPosition(bitToNegate);
    }

    private void negateBitAtPosition(int position) {
        StringBuilder builder = new StringBuilder(inMessage);
        builder.setCharAt(position,reverseBit(inMessage.charAt(position)));
        inMessage = builder.toString();
    }

    private char reverseBit(char bit) {
        return bit == '0' ? '1' : '0';
    }

    public void negateRandomBits(int numberOfBits) {
        for (int i = 0; i < numberOfBits; i++) {
            negateRandomBit();
        }
    }

    public int numberOfBitsInChannel() {
        return inMessage.length();
    }

    private int getRandomBit() {
        Random rand = new Random();
        return rand.nextInt((inMessage.length()));
    }

    public String endTransmission() {
        return inMessage;
    }
}
