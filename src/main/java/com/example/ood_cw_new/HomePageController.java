package com.example.ood_cw_new;

import database.DatabaseConnection;
import database.UserPreferenceProcessor;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Article;
import model.User;
import services.NewsFetcher;
import services.RecommendationEngine;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


public class HomePageController {

    @FXML
    public Button forYouBtn;
    @FXML
    public Button likedBtn;
    @FXML
    public Button profileBtn;

    @FXML
    public ScrollPane scrollPane;
    @FXML
    public GridPane gridPane;
    @FXML
    public ImageView forYouImg;
    @FXML
    public ImageView likeImg;
    @FXML
    public Pane backPane;
    @FXML
    public HBox pageBox;
    @FXML
    public Button backwardBtn;
    @FXML
    public Button forwardBtn;
    @FXML
    public Label pageLabel;
    @FXML
    public GridPane mainGridPane;
    @FXML
    public ScrollPane mainScrollPane;
    @FXML
    public Button businessCatBtn;
    @FXML
    public Button healthCatBtn;
    @FXML
    public Button entCatBtn;
    @FXML
    public Button sportsCatBtn;
    @FXML
    public Button technoCatBtn;
    @FXML
    public Button scienceCatBtn;
    @FXML
    public Button homeBtn;
    @FXML
    public ImageView homeImg;

    private String username;
    private int currentPage = 1;
    private String currentCategory;
    private String interactionType;
    public boolean isHomeBtnClicked = false;
    boolean isLikedTabOpen = false;
    List<String> topCategories;
    NewsFetcher newsFetcher = new NewsFetcher();
    DatabaseConnection databaseConnection = new DatabaseConnection();
    UserPreferenceProcessor processor = new UserPreferenceProcessor();
    RecommendationEngine recommendationEngine = new RecommendationEngine();


    public void initialize() {
        scrollPane.setFitToWidth(true);
        isHomeBtnClicked = true;

        Platform.runLater(() -> {
            if (getUsername() != null) {
                processor.startBatchProcessing(getUsername());
            }
        });

    }

    // Method to set the username
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    private void populateGridPane(List<Article> articles) {

        gridPane.getChildren().clear();
        int column = 0;
        int row = 0;

//        List<Article> articles = newsFetcher.userArticlesMap.get(username);

        for (Article article : articles) {
            VBox articleBox;
            if (article.getTitle().equals("[Removed]")) {
                continue;
            } else {
                articleBox = articleBoxSummeryView(
                        article.getUrlToImage(),
                        article.getTitle(),
                        article.getAuthor(),
                        article
                );
            }

            gridPane.add(articleBox, column, row); // Add the article to the GridPane

            column++;
            if (column == 2) { // Two articles per row
                column = 0;
                row++;
            }

        }

    }

    // Create an individual article cell
    private VBox articleBoxSummeryView(String urlToImage, String title, String author, Article article) {
        VBox articleBox = new VBox();
        articleBox.setStyle("-fx-padding: 10; -fx-border-color: #26585e; -fx-border-radius: 5; -fx-border-width: 2px");

        // Title Label
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(400);

        // Author Label
        Label authorLabel = new Label(author);
        authorLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        authorLabel.setWrapText(true);
        authorLabel.setPrefWidth(230);


        // ImageView for the article image
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(urlToImage, true); // Load the image lazily
            imageView.setImage(image);
            imageView.setFitWidth(350); // Set the desired width
//            imageView.setPreserveRatio(true);
            imageView.setFitHeight(200);
            imageView.maxHeight(250);
        } catch (Exception e) {
            Image noImage = new Image("file:/C:/Users/githm/IdeaProjects/OOD_CW_New/src/main/resources/com/example/ood_cw_new/Images/image-not-an-available-icon-vector-53110770.jpg");
            imageView.setImage(noImage);
            imageView.setFitWidth(350); // Set the desired width
//            imageView.setPreserveRatio(true);
            imageView.setFitHeight(200);
            imageView.maxHeight(250);
            System.out.println("Failed to load image: " + urlToImage);
        }

        VBox imageBox = new VBox();
        imageBox.setStyle("-fx-padding: 10; -fx-border-color: #26585e; -fx-border-width: 2px; -fx-border-radius: 5;");
        imageBox.setPrefWidth(400);
        imageBox.setPrefHeight(350);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.getChildren().addAll(imageView);


        boolean[] isLiked = {false};

        ImageView likeButtonImageView = new ImageView();
        if (isLikedTabOpen){
            Image likeBtnImage = new Image("file:/C://Users//githm//IdeaProjects//OOD_CW_New//src//main//resources//com//example//ood_cw_new//Images//like fill green.png");
            likeButtonImageView.setImage(likeBtnImage);
            isLiked[0] = !isLiked[0];
        }else {
            Image unlikeBtnImage = new Image("file:/C://Users//githm//IdeaProjects//OOD_CW_New//src//main//resources//com//example//ood_cw_new//Images//like normal green.png");
            likeButtonImageView.setImage(unlikeBtnImage);
        }
        likeButtonImageView.setFitWidth(20);
        likeButtonImageView.setFitHeight(20);

        Button likeBtn = new Button();
        likeBtn.setGraphic(likeButtonImageView);
        likeBtn.setStyle("-fx-background-color: transparent; -fx-border-color: none; -fx-padding: 5;");

        likeBtn.setOnAction(actionEvent -> {
            ImageView newLikeButtonImageView = new ImageView();
            if (isLiked[0]) {
                Image unlikedImage = new Image("file:/C://Users//githm//IdeaProjects//OOD_CW_New//src//main//resources//com//example//ood_cw_new//Images//like normal green.png");
                newLikeButtonImageView.setImage(unlikedImage);
                databaseConnection.deleteArticleData(article.getArticleId(), getUsername(), "liked");
            }else {
                interactionType = "liked";
                Image likedImage = new Image("file:/C://Users//githm//IdeaProjects//OOD_CW_New//src//main//resources//com//example//ood_cw_new//Images//like fill green.png");
                newLikeButtonImageView.setImage(likedImage);
                try{
                    databaseConnection.insertArticleData(article);
                    insertionThread(article.getArticleId(), interactionType);
                }catch (Exception e){
                    System.out.println("Can't add article details");
                }
            }
            newLikeButtonImageView.setFitWidth(20);
            newLikeButtonImageView.setFitHeight(20);

            likeBtn.setGraphic(newLikeButtonImageView);

            isLiked[0] = !isLiked[0];
        });

        Button readMoreBtn = new Button();
        readMoreBtn.setText("Read more");
        readMoreBtn.setStyle("-fx-background-color: #26585e; -fx-border-color: none; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 12px;");
        readMoreBtn.setWrapText(true);
        readMoreBtn.setPrefWidth(100);
        readMoreBtn.setOnAction(actionEvent -> {
            interactionType = "read";
            openArticleDetails(article);
            try{
                databaseConnection.insertArticleData(article);
                insertionThread(article.getArticleId(), interactionType);
            }catch (Exception e){
                System.out.println("Can't add article details");
            }

        });


        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(likeBtn, readMoreBtn);
        buttonBox.setStyle("-fx-padding: 5;");
//        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        HBox activityBox = new HBox(10);
        activityBox.getChildren().addAll(authorLabel, buttonBox);
//        activityBox.setStyle("-fx-padding: 5;");
        activityBox.setMaxWidth(400);
        activityBox.setAlignment(Pos.CENTER);

        articleBox.getChildren().addAll(imageBox, titleLabel, activityBox);
        return articleBox;
    }

    private void openArticleDetails(Article article){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("articlePopUp.fxml"));
            Parent root = loader.load();

            //Pass article data to the new controller
            ArticlePopUpController popUpController = loader.getController();
            popUpController.setFullArticle(article);

            // Open the new window
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void forYouBtnOnAction(ActionEvent event) {
        isLikedTabOpen = false;
        currentPage = 1;
        forYouBtn.setStyle("-fx-background-color: #497b90");
        if (isHomeBtnClicked){
            mainGridPane.setVisible(false);
            scrollPane.setVisible(true);
            pageBox.setVisible(true);
        }
        likedBtn.setStyle("-fx-background-color: #26585e");
        gridPane.getChildren().clear();
        topCategories = recommendationEngine.recommendCategories(getUsername());
        for (String category : topCategories){
            newsFetcher.request(getUsername(),"top-headlines", "us", category, currentPage, 20);
            loopWait();
            gridThreadMethod();
        }


    }

    public void likedBtnOnAction(ActionEvent event) {
        isLikedTabOpen = true;
        currentPage = 1;
        likedBtn.setStyle("-fx-background-color: #497b90");
        if (isHomeBtnClicked){
            mainGridPane.setVisible(false);
            scrollPane.setVisible(true);
            pageBox.setVisible(true);
        }
        gridPane.getChildren().clear();
        pageBoxSetup();
        databaseConnection.populateHashMap(getUsername(),"liked");
        likedGridThread();
    }

    public void profileBtnOnAction(ActionEvent event) throws SQLException {
        isLikedTabOpen = false;
//        User user = databaseConnection.profileData(getUsername());
        if (isHomeBtnClicked){
            loadProfilePage(event);
        }else {
            isHomeBtnClicked = true;
            mainGridPane.setVisible(true);
            pageBoxReSet();
            scrollPane.setVisible(false);
            pageBox.setVisible(false);
            gridPane.getChildren().clear();
            newsFetcher.clearArticlesForUser(getUsername());
            databaseConnection.clearLikedArticlesForUser(getUsername());
            loadProfilePage(event);
        }

    }

    public void forwardBtnOnAction(ActionEvent event){
        if (NewsFetcher.arraySize>0){
            currentPage++;
            newsFetcher.clearArticlesForUser(getUsername());
            newsFetcher.request(getUsername(),"top-headlines", "us", currentCategory, currentPage, 20);
            gridThreadMethod();
            updatePaginationButtons();
        }
    }

    public void backwardBtnOnAction(ActionEvent event){
        if (currentPage > 1){
            currentPage--;
            newsFetcher.clearArticlesForUser(getUsername());
            newsFetcher.request(getUsername(),"top-headlines", "us", currentCategory, currentPage, 20);
            gridThreadMethod();
            updatePaginationButtons();
        }
    }

    private void updatePaginationButtons() {
        backwardBtn.setVisible(currentPage>1);
        forwardBtn.setVisible(NewsFetcher.arraySize>0);
        pageLabel.setText("Page " + currentPage);
    }

    @FXML
    private void businessCatBtnOnAction(ActionEvent event){
        isHomeBtnClicked = false;
        currentCategory = "business";
        gridVisibility();
        try{
//            newsFetcher.clearArticlesForUser(getUsername());
            newsFetcher.request(getUsername(),"top-headlines", "us", "business", currentPage, 20);
            gridThreadMethod();

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error with news fetching");
        }

        System.out.println("business button clicked");
    }

    @FXML
    private void entCatBtnOnAction(ActionEvent event){
        isHomeBtnClicked = false;
        currentCategory = "entertainment";
        gridVisibility();
        try{
//            newsFetcher.clearArticlesForUser(getUsername());
            newsFetcher.request(getUsername(),"top-headlines", "us", "entertainment", currentPage, 20);
            gridThreadMethod();

        }catch (Exception e){
            System.out.println("Error with news fetching");
        }        System.out.println("entertainment button clicked");
    }

    @FXML
    private void healthCatBtnOnAction(ActionEvent event){
        isHomeBtnClicked = false;
        currentCategory = "health";
        gridVisibility();
        try{
//            newsFetcher.clearArticlesForUser(getUsername());
            newsFetcher.request(getUsername(),"top-headlines", "us", "health", currentPage, 20);
            gridThreadMethod();

        }catch (Exception e){
            System.out.println("Error with news fetching");
        }        System.out.println("health button clicked");
    }

    @FXML
    private void sportsCatBtnOnAction(ActionEvent event){
        isHomeBtnClicked = false;
        currentCategory = "sports";
        gridVisibility();
        try{
//            newsFetcher.clearArticlesForUser(getUsername());
            newsFetcher.request(getUsername(),"top-headlines", "us", "sports", currentPage, 20);
            gridThreadMethod();

        }catch (Exception e){
            System.out.println("Error with news fetching");
        }        System.out.println("sports button clicked");
    }

    @FXML
    private void technoCatBtnOnAction(ActionEvent event){
        isHomeBtnClicked = false;
        currentCategory = "technology";
        gridVisibility();
        try{
//            newsFetcher.clearArticlesForUser(getUsername());
            newsFetcher.request(getUsername(),"top-headlines", "us", "technology", currentPage, 20);
            gridThreadMethod();

        }catch (Exception e){
            System.out.println("Error with news fetching");
        }        System.out.println("technology button clicked");
    }

    @FXML
    private void scienceCatBtnOnAction(ActionEvent event){
        isHomeBtnClicked = false;
        currentCategory = "science";
        gridVisibility();
        try{
//            newsFetcher.clearArticlesForUser(getUsername());
            newsFetcher.request(getUsername(),"top-headlines", "us", "science", currentPage, 20);
            gridThreadMethod();

        }catch (Exception e){
            System.out.println("Error with news fetching");
        }
        System.out.println("science button clicked");
    }

    @FXML
    public void homeBtnOnAction(ActionEvent event){
        isHomeBtnClicked = true;
        isLikedTabOpen = false;
        likedBtn.setStyle("-fx-background-color: #26585e");
        forYouBtn.setStyle("-fx-background-color: #26585e");
        pageBoxReSet();
        scrollPane.setVisible(false);
        pageBox.setVisible(false);
        mainGridPane.setVisible(true);
//        newsFetcher.clearArticlesForCategory(getUsername());
        newsFetcher.clearArticlesForUser(getUsername());
        databaseConnection.clearLikedArticlesForUser(getUsername());
        gridPane.getChildren().clear(); // Clear any previous content
    }

    public void gridVisibility(){
        mainGridPane.setVisible(false);
        scrollPane.setVisible(true);
        pageBox.setVisible(true);
    }

    public void gridThreadMethod(){
        // Use a separate thread to monitor the fetching status
        Thread checkFetchThread = new Thread(() -> {
            while (!newsFetcher.isFetchingComplete()) {
                try {
                    // Poll every 100ms to check if fetching is complete
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Once data fetching is complete, update the UI
            Platform.runLater(() -> {
                populateGridPane(newsFetcher.userArticlesMap.get(username)); // First method
                updatePaginationButtons();    // Second method
            });
        });

        checkFetchThread.start(); // Start the polling thread
    }

    public void loopWait() {
        while (!newsFetcher.isFetchingComplete()) {
            try {
                // Poll every 100ms to check if fetching is complete
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void likedGridThread(){
        // Use a separate thread to monitor the fetching status
        Thread checkFetchThread = new Thread(() -> {
            while (!databaseConnection.isFetchingComplete()) {
                try {
                    // Poll every 100ms to check if fetching is complete
                    Thread.sleep(100);
                    System.out.println("sleeping");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Once data fetching is complete, update the UI
            Platform.runLater(() -> {
                populateGridPane(databaseConnection.userLikedArticlesMap.get(username)); // First method
            });
        });

        checkFetchThread.start(); // Start the polling thread
    }

    public void insertionThread(String article, String interactionType){
        // Use a separate thread to monitor the fetching status
        Thread checkInsertion = new Thread(() -> {
            while (!databaseConnection.isInsertionComplete()) {
                try {
                    // Poll every 100ms to check if fetching is complete
                    Thread.sleep(100);
//                    System.out.println("sleeping");
                } catch (InterruptedException e) {
                    System.out.println("Not waiting");
                }
            }

            Platform.runLater(() -> {
                databaseConnection.insertUserInteraction(article,getUsername(),interactionType);
            });
        });

        checkInsertion.start(); // Start the polling thread
    }

    public void pageBoxSetup(){
        pageBox.setStyle("-fx-background-color: #AFACACFF");
        forwardBtn.setVisible(false);
        backwardBtn.setVisible(false);
        pageLabel.setPrefWidth(190);
        pageLabel.setText("Stay Informed.. Stay Ahead..");
    }

    public void pageBoxReSet(){
        pageBox.setStyle("-fx-background-color: white");
        forwardBtn.setVisible(true);
        backwardBtn.setVisible(true);
        pageLabel.setPrefWidth(60);
        pageLabel.setText("Page");
    }

    public void loadProfilePage(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Parent root = loader.load();

            ProfileController profileController = loader.getController();
//            profileController.setUser(user);

            // Open the new window
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
