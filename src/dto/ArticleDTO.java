package dto;

/**
 * This is Data Transfer Object, transfer data from Business Logic Layer to UI
 */
public class ArticleDTO {

    // Initialize fields
    private final String uri;
    private final String date;
    private final String title;
    private final String body;

    /**
     * Constructor
     * @param uri
     * @param date
     * @param title
     * @param body
     */
    public ArticleDTO(String uri, String date, String title, String body) {
        this.uri = uri;
        this.date = date;
        this.title = title;
        this.body = body;
    }

    // Getters
    public String getUri() {
        return uri;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
