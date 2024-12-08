package model;

public class Article {
    private String articleId;
    private String title;
    private String author;
    private String urlToImage;
    private String description;
    private String url;
    private String publishAt;
    private String content;
    private String category;

    public Article(){

        this.articleId = articleId;
        this.title = title;
        this.author = author;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishAt = publishAt;
        this.content = content;
        this.category = category;

    }

    public String getArticleId() {
        return articleId;
    }
    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrlToImage() {
        return urlToImage;
    }
    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }
    public String getPublishAt() {
        return publishAt;
    }
    public void setPublishAt(String publishAt) {
        this.publishAt = publishAt;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

//    @Override
//    public String toString() {
//        return "Article{title='" + title + "', author=" + author + "',description=" + description+"',urlImage=" + urlToImage+"'url" + url+"'publishat=" + publishAt+ "'content=" + content+"'category"+"}";
//    }

}
