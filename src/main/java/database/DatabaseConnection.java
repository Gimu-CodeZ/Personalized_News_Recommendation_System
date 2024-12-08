package database;

import com.example.ood_cw_new.Main;
import model.Article;
import model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseConnection {

    // Database URL, username, and password
    static String url = "jdbc:mysql://localhost:3306/newsapp";
    static String user = "root";
    static String password = "";

    // Connection object
    static Connection connection = null;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private boolean isInsertionComplete = false; // Flag to check if insertion is complete

    public final ConcurrentHashMap<String, List<Article>> userLikedArticlesMap = new ConcurrentHashMap<>();
    private boolean isFetchingComplete = false; // Flag to check if fetching is complete
    private boolean isFetchingPreferencesComplete = false; // Flag to check if fetching is complete

    public static int arraySize;
    public final ConcurrentHashMap<Integer, Map<String, Integer>> userPreferenceMap = new ConcurrentHashMap<>();

    public final ConcurrentHashMap<Integer, List<String>> preferences = new ConcurrentHashMap<>();


    public synchronized boolean isFetchingComplete() {
        return isFetchingComplete;
    }

    public synchronized void setFetchingComplete(boolean fetchingComplete) {
        this.isFetchingComplete = fetchingComplete;
    }

    public synchronized boolean isFetchingPreferencesComplete() {
        return isFetchingPreferencesComplete;
    }

    public synchronized void setFetchingPreferencesComplete(boolean isFetchingPreferencesComplete) {
        this.isFetchingPreferencesComplete = isFetchingPreferencesComplete;
    }

    public synchronized boolean isInsertionComplete() {
        return isInsertionComplete;
    }

    public synchronized void setInsertionComplete(boolean fetchingComplete) {
        this.isInsertionComplete = fetchingComplete;
    }

    public static void dbConnection() {
        try {

            // Establish the connection
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database!");


        } catch (Exception e) {
            e.printStackTrace();
//        } finally {
//            // Close the connection
//            try {
//                if (connection != null) {
//                    connection.close();
//                    System.out.println("Database connection closed.");
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }

        }
    }

    public void insertArticleData(Article article) throws SQLException {

        executorService.submit(() -> {
            dbConnection();

            String checkQuery = "SELECT COUNT(*) FROM articles WHERE article_id = ?";

            String insertQuery = "INSERT INTO articles (article_id,title, author, url, category, published_at, image_url, description,content) VALUES (?,?,?,?,?,?,?,?,?)";

            PreparedStatement checkStatement = null;
            PreparedStatement insertStatement  = null;
            ResultSet resultSet = null;


            try {
                // Check if the article already exists in the database
                checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, article.getArticleId());

                resultSet = checkStatement.executeQuery();

                // If the article exists, skip the insertion
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    System.out.println("Article already exists, skipping insertion.");
//                    insertStatement.close();
                    dbConnectionClose();
                    System.out.println("article section closed............");
                    isInsertionComplete = true;
                    return;  // Skip insertion if the article already exists
                }else {
                    // Parse the datetime
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(article.getPublishAt());

                    // Convert to MySQL-compatible format
                    LocalDateTime mysqlDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
                    String formattedDateTime = mysqlDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    insertStatement  = connection.prepareStatement(insertQuery);

                    insertStatement .setString(1,article.getArticleId());
                    insertStatement .setString(2, article.getTitle());
                    insertStatement .setString(3, article.getAuthor());
                    insertStatement .setString(4, article.getUrl());
                    insertStatement .setString(5, article.getCategory());
                    insertStatement .setString(6, formattedDateTime);
                    insertStatement .setString(7, article.getUrlToImage());
                    insertStatement .setString(8, article.getDescription());
                    insertStatement .setString(9, article.getContent());

                    // Execute the update
                    int rowsInserted = insertStatement .executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("Article inserted successfully!");
                    }
                }

                insertStatement.close();
                checkStatement.close();

            } catch (SQLException e) {
                System.out.println("Can't insert article data");
            }

            isInsertionComplete = true;
            dbConnectionClose();

        });

    }

    public void insertUserData(User user) throws SQLException {

        dbConnection();

        String insertQuery = "INSERT INTO users (username, email, password) VALUES (?,?,?)";


        PreparedStatement statement = connection.prepareStatement(insertQuery);

        statement.setString(1, user.getUsername());
        statement.setString(2, user.getEmail());
        statement.setString(3, user.getPassword());


        // Execute the update
        int rowsInserted = statement.executeUpdate();

        if (rowsInserted > 0) {
            System.out.println("A new user was inserted successfully!");
        }

        statement.close();
        dbConnectionClose();
    }

    public static boolean retrieveUserData(String username, String password) throws SQLException {

        dbConnection();

        String query = "SELECT username, password FROM users WHERE username = ? AND password = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Prepare the query with placeholders
            statement = connection.prepareStatement(query);

            // Set the parameters for username and password
            statement.setString(1, username);
            statement.setString(2, password);

            // Execute the query
            resultSet = statement.executeQuery();

            // Check if a result is returned
            if (resultSet.next()) {
                return true; // User exists with the provided username and password
            } else {
                return false; // No user found
            }
        } finally {
            // Close resources
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionClose(); // Close the database connection
        }

    }

//    public User profileData(String username) throws SQLException {
//
//        dbConnection();
//
//        String query = "SELECT email, password FROM users WHERE username = ?";
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//
//        User user = new User();
//        user.setUsername(username);
//
//        try {
//            // Prepare the query with placeholders
//            statement = connection.prepareStatement(query);
//
//            // Set the parameters for username and password
//            statement.setString(1, username);
//
//            // Execute the query
//            resultSet = statement.executeQuery();
//
//            // Check if a result is returned
//            while (resultSet.next()) {
//                String profEmail = resultSet.getString("email");
//                String profPassword = resultSet.getString("password");
//
//                user.setEmail(profEmail);
//                user.setPassword(profPassword);
//            }
//        } finally {
//            // Close resources
//            if (resultSet != null) resultSet.close();
//            if (statement != null) statement.close();
//            dbConnectionClose(); // Close the database connection
//        }
//
//        return user;
//
//    }

    public static String checkUserDataExistence(String username, String email) throws SQLException {

        dbConnection();

        String query = "SELECT username, email FROM users WHERE username = ? OR email = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Prepare the query with placeholders
            statement = connection.prepareStatement(query);

            // Set the parameters for username and password
            statement.setString(1, username);
            statement.setString(2, email);

            // Execute the query
            resultSet = statement.executeQuery();

            // Check if a result is returned
            if (resultSet.next()) {
                boolean isUsernameMatch = resultSet.getString("username").equals(username);
                boolean isEmailMatch = resultSet.getString("email").equals(email);

                // Determine the match type
                if (isUsernameMatch && isEmailMatch) {
                    return "Both username and email exist";
                } else if (isUsernameMatch) {
                    return "Username exists";
                } else if (isEmailMatch) {
                    return "Email exists";
                }
            }
            return "No match found"; // No user found
        } finally {
            // Close resources
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            dbConnectionClose(); // Close the database connection
        }

    }

    public void insertUserInteraction(String articleId, String username, String interactionType){

        executorService.submit(() -> {

            dbConnection();
            int userId = retrieveUserIdByName(username);

            String checkQuery = "SELECT COUNT(*) FROM user_interactions WHERE user_id = ? && article_id = ? && interaction_type = ?";
            String insertQuery = "INSERT INTO user_interactions (user_id,article_id,interaction_type) VALUES (?,?,?)";

            PreparedStatement checkStatement = null;
            PreparedStatement insertStatement  = null;
            ResultSet resultSet = null;

            try{

                checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setInt(1, userId);
                checkStatement.setString(2, articleId);
                checkStatement.setString(3, interactionType);

                resultSet = checkStatement.executeQuery();

                // If the interaction exists, skip the insertion
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    System.out.println("Interaction already exists, skipping insertion.");
//                    insertStatement.close();
                    checkStatement.close();
                    dbConnectionClose();
                    System.out.println("interaction section closed..............");
                    return;
                }else {
                    insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setInt(1,userId);
                    insertStatement.setString(2, articleId);
                    insertStatement.setString(3,interactionType);

                    int rowsInserted = insertStatement.executeUpdate();

                    if (rowsInserted > 0) {
                        System.out.println("Interaction inserted successfully!");
                    }

                }

                insertStatement.close();
                checkStatement.close();

            }catch (Exception e){
                e.printStackTrace();
//                System.out.println("Can't add interactions");
            }

            dbConnectionClose();
            isInsertionComplete = false;
        });

    }

    public static int retrieveUserIdByName(String username){

        int userId = 0;
        String query = "SELECT user_id FROM users WHERE username = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Prepare the query with placeholders
            statement = connection.prepareStatement(query);

            // Set the parameters for username and password
            statement.setString(1, username);

            // Execute the query
            resultSet = statement.executeQuery();

            // Check if a result is returned
            if (resultSet.next()) {
                userId = resultSet.getInt("user_id");
                return userId;
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Can't select the userid");
        }
        return userId;
    }

    public void deleteArticleData(String articleId, String username, String interactionType) {

        executorService.submit(() -> {
            dbConnection();

            int userId = retrieveUserIdByName(username);

            String checkQuery = "SELECT COUNT(*) FROM user_interactions WHERE user_id = ? AND article_id = ? AND interaction_type = ? ";

            String deleteQuery = "DELETE FROM user_interactions WHERE user_id = ? AND article_id = ? AND interaction_type = ?";
            String deleteArticleQuery = "DELETE FROM articles WHERE article_id = ?";


            PreparedStatement checkStatement = null;
            PreparedStatement deleteStatement = null;
            ResultSet resultSet = null;

            try {
                // Check if the article already exists in the database
                checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setInt(1, userId);
                checkStatement.setString(2, articleId);
                checkStatement.setString(3, interactionType);

                resultSet = checkStatement.executeQuery();

                // If the interaction exists,delete the interaction
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // Prepare the delete statement
                    deleteStatement = connection.prepareStatement(deleteQuery);
                    deleteStatement.setInt(1, userId);
                    deleteStatement.setString(2, articleId);
                    deleteStatement.setString(3, interactionType);

                    // Execute the delete query
                    int rowsDeleted = deleteStatement.executeUpdate();

                    if (rowsDeleted > 0) {
                        System.out.println("User interaction deleted successfully!");
                    } else {
                        System.out.println("No interaction found to delete.");
                    }

                    checkStatement = connection.prepareStatement(checkQuery);
                    checkStatement = connection.prepareStatement(checkQuery);
                    checkStatement.setInt(1, userId);
                    checkStatement.setString(2, articleId);
                    checkStatement.setString(3, "read");

                    resultSet = checkStatement.executeQuery();

                    if (!resultSet.next() && resultSet.getInt(1) > 0) {
                        deleteStatement = connection.prepareStatement(deleteArticleQuery);
                        deleteStatement.setString(1, articleId);

                        int articleRowsDeleted = deleteStatement.executeUpdate();

                        if (articleRowsDeleted > 0) {
                            System.out.println("Article deleted successfully!");
                        } else {
                            System.out.println("No article found to delete.");
                        }
                    }

                }else {
                    System.out.println("No matching interaction found.");
                }

                checkStatement.close();
                deleteStatement.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            dbConnectionClose();
        });
    }

    public void populateHashMap(String username, String interactionType){
        executorService.submit(() -> {
            List<Article> likedArticleList = retrieveInteractedArticles(username,interactionType);

            userLikedArticlesMap.compute(username, (key, existingArticles) -> {
                if (existingArticles == null) {
                    existingArticles = Collections.synchronizedList(new ArrayList<>());
                }
                existingArticles.addAll(likedArticleList); // Add new articles to the user's list
                setFetchingComplete(true);
                return existingArticles;
            });

            System.out.println("Articles liked by user " + username + " updated successfully.");

        });
    }

    public List<Article> retrieveInteractedArticles(String username, String interactionType){
        List<Article> likedArticlesList = Collections.synchronizedList(new ArrayList<>());

        dbConnection();
        String query = "SELECT * FROM articles JOIN user_interactions ON articles.article_id = user_interactions.article_id JOIN users ON user_interactions.user_id = users.user_id WHERE users.username = ? AND user_interactions.interaction_type = ?";

        try {
            // Prepare the query with placeholders
            PreparedStatement statement = connection.prepareStatement(query);

            // Set the parameters for username and password
            statement.setString(1, username);
            statement.setString(2, interactionType);


            // Execute the query
            ResultSet likedArticles = statement.executeQuery();

            while (likedArticles.next()) {
                Article article = new Article();
                article.setArticleId(likedArticles.getString("article_id"));
                article.setTitle(likedArticles.getString("title"));
                article.setAuthor(likedArticles.getString("author"));
                article.setDescription(likedArticles.getString("description"));
                article.setUrl(likedArticles.getString("url"));
                article.setUrlToImage(likedArticles.getString("image_url"));
                article.setPublishAt(likedArticles.getTimestamp("published_at").toString());
                article.setContent(likedArticles.getString("content"));
                article.setCategory(likedArticles.getString("category"));
                likedArticlesList.add(article);
            }

        }catch (Exception e){
            e.printStackTrace();
//            System.out.println("Can't select the userid");
        }

        dbConnectionClose();

        arraySize = likedArticlesList.size();
        return likedArticlesList;
    }

    public void clearLikedArticlesForUser(String username) {
        userLikedArticlesMap.computeIfPresent(username, (key, existingArticles) -> {
            existingArticles.clear();
            setFetchingComplete(false);
            return existingArticles;
        });
    }

    public void fetchingPreferences(String username){

        executorService.submit(() -> {
            dbConnection();

            String query = "SELECT user_interactions.user_id, articles.category, user_interactions.interaction_type FROM user_interactions JOIN articles ON user_interactions.article_id = articles.article_id JOIN users ON users.user_id = user_interactions.user_id WHERE users.username = ?";

            try{
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1,username);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");
                    String category = resultSet.getString("category");
                    String interactionType = resultSet.getString("interaction_type");

                    // Calculate score
                    int score = interactionType.equals("liked") ? 10 : 5;
                    // Update user preferences
                    userPreferenceMap.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).merge(category, score, Integer::sum);

                }

                System.out.println("Data fetched");

            }catch (Exception e){
                System.out.println("Error with fetching data");
            }
            setFetchingPreferencesComplete(true);
            dbConnectionClose();
        });

    }

    public void populatePreference(){
        dbConnection();
        String upsertQuery = "INSERT INTO preferences (user_id, category, score) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE score = VALUES(score)";

        try{
            PreparedStatement statement = connection.prepareStatement(upsertQuery);

            // Iterate over the ConcurrentHashMap
            for (Map.Entry<Integer, Map<String, Integer>> userEntry : userPreferenceMap.entrySet()) {
                int userId = userEntry.getKey();
                System.out.println(userId);
                // Iterate over the categories and scores for the user
                for (Map.Entry<String, Integer> categoryEntry : userEntry.getValue().entrySet()) {
                    String category = categoryEntry.getKey();
                    int score = categoryEntry.getValue();
                    System.out.println(category);
                    System.out.println(score);
                    // Add to the batch
                    statement.setInt(1, userId);
                    statement.setString(2, category);
                    statement.setInt(3, score);
                    statement.addBatch();
                }
            }

            // Execute the batch update
            int[] results = statement.executeBatch();
            System.out.println("Preferences updated for " + results.length + " entries.");
            userPreferenceMap.clear();

        }catch (Exception e){
            System.out.println("Error");
        }
        setFetchingComplete(false);
        dbConnectionClose();
    }

    public List<String> userPreferences(String username){
        dbConnection();
        int userIdForUsername = retrieveUserIdByName(username);
        String query = "SELECT user_id, category, score FROM preferences WHERE user_id = ?";

        try{
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1,userIdForUsername);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String category = resultSet.getString("category");
                int score = resultSet.getInt("score");


                preferences.computeIfAbsent(userId, k -> new ArrayList<>())
                        .add(category); // Add the category as a string
                preferences.get(userId)
                        .add(String.valueOf(score));

            }

        }catch (Exception e){
            System.out.println("Error with fetching data");
        }
        setFetchingPreferencesComplete(true);
        dbConnectionClose();
        return preferences.get(userIdForUsername);
    }

    public static void dbConnectionClose(){
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (Exception ex) {
            System.out.println("Connection can't close");
        }
    }

}
