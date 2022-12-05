package communication;


import algorithm.Decoder;

public class Receiver {
    public String getMessage(String message, Decoder codec) {
        return codec.decode(message);
    }
}
