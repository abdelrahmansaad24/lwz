import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class LZWEncoder {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java LZWEncoder <input file> <number of bits>");
            System.exit(1);
        }

        String inputFile = args[0];
        int n = Integer.parseInt(args[1]);
        int maximumTableSize = (int) Math.pow(2, n);

        // Reading the input file
        StringBuilder data = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line).append("\n");
            }
        }

        // Building and initializing the dictionary
        int dictionarySize = 256;
        Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < dictionarySize; i++) {
            dictionary.put("" + (char) i, i);
        }

        String string = "";
        ByteArrayOutputStream compressedData = new ByteArrayOutputStream();

        // LZW Compression algorithm
        for (char symbol : data.toString().toCharArray()) {
            String stringPlusSymbol = string + symbol;
            if (dictionary.containsKey(stringPlusSymbol)) {
                string = stringPlusSymbol;
            } else {
                compressedData.write(ByteBuffer.allocate(2).putShort(dictionary.get(string).shortValue()).array());
                if (dictionary.size() < maximumTableSize) {
                    dictionary.put(stringPlusSymbol, dictionarySize++);
                }
                string = "" + symbol;
            }
        }

        if (!string.isEmpty()) {
            compressedData.write(ByteBuffer.allocate(2).putShort(dictionary.get(string).shortValue()).array());
        }

        // Storing the compressed data into a file
        String outputFileName = inputFile.substring(0, inputFile.lastIndexOf('.')) + ".lzw";
        try (FileOutputStream outputStream = new FileOutputStream(outputFileName)) {
            compressedData.writeTo(outputStream);
        }

        System.out.println("File compressed successfully: " + outputFileName);
    }
}
