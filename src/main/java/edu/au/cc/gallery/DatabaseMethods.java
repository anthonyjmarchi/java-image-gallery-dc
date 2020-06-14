package edu.au.cc.gallery;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.DriverManager;
import java.sql.Connection;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import static spark.Spark.*;
import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.HashMap;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

public class DatabaseMethods {

        String username = "";
        String password = "";
        String fullName = "";
        
        private static final String dbUrl = "jdbc:postgresql://image-gallery.cm6ntogsarqg.us-east-1.rds.amazonaws.com/";

        private Connection connection;

        public Connection connect() throws SQLException {

            try {
                    Class.forName("org.postgresql.Driver");
                    connection = DriverManager.getConnection(dbUrl, "image_gallery", getPassword());
                    return connection;
            }
            catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                    System.exit(1);
            }
            return connection;
        }

        public void close() throws SQLException {
            connection.close();
        }

	private String getPassword() {
            try(BufferedReader br = new BufferedReader(new FileReader("/home/ec2-user/.sql-passwd"))) {
            String result = br.readLine();
            br.close();
            return result;
            } catch (IOException ex) {
                    System.err.println("Error opening password file. Make sure.sql-passwd exists");
                    System.exit(1);
            }
            return null;
        }

        public String addUser(Request req, Response resp) throws SQLException {
            
            String usernameAdd = req.queryParams("username");
            String passwordAdd = req.queryParams("password");
            String fullNameAdd = req.queryParams("fullName");
            
            DatabaseMethods db = new DatabaseMethods();
            db.connect();

            String SQL = "INSERT INTO users(username, password, full_name) " + "VALUES(?,?,?)";

            long id = 0;
    
            try (
                    Connection conn = connect();
                    PreparedStatement pstmt = conn.prepareStatement(SQL);) {
    
                    pstmt.setString(1, usernameAdd);
                    pstmt.setString(2, passwordAdd);
                    pstmt.setString(3, fullNameAdd);
    
                    int affectedRows = pstmt.executeUpdate();
                } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                    }
                db.close();
                return "User: " + req.queryParams("fullNameAdd") + " has been added";
        }

        public String admin(Request req, Response resp) {
                        Map<String, Object> model = new HashMap<String, Object>();
                        return new HandlebarsTemplateEngine()
                                .render(new ModelAndView(model, "admin.hbs"));
        }

        public void addRoutes() {

                get("/hello", (req, res) -> "Hello World");

                post("/addUser", (req, res) -> addUser(req, res));

                get("/admin", (req, res) -> admin(req, res));

                get("/viewUsers", (req, res) -> {
                        Map<String, Object> model = new HashMap<String, Object>();
                        return new HandlebarsTemplateEngine()
                                .render(new ModelAndView(model, "viewUsers.hbs"));
                });
        }
}