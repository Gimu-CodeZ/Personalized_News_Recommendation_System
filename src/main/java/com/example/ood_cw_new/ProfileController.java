package com.example.ood_cw_new;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.User;

import java.io.File;

public class ProfileController {

    @FXML
    public ImageView profileImage;
    @FXML
    public Label usernameLbl;
    @FXML
    public Label emailLbl;
    @FXML
    public Label passwordLbl;
    @FXML
    public Button usernameEditBtn;
    @FXML
    public Button emailEditBtn;
    @FXML
    public Button passwordEditBtn;
    @FXML
    public Button profileImageEditBtn;

    String filePath;
    User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void initialize(){

//        usernameLbl.setText(getUser().getUsername());
//        emailLbl.setText(getUser().getEmail());
//        passwordLbl.setText(getUser().getPassword());

    }

    public void profileImageEditBtnOnAction(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            filePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.getName());
            profileImage.setImage(image);
        } else {
            Image image = new Image("src/main/resources/com/example/ood_cw_new/Images/blank-profile-picture-973460_1280.jpg");
            profileImage.setImage(image);
        }
    }




}


