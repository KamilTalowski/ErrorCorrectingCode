package app;

import algorithm.HammingCode;
import algorithm.MultidimensionalParityCodeCoder;
import algorithm.TripleRepetitionCode;
import communication.Channel;
import communication.Receiver;
import communication.Transmitter;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.Utils;

import java.io.*;

public class MainApp extends Application {

    private enum AlgorithmType {Triple, Multidimensional, Hamming}

    private enum NegateType {Bit, Bits}

    private Stage primaryStage;
    private StringBuilder report = new StringBuilder();


    @FXML
    public Label fxml_Communicate;
    @FXML
    public TextField fxml_bitsInput, fxml_bitsToNegate;
    @FXML
    private Text fxml_BitsTransmitter2, fxml_BitsChannel2, fxml_BitsReceiver2;
    @FXML
    private ToggleGroup negateBitsGroup, algorithmGroup;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("main.fxml"));
        Scene scene = new Scene(root, 913.0, 434.0);

        this.primaryStage = primaryStage;
        primaryStage.setResizable(false);
        primaryStage.setTitle("FXML Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    public void onSaveReport(ActionEvent actionEvent) {
        if (!isReadyToStart()) {
            return;
        }

        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file == null) {
            fxml_Communicate.setText("Can't create file!");
            return;
        }

        try (PrintWriter out = new PrintWriter(file)) {
            String logs = startTransmission();
            if (logs != null) {
                out.println(startTransmission());
            } else {
                out.println("Something goes wrong!");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean isReadyToStart() {
        String message = fxml_bitsInput.getText();
        AlgorithmType algType = getAlgorithmType();
        NegateType negType = getNegateType();

        if (message == null || algType == null || negType == null) {
            fxml_Communicate.setText("Select message, algorithm type and negation type");
            return false;
        }
        return true;
    }

    private String startTransmission() {
        if (!isReadyToStart()) {
            return null;
        }
        report = new StringBuilder();
        resetFields();

        String message = fxml_bitsInput.getText();

        report.append("Orginal message to be sent: ").append(message).append('\n');

        String sentMessage = sendMessage(message);

        if (sentMessage == null) {
            return report.toString();
        }

        report.append("---- START TRANSMISSION ----").append('\n');
        report.append("Channel message before: ").append(sentMessage).append('\n');

        Channel channel = new Channel();
        channel.startTransmission(sentMessage);
        negateBits(channel);

        String channelMessage = channel.endTransmission();
        if (channelMessage == null) {
            fxml_BitsChannel2.setText("");
            String errorMessage = "Problem with communication channel";
            report.append(errorMessage).append('\n');
            fxml_Communicate.setText(errorMessage);
            return report.toString();
        }
        report.append("Channel message after: ").append(channelMessage).append('\n');

        fxml_BitsChannel2.setText(channelMessage);
        String messageFromReceiver = getMessageFromReceiver(channelMessage);
        report.append("---- END OF TRANSMISSION ----").append('\n');
        report.append("Received message: ").append(messageFromReceiver).append('\n');

        if (messageFromReceiver != null) {
            fxml_BitsReceiver2.setText(messageFromReceiver);
            fxml_Communicate.setText("");
        } else {
            fxml_Communicate.setText("Problem with decoding");
            fxml_BitsReceiver2.setText("");
        }
        return report.toString();
    }

    private void negateBits(Channel channel) {
        NegateType negType = getNegateType();

        switch (negType) {
            case Bit:
                report.append("Negate random bit").append('\n');
                channel.negateRandomBit();
                break;
            case Bits:
                try {
                    if (fxml_bitsToNegate.getText() == null) {
                        fxml_Communicate.setText("Type amount of bits");
                    }
                    Integer numberOfBits = Integer.parseInt(fxml_bitsToNegate.getText());
                    report.append("Negate ").append(numberOfBits).append(" random bits").append('\n');
                    channel.negateRandomBits(numberOfBits);
                } catch (NumberFormatException e) {
                    report.append("Type amount of bits").append('\n');
                    fxml_Communicate.setText("Type amount of bits");
                }
                break;
        }
    }

    private String getMessageFromReceiver(String channelMessage) {
        AlgorithmType algType = getAlgorithmType();
        Receiver receiver = new Receiver();
        String messageFromReceiver = null;

        switch (algType) {
            case Triple:
                messageFromReceiver = receiver.getMessage(channelMessage, new TripleRepetitionCode());
                break;
            case Multidimensional:
                messageFromReceiver = receiver.getMessage(channelMessage, new MultidimensionalParityCodeCoder());
                break;
            case Hamming:
                messageFromReceiver = receiver.getMessage(channelMessage, new HammingCode());
                break;
        }
        return messageFromReceiver;
    }

    private String sendMessage(String message) {
        AlgorithmType algType = getAlgorithmType();
        Transmitter trans = new Transmitter();
        String sentMessage = null;
        try {
            switch (algType) {
                case Triple:
                    fxml_BitsTransmitter2.setText(Utils.convertToBinaryString(fxml_bitsInput.getText()));
                    sentMessage = trans.sendMessage(message, new TripleRepetitionCode());
                    break;
                case Multidimensional:
                    fxml_BitsTransmitter2.setText(Utils.convertMessageToBinary(fxml_bitsInput.getText()));
                    sentMessage = trans.sendMessage(message, new MultidimensionalParityCodeCoder());
                    break;
                case Hamming:
                    fxml_BitsTransmitter2.setText(Utils.convertToBinaryString(fxml_bitsInput.getText()));
                    sentMessage = trans.sendMessage(message, new HammingCode());
                    break;
            }
        } catch (NumberFormatException e) {
            resetFields();
            String errorMessage = "Input is to big! Choose normal number";
            report.append(errorMessage).append('\n');
            fxml_Communicate.setText(errorMessage);
            return null;
        }
        if (sentMessage == null) {
            String errorMessage = "Problem with encoding message";
            report.append(errorMessage).append('\n');
            fxml_Communicate.setText(errorMessage);
        }
        return sentMessage;
    }

    private void resetFields() {
        fxml_BitsTransmitter2.setText("");
        fxml_BitsChannel2.setText("");
        fxml_BitsReceiver2.setText("");
        fxml_Communicate.setText("");
    }

    @FXML
    public void onFromFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        String input = null;
        if(selectedFile != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    input = line;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fxml_bitsInput.setText(input == null ? "" : input);
    }

    @FXML
    private void onStartTransmission(ActionEvent event) {
        startTransmission();
    }

    private AlgorithmType getAlgorithmType() {
        if (algorithmGroup.getSelectedToggle() == null || algorithmGroup.getSelectedToggle().getUserData() == null) {
            return null;
        }

        String selected = algorithmGroup.getSelectedToggle().getUserData().toString();
        if (selected != null) {
            switch (selected) {
                case "triple":
                    return AlgorithmType.Triple;
                case "parity":
                    return AlgorithmType.Multidimensional;
                case "hamming":
                    return AlgorithmType.Hamming;
            }
        }
        return null;
    }

    private NegateType getNegateType() {
        if (negateBitsGroup.getSelectedToggle() == null || negateBitsGroup.getSelectedToggle().getUserData() == null) {
            return null;
        }

        String selected = negateBitsGroup.getSelectedToggle().getUserData().toString();
        if (selected != null) {
            if (selected.equals("negateBit")) {
                return NegateType.Bit;
            } else if (selected.equals("negateBits")) {
                return NegateType.Bits;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}