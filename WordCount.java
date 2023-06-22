import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WordCount {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\royos\\Downloads\\gutenberg-1G.txt\\gutenberg-1G.txt";
        int numIterations = 1;
        long totalRuntime = 0;

        for (int i = 0; i < numIterations; i++) {
            long startTime = System.currentTimeMillis();

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                StringBuilder sb = new StringBuilder();
                String line;

                // Read text from the file
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                String text = sb.toString();

                // Clean the text by removing non-alphanumeric characters and converting to lowercase
                String cleanedText = cleanText(text);

                // Split words into an array
                String[] words = splitWords(cleanedText);

                // Create a HashMap to count word frequencies
                Map<String, Integer> wordCountMap = countWordFrequencies(words);

                // Display the word count results
                displayWordCountResults(wordCountMap);

            } catch (IOException e) {
                e.printStackTrace();
            }

            long endTime = System.currentTimeMillis();
            long runtime = endTime - startTime;
            totalRuntime += runtime;

            System.out.println("Iteration " + (i + 1) + " Runtime: " + runtime + " milliseconds");
        }

        double averageRuntime = (double) totalRuntime / numIterations;
        System.out.println("Average Runtime: " + averageRuntime + " milliseconds");
    }

    private static String cleanText(String text) {
        return text.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
    }

    private static String[] splitWords(String text) {
        return text.split(" ");
    }

    private static Map<String, Integer> countWordFrequencies(String[] words) {
        Map<String, Integer> wordCountMap = new HashMap<>();

        for (String word : words) {
            wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
        }

        return wordCountMap;
    }

    private static void displayWordCountResults(Map<String, Integer> wordCountMap) {
        for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
            String word = entry.getKey();
            int count = entry.getValue();
//            System.out.println(word + ": " + count);
        }
    }
}