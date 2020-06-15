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
import java.util.ArrayList;
import java.util.HashMap;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

public class DatabaseMethods {

        String username = "";
        String password = "";
        String fullName = "";
        String fullNameToUpdate = "";

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
                return "User: " + fullNameAdd + " has been added";
        }

        public String viewUsers(Request req, Response resp) throws SQLException {
            Connection conn = connect();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users");
            ResultSet rs = stmt.executeQuery();
            Map<String, Object> model = new HashMap<String, Object>();
            ArrayList<String> nameArray= new ArrayList<String>();
            while (rs.next()) {
                nameArray.add(rs.getString(3));
            }
            model.put("users", nameArray);
            rs.close();
            return new HandlebarsTemplateEngine()
                    .render(new ModelAndView(model, "viewUsers.hbs"));
        }

	public String deleteUserHelper(Request req, Response resp) throws SQLException {

	    String fullNameIn = req.queryParams("fullName");
     
            String SQL = "DELETE FROM users WHERE full_name = '" + fullNameIn + "'";
		
	    DatabaseMethods db = new DatabaseMethods();
            db.connect();

            try (
                Connection conn = connect();

                PreparedStatement pstmt = conn.prepareStatement(SQL);) {
                pstmt.executeUpdate();
                } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                }
	    	db.close();
        	return "User: " + fullNameIn + " has been deleted";
	}

	 public String editGet(Request req, Response resp) throws SQLException {
            fullNameToUpdate = req.params(":fullName");
	    return new HandlebarsTemplateEngine().render(new ModelAndView(fullNameToUpdate, "editUsers.hbs"));
	 }

        public String editPost(Request req, Response resp) throws SQLException {
		
	    DatabaseMethods db = new DatabaseMethods();
            db.connect();

            String usernameUpdate= req.queryParams("username");
            String passwordUpdate = req.queryParams("password");
            String fullNameUpdate = req.queryParams("fullName");
            
            String SQL = "UPDATE users SET username = '" + usernameUpdate + "', password = '" + passwordUpdate
            + "', full_name = '" + fullNameUpdate + "' WHERE username = '" + fullNameUpdate + "'";

            try (
                  Connection conn = connect();
                  PreparedStatement pstmt = conn.prepareStatement(SQL);) {
            int affectedRows = pstmt.executeUpdate();
            } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            }
            db.close();
	    return "User: " + fullNameToUpdate + " has been updated";
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

        public String admin(Request req, Response resp) {
                        Map<String, Object> model = new HashMap<String, Object>();
                        return new HandlebarsTemplateEngine()
                                .render(new ModelAndView(model, "admin.hbs"));
        }

        public void addRoutes() {

                get("/hello", (req, res) -> "Hello World");

                post("/addUser", (req, res) -> addUser(req, res));

                get("/admin", (req, res) -> admin(req, res));

                get("/viewUsers", (req, res) -> viewUsers(req, res)); 

                get("/deleteUserHelper", (req, res) -> deleteUserHelper(req, res));

		get("/editGet/:fullName", (req, res) -> editGet(req, res));
        	
		post("/editPost", (req, res) -> editPost(req, res));
	}
}
     
