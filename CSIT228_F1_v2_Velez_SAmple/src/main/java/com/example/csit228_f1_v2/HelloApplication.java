package com.example.csit228_f1_v2;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class HelloApplication extends Application {
    static final String DB_URL = "jdbc:mysql://localhost:3306/dbaccount";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "";
    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        Text txtWelcome = new Text("Welcome to CIT");
        txtWelcome.setFont(Font.font("Chiller", FontWeight.EXTRA_BOLD, 69));
        txtWelcome.setFill(Color.RED);
//        grid.setAlignment();
        grid.setPadding(new Insets(20));
//        grid.
        txtWelcome.setTextAlignment(TextAlignment.CENTER);
        grid.add(txtWelcome, 0, 0, 3, 1);

        Label lbUsername = new Label("Username: ");
        lbUsername.setTextFill(Color.LIGHTSKYBLUE);
        lbUsername.setFont(Font.font(30));
        grid.add(lbUsername, 0, 1);

        TextField tfUser = new TextField();
        grid.add(tfUser, 1, 1);
        tfUser.setFont(Font.font(30));
//        tfUsername.setMaxWidth(150);

        Label lbPassword = new Label("Password");
        lbPassword.setFont(Font.font(30));
        lbPassword.setTextFill(Color.CHARTREUSE);
        grid.add(lbPassword, 0, 2);

        PasswordField pfPass = new PasswordField();
        pfPass.setFont(Font.font(30));
        grid.add(pfPass, 1, 2);


        Button btnRegister = new Button("Register");
        btnRegister.setFont(Font.font(20));
        grid.add(btnRegister, 0, 8, 2, 1);

        ToggleButton btnShow = new ToggleButton("( )");
//        btnShow.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                if (btnShow.isSelected()) {
//                    tmpPassword.setText(pfPassword.getText());
//                    tmpPassword.setVisible(true);
//                } else {
//                    tmpPassword.setVisible(false);
//                    pfPassword.setText(tmpPassword.getText());
//                }
//            }
//        });


        Button btnLogin = new Button("Log In");
        btnLogin.setFont(Font.font(40));
        grid.add(btnLogin, 0, 3, 2, 1);

        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Hello");
                try {
                    Parent p = FXMLLoader.load(getClass().getResource("homepage.fxml"));
                    Scene s = new Scene(p);
                    stage.setScene(s);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btnLogin.setOnAction(event -> {
            String username = tfUser.getText();
            String password = pfPass.getText();

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                if (conn != null) {
                    System.out.println("Database connection established successfully!");

                    // Create 'account' table if not exists
                    createAccountTable(conn);

                    // Validate username and password against 'account' table
                    if (validateLogin(conn, username, password)) {
                        System.out.println("Login successful!");
                        // Perform actions after successful login (e.g., navigate to homepage)
                        try {
                            Parent p = FXMLLoader.load(getClass().getResource("homepage.fxml"));
                            Scene s = new Scene(p);
                            stage.setScene(s);
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Invalid username or password!");
                    }
                } else {
                    System.out.println("Failed to establish database connection!");
                }
            } catch (SQLException e) {
                System.err.println("Error connecting to the database: " + e.getMessage());
            }
        });
        btnRegister.setOnAction(event -> {
            String Username = tfUser.getText();
            String Password = pfPass.getText();


            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                if (conn != null) {
                    System.out.println("Database connection established successfully!");

                    // Insert new user into 'account' table
                    if (insertUser(conn, Username, Password)) {
                        System.out.println("Registration successful!");
                        // Clear registration fields after successful registration
                        tfUser.clear();
                        pfPass.clear();

                    } else {
                        System.out.println("Failed to register user.");
                    }
                } else {
                    System.out.println("Failed to establish database connection!");
                }
            } catch (SQLException e) {
                System.err.println("Error connecting to the database: " + e.getMessage());
            }
        });

        Scene scene = new Scene(grid, 700, 500, Color.BLACK);
        stage.setScene(scene);
        scene.setFill(Color.CORNFLOWERBLUE);
        stage.show();
        txtWelcome.minWidth(grid.getWidth());
    }
    private void createAccountTable(Connection conn) throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS account (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "username VARCHAR(25) NOT NULL," +
                "name VARCHAR(50) ,"+
                "password VARCHAR(100) NOT NULL CHECK(" +
                "    password REGEXP '[A-Z]' AND " + // At least one uppercase letter
                "    password REGEXP '[a-z]' AND " + // At least one lowercase letter
                "    password REGEXP '[0-9]' AND " + // At least one digit
                "    LENGTH(password) >= 8" +        // Minimum length of 8 characters
                "))";

        try (Statement statement = conn.createStatement()) {
            statement.execute(query);
            System.out.println("Successfully created the table 'account'");
        } catch (SQLException e) {
            System.err.println("Error creating table 'account': " + e.getMessage());
        }
    }

    private boolean validateLogin(Connection conn, String username, String password) throws SQLException {
        String query = "SELECT * FROM account WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet resultSet = pstmt.executeQuery();
            return resultSet.next(); // Return true if a matching user is found
        }
    }
    private boolean insertUser(Connection conn, String username, String password) throws SQLException {
        String query = "INSERT INTO account (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Return true if insertion was successful
        }
    }

    public static void main(String[] args) {
        launch();
    }
}