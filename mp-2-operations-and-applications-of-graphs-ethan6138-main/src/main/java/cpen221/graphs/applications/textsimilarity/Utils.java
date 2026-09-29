package cpen221.graphs.applications.textsimilarity;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utility methods for reading textual content from local files or URLs
 * and for extracting “words” from raw tokens according to the project’s
 * punctuation rules.
 *
 * <p>This class is stateless and only exposes static helper methods:</p>
 * <ul>
 *   <li>{@link #readContent(String)}: read full text from a file or URL</li>
 *   <li>{@link #isFile(String)}: check if a path refers to an existing file</li>
 *   <li>{@link #isValidURL(String)}: check if a string is a syntactically valid URL</li>
 *   <li>{@link #extractWord(String)}: strip leading/trailing punctuation to obtain a word</li>
 * </ul>
 */
public class Utils {

    /**
     * Read the entire content from the given input, which may be either a
     * local file path or a URL.
     *
     * <p>Behaviour:</p>
     * <ul>
     *   <li>If {@code input} names an existing file, the file is opened and read
     *       line by line.</li>
     *   <li>Otherwise, if {@code input} is a syntactically valid URL, the URL is
     *       opened and its content is read line by line.</li>
     *   <li>If neither case applies, an error message is printed and {@code null}
     *       is returned.</li>
     * </ul>
     *
     * <p>Lines are concatenated with a trailing newline character to form the
     * returned string.</p>
     *
     * @param input a file path or URL string
     * @return the full contents as a single {@code String}, or {@code null}
     * if the file/URL could not be read or {@code input} is invalid
     */
    public static String readContent(String input) {
        StringBuilder content = new StringBuilder();

        if (isFile(input)) {
            // Read content from the file
            try (BufferedReader reader = new BufferedReader(new FileReader(input))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            } catch (IOException e) {
                System.err.println("Error reading from file: " + e.getMessage());
                return null;
            }
        } else if (isValidURL(input)) {
            // Read content from the URL
            try {
                URL url = new URL(input);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
            } catch (IOException e) {
                System.err.println("Error reading from URL: " + e.getMessage());
                return null;
            }
        } else {
            System.out.println("Invalid file name or URL.");
            return null;
        }
        return content.toString();
    }

    /**
     * Determine whether the given path refers to an existing regular file.
     *
     * @param path file system path to check
     * @return {@code true} if {@code path} exists and is a file;
     * {@code false} otherwise
     */
    public static boolean isFile(String path) {
        File file = new File(path);
        return file.exists() && file.isFile();
    }

    /**
     * Determine whether the given string is a syntactically valid URL.
     *
     * <p>This method checks that {@code urlString} can be parsed as a
     * {@link URL} and converted to a URI. It does not attempt to open
     * the URL or verify that any remote resource exists.</p>
     *
     * @param urlString the candidate URL string
     * @return {@code true} if {@code urlString} is a syntactically valid URL;
     * {@code false} otherwise
     */
    public static boolean isValidURL(String urlString) {
        try {
            URL url = new URL(urlString);
            url.toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extract a “word” from the given token by removing leading and trailing
     * punctuation characters.
     *
     * <p>Rules (matching the project handout):</p>
     * <ul>
     *   <li>The input {@code token} is assumed to be a non-null string obtained
     *       by splitting on whitespace.</li>
     *   <li>Any punctuation characters at the <em>start</em> or <em>end</em> of the
     *       token are removed.</li>
     *   <li>If the resulting string is empty, or consists entirely of punctuation,
     *       the empty string {@code ""} is returned.</li>
     *   <li>Internal punctuation (e.g., in {@code "real-time"} or {@code "a$b"})
     *       is preserved.</li>
     * </ul>
     *
     * @param token a single token from the original text (may include punctuation)
     * @return the cleaned word with leading/trailing punctuation removed,
     * or {@code ""} if no valid word remains
     */
    public static String extractWord(String token) {
        String regex = "^\\p{Punct}+|\\p{Punct}+$";
        String cleanedWord = token.replaceAll(regex, "").trim();
        return cleanedWord.isEmpty() || cleanedWord.matches("^\\p{Punct}+$") ? "" : cleanedWord;
    }

}
