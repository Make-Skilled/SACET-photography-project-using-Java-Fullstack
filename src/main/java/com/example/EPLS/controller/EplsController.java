package com.example.EPLS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EplsController {
	
	 @GetMapping("/")
	    public String index() {
	        return "index"; // Serves index.html
	    }

	    @GetMapping("/admindashboard")
	    public String adminDashboard() {
	        return "admindashboard"; // Serves admindashboard.html
	    }

	    @GetMapping("/challenges")
	    public String challenges() {
	        return "challenges"; // Serves challenges.html
	    }

	    @GetMapping("/dashboard")
	    public String dashboard() {
	        return "dashboard"; // Serves dashboard.html
	    }

	    @GetMapping("/learning")
	    public String learning() {
	        return "learning"; // Serves learning.html
	    }

	    @GetMapping("/library")
	    public String library() {
	        return "library"; // Serves library.html
	    }

	    @GetMapping("/login")
	    public String login() {
	        return "login"; // Serves login.html
	    }

	    @GetMapping("/profilepage")
	    public String profilePage() {
	        return "profilepage"; // Serves profilepage.html
	    }

	    @GetMapping("/review")
	    public String review() {
	        return "review"; // Serves review.html
	    }

	    @GetMapping("/signup")
	    public String signup() {
	        return "signup"; // Serves signup.html
	    }

	    @GetMapping("/upload")
	    public String upload() {
	        return "upload"; // Serves upload.html
	    }

}
