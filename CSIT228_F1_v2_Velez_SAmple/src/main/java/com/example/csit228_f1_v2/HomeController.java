package com.example.csit228_f1_v2;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.example.csit228_f1_v2.HelloApplication.*;

public class HomeController {

    public ToggleButton tbNight;

     public Label lblName;

    public Label lblNew;

    public Label lblConfirm;
    public TextField tfName;
    public PasswordField pfNew;
    public PasswordField pfConfirm;
    public Label lblGreet;
    public  Label lblGrtName;



    public void onNightModeClick() {
        if (tbNight.isSelected()) {
            // Set background color to black and change text to "DAY"
            Pane parentPane = (Pane) tbNight.getParent();
            parentPane.setStyle("-fx-background-color: BLACK");
            tbNight.setText("DAY");

            // Set text color to white for labels
            setLabelsTextColor(Color.WHITE);
        } else {
            // Set background color to white and change text to "NIGHT"
            Pane parentPane = (Pane) tbNight.getParent();
            parentPane.setStyle("-fx-background-color: WHITE");
            tbNight.setText("NIGHT");

            // Set text color to black for labels
            setLabelsTextColor(Color.BLACK);
        }
    }

    private void setLabelsTextColor(Color color) {
        lblName.setTextFill(color);
        lblNew.setTextFill(color);
        lblConfirm.setTextFill(color);
        lblGrtName.setTextFill(color);
        lblGreet.setTextFill(color);
    }
    public void updateNameAndPassword() {
        String newName = tfName.getText();
        String newPassword = pfNew.getText();
        String confirmPassword = pfConfirm.getText();

        // Get the currently authenticated username (replace this with your actual authentication logic)
        String currentUsername = getCurrentUsername();

        if (currentUsername != null && !currentUsername.isEmpty()) {
            // Update name and password in the database for the current user
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String updateQuery = "UPDATE account SET name = ?, password = ? WHERE username = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                    pstmt.setString(1, newName);
                    pstmt.setString(2, newPassword);
                    pstmt.setString(3, currentUsername);
                    int rowsUpdated = pstmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Name and password updated successfully!");
                        lblGrtName.setText(newName); // Update lblGrtName with new name
                    } else {
                        System.out.println("Failed to update name and password.");
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error updating name and password: " + e.getMessage());
            }
        } else {
            System.out.println("Current username not available or invalid.");
        }
    }





    public void onSaveClick(ActionEvent actionEvent) {
        updateNameAndPassword();
    }
    private void deleteUser() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String deleteQuery = "DELETE FROM account WHERE username = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
                pstmt.setString(1, getCurrentUsername());
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("User data deleted successfully!");
                    // Optionally, reset UI components after deletion
                } else {
                    System.out.println("Failed to delete user data.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user data: " + e.getMessage());
        }
    }
    public void onDeleteClick(ActionEvent actionEvent) {
        deleteUser();
    }
    private String getCurrentUsername() {
        // Implement your authentication logic here to retrieve the current username
        // For demonstration purposes, let's assume you have a method to authenticate
        // and retrieve the username based on session or token.

        // Example: Fetching username from authenticated session
        String authenticatedUsername = AuthenticationService.getAuthenticatedUsername();

        // Alternatively, you could directly retrieve it from the database if you have
        // an active session or token linked to the username.

        // You need to replace AuthenticationService.getAuthenticatedUsername() with
        // your actual authentication logic.

        return authenticatedUsername;
    }
}

