package edu.au.cc.gallery;

import static spark.Spark.*;
import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.HashMap;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

public class DatabaseMethods {

	public String addUser(Request req, Response resp) {
		return "User: " + req.queryParams("fname") + " " + req.queryParams("lname") + " has been added"; 
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
