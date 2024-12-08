package services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import model.Article;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;
import java.util.concurrent.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewsFetcher {

    // Thread pools request data
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private ScheduledExecutorService debounceScheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> debounceTask;

    public HttpURLConnection connection;

    public String line;
    static StringBuffer responseContent = new StringBuffer();

    public final ConcurrentHashMap<String, List<Article>> userArticlesMap = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, List<Article>> recommendedArticles = new ConcurrentHashMap<>();

    public static int arraySize;
    public final String apiKey = "c2545ef021d94ac4bebaf56e712750e3"; //021587f593344d2ba81fc9a7a42a375d

    private boolean isFetchingComplete = false; // Flag to check if fetching is complete

    public synchronized boolean isFetchingComplete() {
        return isFetchingComplete;
    }

    public synchronized void setFetchingComplete(boolean fetchingComplete) {
        this.isFetchingComplete = fetchingComplete;
    }



    // Clear all articles for the given username
    public void clearArticlesForUser(String username) {
        userArticlesMap.computeIfPresent(username, (key, existingArticles) -> {
            existingArticles.clear();  // Clear all articles for the user
            setFetchingComplete(false);
            return existingArticles;
        });
    }


    public void request(String username, String endpoint, String country, String category, int page, int pageSize) {
        executorService.submit(() -> {
            List<Article> articles = fetchCategoryData(username,endpoint, country,category,page,pageSize);

            // Ensure thread-safe update of user's article list
            userArticlesMap.compute(username, (key, existingArticles) -> {
                if (existingArticles == null) {
                    existingArticles = Collections.synchronizedList(new ArrayList<>());
                }
                existingArticles.addAll(articles); // Add new articles to the user's list
                setFetchingComplete(true);
                return existingArticles;
            });

            System.out.println("Articles for user " + username + " updated successfully.");

        });
    }

    public List<Article> fetchCategoryData(String username, String endpoint, String country, String category, int page, int pageSize) {
        List<Article> articlesList = Collections.synchronizedList(new ArrayList<>());

        try {
            responseContent.setLength(0);
            System.out.println(category);
            String apiUrl = "https://newsapi.org/v2/" + endpoint + "?country=" + country + "&category="+ category + "&page=" + page + "&pageSize=" + pageSize + "&apiKey=" + apiKey;
            System.out.println(apiUrl);
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();

            //request setup
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            if(status > 200) {
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    while ((line = reader.readLine()) != null) {
                        responseContent.append(line);
                    }
                }catch (IOException e){
                    System.out.println("Error reading response: " + e.getMessage());
                }
            }else {
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    while ((line = reader.readLine()) != null) {
                        responseContent.append(line);
                    }
                }catch (IOException e){
                    System.out.println("Error reading response: " + e.getMessage());
                }
            }

            articlesList = parse(responseContent.toString(),category);

        } catch (MalformedURLException e) {
            System.err.println("Invalid URL: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error during API call: " + e.getMessage());
        }

        return articlesList;
    }


    public List<Article> parse(String respondBody, String category){
        List<Article> articlesList = Collections.synchronizedList(new ArrayList<>());

        if (respondBody == null || respondBody.isEmpty()) {
            System.out.println("Empty or invalid response body.");
            return articlesList;
        }

        JSONObject responseJson = new JSONObject(respondBody);

        if (!responseJson.has("articles")) {
            System.out.println("No 'articles' key in the response for category: " + category);
            return articlesList;
        }

        JSONArray allNews = responseJson.getJSONArray("articles");


        for (int i = 0; i < allNews.length(); i++){
            JSONObject news = allNews.getJSONObject(i);

            Article article = new Article();
            if (!news.getString("title").equals("[Removed]")) {
                article.setTitle(news.getString("title"));
                article.setAuthor(news.optString("author", "Unknown"));
                article.setDescription(news.optString("description", "No description available"));
                article.setUrl(news.optString("url", "No url available"));
                article.setUrlToImage(news.optString("urlToImage", "No image available"));
                article.setPublishAt(news.getString("publishedAt"));
                article.setContent(news.optString("content" , "No content available"));
                article.setCategory(category);
                articlesList.add(article);
            }

        }

        assignUniqueIds(articlesList);
        arraySize = articlesList.size();
        return articlesList;
    }


    public void assignUniqueIds(List<Article> articles) {
        for (Article article : articles) {
            if (article.getUrl() != null) {
                // Generate a hash-based ID from the URL
                String uniqueId = Integer.toHexString(article.getUrl().hashCode());
                article.setArticleId(uniqueId);
            } else if (article.getTitle() != null){
                // Handle cases where the URL might be null
                String fallbackId = Integer.toHexString(article.getTitle().hashCode());
                article.setArticleId(fallbackId);
            }else {
                article.setArticleId(UUID.randomUUID().toString());
            }
        }
    }

}
