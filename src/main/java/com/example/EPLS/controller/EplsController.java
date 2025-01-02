package com.example.EPLS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.EPLS.model.Users;
import com.example.EPLS.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class EplsController {

	@Autowired
	private UserRepository userRepository;

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

	@PostMapping("/register")
	public String registerUser(@ModelAttribute("Users") Users user, Model model) {
		try {
			// Check if email already exists
			if (userRepository.findByEmail(user.getEmail()) != null) {
				model.addAttribute("error", "Email already in use.");
				return "signup"; // Return to the same form with an error message
			}

			// Save the user (password should be hashed in production)
			userRepository.save(user);

			// Add a success message to the model
			model.addAttribute("success", "User registered successfully.");
			return "login"; // Redirect to a success template
		} catch (Exception e) {
			model.addAttribute("error", "An error occurred during registration.");
			return "signup"; // Return to the form with a generic error message
		}
	}
	
	@PostMapping("/login")
	public String loginUser(@RequestParam("email") String email,
	                        @RequestParam("password") String password,
	                        HttpSession session,
	                        Model model) {
	    // Check if it's the admin user
	    if ("admin123@gmail.com".equals(email) && "admin123".equals(password)) {
	        return "admindashboard"; // Redirect to admin dashboard
	    }

	    // Find user by email
	    Users user = userRepository.findByEmail(email);

	    if (user == null || !user.getPassword().equals(password)) {
	        // Authentication failed
	        model.addAttribute("error", "Invalid email or password.");
	        return "login"; // Redirect back to login page with error message
	    }

	    // Authentication succeeded
	    session.setAttribute("userEmail", user.getEmail());
	    model.addAttribute("user", user); // Add user object to model for personalization
	    return "dashboard"; // Redirect to the user dashboard
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
	    session.invalidate(); // Invalidate the session
	    return "redirect:/"; // Redirect to login page
	}

	@GetMapping("/profile")
	public String getProfile(HttpSession session, Model model) {
	    // Retrieve the email from the session
	    String email = (String) session.getAttribute("userEmail");
	    System.out.print("userEmail");

	    if (email == null) {
	        // If no email is found in the session, redirect to the login page
	        return "redirect:/login";
	    }

	    // Fetch the user details from the database
	    Users user = userRepository.findByEmail(email);
	    System.out.println(user.getFullname());

	    // Add user details to the model to display in the Thymeleaf template
	    model.addAttribute("name", user.getFullname());
	    model.addAttribute("email",user.getEmail());
	    return "profilepage"; // Render the profile page
	}


}
