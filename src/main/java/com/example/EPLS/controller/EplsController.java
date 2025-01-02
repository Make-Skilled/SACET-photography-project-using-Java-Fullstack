package com.example.EPLS.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.EPLS.model.Images;
import com.example.EPLS.model.Users;
import com.example.EPLS.repository.ImagesRepository;
import com.example.EPLS.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class EplsController {

	@Value("${upload.path:src/main/resources/static/uploads}")
	private String uploadDir;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ImagesRepository imagesRepository;

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
	public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpSession session, Model model) {
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
		model.addAttribute("email", user.getEmail());
		return "profilepage"; // Render the profile page
	}

	@PostMapping("/upload")
	public String uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("title") String title,
			@RequestParam("description") String description, HttpSession session, // To retrieve the user who uploaded
																					// the image
			Model model) {

		// Check if the file is empty
		if (file.isEmpty()) {
			model.addAttribute("error", "Please select an image to upload.");
			return "upload"; // Return to the upload page with an error
		}

		// Retrieve the user email from the session (assuming the user is logged in)
		String uploadedBy = (String) session.getAttribute("userEmail");
		if (uploadedBy == null) {
			model.addAttribute("error", "You need to be logged in to upload an image.");
			return "upload"; // Redirect back if user is not logged in
		}

		// Log the title and description for debugging
		System.out.println("Title: " + title);
		System.out.println("Description: " + description);

		try {
			// Resolve the upload directory from application.properties
			File uploadDirectory = new File(uploadDir);
			if (!uploadDirectory.exists()) {
				uploadDirectory.mkdirs(); // Create the directory if it doesn't exist
			}

			// Save the file to the directory
			String filePath = uploadDirectory.getAbsolutePath() + "/" + file.getOriginalFilename();
			file.transferTo(new File(filePath));

			// Save the metadata to the database
			Images image = new Images(file.getOriginalFilename(), title, description, uploadedBy);
			imagesRepository.save(image); // Save image metadata to the database

			// Add success message to the model
			model.addAttribute("success", "Image uploaded successfully: /uploads/" + file.getOriginalFilename());
			return "upload"; // Return to the upload page with success message
		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("error", "An error occurred during file upload.");
			return "upload"; // Return to the upload page with error message
		}
	}
	
	@GetMapping("/library")
	public String gallery(HttpSession session, Model model) {
	    // Retrieve the user email from the session
	    String user = (String) session.getAttribute("userEmail");
	    System.out.print("user");

	    // Check if user is not logged in
	    if (user == null) {
	        return "redirect:/login"; // Redirect to login page if not logged in
	    }

	    // Fetch the images uploaded by the logged-in user from the repository
	    List<Images> gallery = imagesRepository.findByUploadedBy(user);

	    // Add the gallery images to the model
	    model.addAttribute("libraryItems", gallery);

	    // Return the gallery page view
	    return "library"; // This assumes you have a Thymeleaf template named gallery.html
	}
	
	 @PostMapping("/deleteLibraryItem/{id}")
	    public String deleteLibraryItem(@PathVariable("id") Long id) {
	        try {
	            // Check if the item exists
	            if (imagesRepository.existsById(id)) {
	                // Delete the item from the database using the repository
	                imagesRepository.deleteById(id);
	            } else {
	                // If the item does not exist, handle accordingly (optional, you can redirect or show an error)
	                return "redirect:/gallery?error=ItemNotFound";
	            }

	            // Redirect to the library page after deletion
	            return "redirect:/gallery";
	        } catch (Exception e) {
	            e.printStackTrace();
	            // Redirect to the library page with an error message if something goes wrong
	            return "redirect:/gallery?error=true";
	        }
	    }

}
