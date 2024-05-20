import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class LZWDecoder {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java LZWDecoder <input file> <number of bits>");
            System.exit(1);
        }

        String inputFile = args[0];
        int n = Integer.parseInt(args[1]);
        int maximumTableSize = (int) Math.pow(2, n);

        // Reading the compressed file
        ByteArrayOutputStream compressedData = new ByteArrayOutputStream();
        try (FileInputStream fileInputStream = new FileInputStream(inputFile)) {
            byte[] buffer = new byte[2];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                compressedData.write(buffer, 0, bytesRead);
            }
        }

        byte[] byteArray = compressedData.toByteArray();
        int[] compressedCodes = new int[byteArray.length / 2];
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        for (int i = 0; i < compressedCodes.length; i++) {
            compressedCodes[i] = byteBuffer.getShort() & 0xFFFF;
        }

        // Building and initializing the dictionary
        int dictionarySize = 256;
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < dictionarySize; i++) {
            dictionary.put(i, "" + (char) i);
        }

        int nextCode = 256;
        StringBuilder decompressedData = new StringBuilder();
        String string = "";

        // LZW Decompression algorithm
        for (int code : compressedCodes) {
            String entry;
            if (dictionary.containsKey(code)) {
                entry = dictionary.get(code);
            } else if (code == nextCode) {
                entry = string + string.charAt(0);
            } else {
                throw new IllegalArgumentException("Bad compressed code: " + code);
            }

            decompressedData.append(entry);

            if (!string.isEmpty()) {
                dictionary.put(nextCode++, string + entry.charAt(0));
            }
            string = entry;
        }

        // Storing the decompressed data into a file
        String outputFileName = inputFile.substring(0, inputFile.lastIndexOf('.')) + "_decoded.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            writer.write(decompressedData.toString());
        }

        System.out.println("File decompressed successfully: " + outputFileName);
    }
}
