package com.example.ood_cw_new;

import database.DatabaseConnection;
import database.UserPreferenceProcessor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Article;
import model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class SignInController {

    @FXML
    public VBox signUpVbox;
    @FXML
    public VBox signinVbox;
    @FXML
    public TextField userNameSignUp;
    @FXML
    public PasswordField passwordSignUp;
    @FXML
    public TextField emailSignUp;
    @FXML
    public TextField userNameSignIn;
    @FXML
    public PasswordField passwordSignIn;
    @FXML
    public Button logInMoveBtn;
    @FXML
    public Button signUpBtn;
    @FXML
    public Button logInBtn;
    @FXML
    public Button signUpMoveBtn;
    @FXML
    public Label errorLabel;


    public void signUpMoveBtnOnAction(ActionEvent event) {
        signUpVbox.setVisible(true);
        signinVbox.setVisible(false);
    }

    public void signInMoveBtnOnAction(ActionEvent event) {
        signinVbox.setVisible(true);
        signUpVbox.setVisible(false);
    }


    private void loadHomePage(ActionEvent event, String username) {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("homePage.fxml"));
            Parent root = loader.load();

            // Pass the username to the controller
            HomePageController homePageController = loader.getController();
            homePageController.setUsername(username);

            //Pass the username for preferenceProcess
//            UserPreferenceProcessor userPreferenceProcessor = loader.getController();
//            userPreferenceProcessor.setUsername(username);

            // Get the current stage and close it
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            // Create and show the new stage
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();
            newStage.setResizable(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void loginBtnOnAction(ActionEvent event) throws SQLException {
        String username = userNameSignIn.getText();
        String password = passwordSignIn.getText();

        try{
            boolean isExist = DatabaseConnection.retrieveUserData(username,password);
            if (isExist) {
                loadHomePage(event, username);
                System.out.println("User found");
            }else {
                userNameSignIn.clear();
                passwordSignIn.clear();
                showWarning("No user exist");
                System.out.println("No user exist");
            }
        }catch (Exception e){
//            e.printStackTrace();
            System.out.println("Can't find user");
        }

    }

    public void signUpBtnOnAction(ActionEvent event) {

        boolean isValid = validateInputs();

        if (isValid) {
            User user = new User();
            user.setUsername(userNameSignUp.getText());
            user.setEmail(emailSignUp.getText());
            user.setPassword(passwordSignUp.getText());

            try{
                DatabaseConnection connection = new DatabaseConnection();
                connection.insertUserData(user);
                showMessage("You Successfully Registered!");
                signUpVbox.setVisible(false);
                signinVbox.setVisible(true);
            }catch (Exception e){
                System.out.println("user already exist");
            }

        }else {
            System.out.println("Can't insert user...Inputs are not valid");
        }

    }

    public boolean validateInputs() {
        String username = userNameSignUp.getText();
        String email = emailSignUp.getText();
        String passwordSignUpText = passwordSignUp.getText();

        // Check existence of username and email
        try{
            String existenceResult = DatabaseConnection.checkUserDataExistence(username, email);
            switch (existenceResult) {
                case "Both username and email exist":
                    userNameSignUp.setStyle("-fx-border-color: #8c1e1e;");
                    emailSignUp.setStyle("-fx-border-color: #8c1e1e;");
                    errorLabel.setText("Username and email already exists");
                    System.out.println("User with both username and email already exists");
                    return false;
                case "Username exists":
                    userNameSignUp.setStyle("-fx-border-color: #8c1e1e;");
                    errorLabel.setText("Username already exists");
                    System.out.println("Username already exists");
                    return false;
                case "Email exists":
                    emailSignUp.setStyle("-fx-border-color: #8c1e1e;");
                    errorLabel.setText("Email already exists");
                    System.out.println("Email already exists");
                    return false;
                case "No match found":
                    System.out.println("User does not exist, proceeding with registration...");
                    break; // Proceed with further logic
                default:
                    throw new IllegalStateException("Unexpected value: " + existenceResult);
            }
        }catch (Exception e){
            System.out.println("Database connection error");
        }


        // Validate email
        if (!isValidEmail(email)) {
            emailSignUp.setStyle("-fx-border-color: #8c1e1e;");
            errorLabel.setText("Invalid email! Must end with @gmail.com");
            System.out.println("Invalid email! Must end with @gmail.com");
            return false;
        }

        // Validate password
        String validationMessage = getPasswordValidationMessage(passwordSignUpText);

        if (!validationMessage.isEmpty()) {
            passwordSignUp.setStyle("-fx-border-color: #8c1e1e;");
            errorLabel.setText(validationMessage); // Set the validation message to the Label
            return false;
        }

        // Success
        errorLabel.setText("");
        userNameSignUp.setStyle("-fx-border-color: transparent");
        emailSignUp.setStyle("-fx-border-color: transparent");
        passwordSignUp.setStyle("-fx-border-color: transparent");
        System.out.println("Validation successful!");
        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        return email.matches(emailRegex);
    }


    private String getPasswordValidationMessage(String password) {
        StringBuilder validationMessage = new StringBuilder();

        // Check for minimum length
        if (password.length() < 8) {
            validationMessage.append("Must be at least 8 characters.\n");
        }

        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            validationMessage.append("Must include an uppercase letter.\n");
        }

        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            validationMessage.append("Must include a lowercase letter.\n");
        }

        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            validationMessage.append("Must include a digit.\n");
        }

        // Check for at least one special character
        if (!password.matches(".*[@#$^&+=!].*")) {
            validationMessage.append("Must include a special character (e.g., @#$^&+=!).\n");
        }

        return validationMessage.toString();
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
//        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        Optional<ButtonType> result = alert.showAndWait();
    }
    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        Optional<ButtonType> result = alert.showAndWait();
    }


}