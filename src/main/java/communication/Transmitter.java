package communication;

import algorithm.Coder;

public class Transmitter {
    public String sendMessage(String message, Coder codec) {
        return codec.encode(message);
    }
}
