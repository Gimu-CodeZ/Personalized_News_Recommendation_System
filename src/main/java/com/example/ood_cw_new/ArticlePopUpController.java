package com.example.ood_cw_new;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.Article;

public class ArticlePopUpController {

    @FXML
    public ScrollPane detailsScrollPane;
    @FXML
    public Label titleLabel;
    @FXML
    public ImageView imageView;
    @FXML
    public VBox headerVbox;

    public void initialize(){
        detailsScrollPane.setFitToWidth(true);
        headerVbox.setAlignment(Pos.CENTER);
        titleLabel.setWrapText(true);

    }

    public void setFullArticle(Article article) {

        titleLabel.setText(article.getTitle());

        try{
            Image image = new Image(article.getUrlToImage());
            imageView.setImage(image);
        } catch (Exception e) {
            Image noImage = new Image("file:/C:/Users/githm/IdeaProjects/OOD_CW_New/src/main/resources/com/example/ood_cw_new/Images/image-not-an-available-icon-vector-53110770.jpg");
            imageView.setImage(noImage);
            System.out.println("Failed to load image");
        }


        VBox detailsBox = new VBox(15);
        detailsBox.setStyle("-fx-padding: 10; -fx-border-radius: 5;");

        // Author Label
        Label authorLabel = new Label("Author - " + article.getAuthor());
        authorLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        authorLabel.setMaxWidth(700);
        authorLabel.setWrapText(true);

        // Description Label
        Label descriptionLabel = new Label(article.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 13px;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(700);

        // Url Label
        Label urlLabel = new Label(article.getUrl());
        urlLabel.setWrapText(true);
        urlLabel.setStyle("-fx-text-fill: blue; -fx-underline: true;");
        urlLabel.setOnMouseClicked(event -> openUrl(article.getUrl()));
        urlLabel.setMaxWidth(700);


        // Date Label
        Label dateLabel = new Label("Published: " + article.getPublishAt());
        dateLabel.setStyle("-fx-font-size: 13px;");

        // Content Label
        Label contentLabel = new Label(article.getContent());
        contentLabel.setStyle("-fx-font-size: 13px;");
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(700);

        detailsBox.getChildren().addAll(authorLabel, descriptionLabel, urlLabel, dateLabel, contentLabel);
        detailsBox.setFillWidth(true);

        detailsScrollPane.setContent(detailsBox);

    }

    private void openUrl(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
