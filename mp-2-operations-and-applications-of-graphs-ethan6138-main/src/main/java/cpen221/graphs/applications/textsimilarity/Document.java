package cpen221.graphs.applications.textsimilarity;

import cpen221.graphs.utils.In;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable representation of a textual document identified by a URL or file path.
 *
 * <p>Representation:
 * <ul>
 *   <li>{@code documentURL}: location of the document (file name or URL)</li>
 *   <li>{@code documentId}: user-visible identifier (e.g., book title)</li>
 *   <li>{@code wordCounts}: map from lowercase words to positive occurrence counts</li>
 *   <li>{@code totalWordCount}: sum of all word counts</li>
 * </ul>
 *
 * <p>Representation invariant:
 * <ul>
 *   <li>{@code documentURL} and {@code documentId} are non-null and non-empty</li>
 *   <li>{@code wordCounts} is non-null</li>
 *   <li>Every key in {@code wordCounts} is non-null, non-empty, lowercase</li>
 *   <li>Every value in {@code wordCounts} is non-null and &gt; 0</li>
 *   <li>{@code totalWordCount == sum(wordCounts.values())}</li>
 * </ul>
 *
 * <p>Abstraction function:
 *   AF(documentURL, documentId, wordCounts, totalWordCount) represents the
 *   document located at {@code documentURL} with identifier {@code documentId},
 *   where {@code wordCounts} records how many times each word appears
 *   (bag-of-words model).</p>
 */
public class Document {

    private final String documentURL;
    private final String documentId;
    private final Map<String, Integer> wordCounts;
    private final int totalWordCount;

    /**
     * Create a document identified by {@code id}, whose contents are read
     * from the file or URL given by {@code url}.
     *
     * @param id  non-null, non-empty identifier for this document
     * @param url non-null, non-empty file path or URL pointing to the document text
     * @throws NullPointerException     if {@code id} or {@code url} is null
     * @throws IllegalArgumentException if {@code id} or {@code url} is empty
     * @throws IOException              if {@code url} cannot be opened or read
     */
    public Document(String id, String url) throws IOException {
        assert id != null;
        assert !id.isEmpty();
        assert url != null;
        assert !url.isEmpty();

        this.documentURL = url;
        this.documentId = id;

        Map<String, Integer> mutableWordCounts = new HashMap<>();
        int runningWordTotal = 0;

        final String rawText;
        try {
            In documentInput = new In(url);
            rawText = documentInput.readAll();
            documentInput.close();
        } catch (IllegalArgumentException exception) {
            throw new IOException("Could not read document from: " + url, exception);
        }

        String lowerCasedText = rawText.toLowerCase(Locale.ROOT);
        String[] rawTokens = lowerCasedText.split("\\s+");

        for (String rawToken : rawTokens) {
            if (rawToken.isEmpty()) {
                continue;
            }

            String cleanedWord = Utils.extractWord(rawToken);
            if (cleanedWord.isEmpty()) {
                continue;   // token was pure punctuation or stripped to empty
            }

            mutableWordCounts.merge(cleanedWord, 1, Integer::sum);
            runningWordTotal++;
        }

        this.wordCounts = Collections.unmodifiableMap(mutableWordCounts);
        this.totalWordCount = runningWordTotal;

        checkRep();
    }

    /**
     * Convenience constructor that uses {@code url} as both the identifier
     * and the source of the document contents.
     *
     * @param url non-null, non-empty file path or URL pointing to the document text
     * @throws NullPointerException     if {@code url} is null
     * @throws IllegalArgumentException if {@code url} is empty
     * @throws IOException              if {@code url} cannot be opened or read
     */
    public Document(String url) throws IOException {
        this(url, url);
    }

    // representation invariant check
    private void checkRep() {
        assert documentURL != null && !documentURL.isEmpty();
        assert documentId != null && !documentId.isEmpty();
        assert wordCounts != null;

        int sumOfCounts = 0;
        for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
            String word = entry.getKey();
            Integer count = entry.getValue();
            assert word != null;
            assert !word.isEmpty();
            assert word.equals(word.toLowerCase(Locale.ROOT));
            assert count != null;
            assert count > 0;
            sumOfCounts += count;
        }
        assert sumOfCounts == totalWordCount;
    }

    /**
     * Obtain an immutable map that represents the words in the document
     * and the number of occurrences of each word.
     *
     * @return an immutable view of the word-count map
     */
    public Map<String, Integer> getCountMap() {
        return wordCounts;
    }

    /**
     * @return Total number of words (after tokenization and cleaning).
     */
    int getTotalWords() {
        return totalWordCount;
    }

    /**
     * @return the unique identifier of this document; never null or empty
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * @return the file path or URL this document was constructed from;
     * never null or empty
     */
    public String getDocumentURL() {
        return documentURL;
    }

    /**
     * Compute the Jensen–Shannon divergence between this document
     * and {@code otherDoc}, scaled and rounded as defined in
     * {@link Similarity#getJensenShannonDivergence(Document, Document)}.
     *
     * @param otherDoc non-null document to compare with
     * @return non-negative integer divergence value (as a long),
     * consistent with {@code Similarity.getJensenShannonDivergence}
     * @throws NullPointerException if {@code otherDoc} is null
     */
    public long computeJSDiv(Document otherDoc) {
        Objects.requireNonNull(otherDoc, "otherDoc must not be null");
        // Similarity handles the scaling; we just cast the integer-valued double
        return (long) Similarity.getJensenShannonDivergence(this, otherDoc);
    }

    /**
     * Produce a string representation of a Document.
     */
    @Override
    public String toString() {
        // return the id associated with this document
        return this.documentId;
    }

}

