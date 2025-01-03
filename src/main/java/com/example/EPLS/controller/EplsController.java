package com.example.EPLS.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

import com.example.EPLS.model.ChallengeRequests;
import com.example.EPLS.model.Challenges;
import com.example.EPLS.model.Images;
import com.example.EPLS.model.Users;
import com.example.EPLS.repository.ChallengeRequestsRepository;
import com.example.EPLS.repository.ChallengesRepository;
import com.example.EPLS.repository.ImagesRepository;
import com.example.EPLS.repository.UserRepository;
import com.example.EPLS.service.ChallengeService;

import jakarta.servlet.http.HttpSession;

@Controller
public class EplsController {

	@Value("${upload.path:src/main/resources/static/uploads}")
	private String uploadDir;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ImagesRepository imagesRepository;

	@Autowired
	ChallengesRepository challengeRepository;

	@Autowired
	private ChallengeService challengeService;

	@Autowired
	private HttpSession session;

	@Autowired
	private ChallengeRequestsRepository challengeRequestRepository;

	@GetMapping("/")
	public String index() {
		return "index"; // Serves index.html
	}

	@GetMapping("/admindashboard")
	public String adminDashboard(Model model) {
		List<Users> allUsers = userRepository.findAll();
		List<ChallengeRequests> participationRequests = challengeRequestRepository.findAll();
		
	    System.out.println("DEBUG: Participation Requests:");
	    for (ChallengeRequests request : participationRequests) {
	        System.out.println(request.toString());  // This will call the toString() method of ChallengeRequests
	    }

		// Add user details and the list of all users to the model
		model.addAttribute("usersList", allUsers);
		model.addAttribute("challengeRequests",participationRequests);
		return "admindashboard"; // Serves admindashboard.html
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

	@GetMapping("/createChallenge")
	public String createChallenge() {
		return "createChallenge";
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
			return "redirect:/admindashboard"; // Redirect to admin dashboard
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
		session.setAttribute("fullname", user.getFullname());
		model.addAttribute("user", user); // Add user object to model for personalization
		return "redirect:/dashboard"; // Redirect to the user dashboard
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
				// If the item does not exist, handle accordingly (optional, you can redirect or
				// show an error)
				return "redirect:/library?error=ItemNotFound";
			}

			// Redirect to the library page after deletion
			return "redirect:/library";
		} catch (Exception e) {
			e.printStackTrace();
			// Redirect to the library page with an error message if something goes wrong
			return "redirect:/library?error=true";
		}
	}

	@PostMapping("/deleteUser/{id}")
	public String deleteUser(@PathVariable("id") Long id, Model model) {
		try {
			// Check if the user exists
			Optional<Users> userOpt = userRepository.findById(id);

			if (!userOpt.isPresent()) {
				// User not found, return an error message (or redirect with an error)
				model.addAttribute("error", "User not found!");
				return "redirect:/admindashboard"; // Redirect back to the dashboard
			}

			// Delete the user
			userRepository.deleteById(id);

			// Redirect to the dashboard after successful deletion
			model.addAttribute("success", "User deleted successfully.");
			return "redirect:/admindashboard";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "An error occurred while deleting the user.");
			return "redirect:/admindashboard"; // Redirect back to the dashboard in case of an error
		}
	}

	@PostMapping("/createChallenge")
	public String createChallenge(@RequestParam("file") MultipartFile file, @RequestParam("title") String title,
			@RequestParam("description") String description, Model model) {

		// Check if the file is empty
		if (file.isEmpty()) {
			model.addAttribute("error", "Please select an image to upload for the challenge.");
			return "create_challenge"; // Return to the challenge creation page with an error
		}

		// Log the title and description for debugging
		System.out.println("Challenge Title: " + title);
		System.out.println("Challenge Description: " + description);

		try {
			// Resolve the upload directory from application.properties
			File uploadDirectory = new File(uploadDir);
			if (!uploadDirectory.exists()) {
				uploadDirectory.mkdirs(); // Create the directory if it doesn't exist
			}

			// Generate a unique filename using UUID to avoid conflicts
			String originalFilename = file.getOriginalFilename();
			String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

			// Save the file to the directory with the unique filename
			String filePath = uploadDirectory.getAbsolutePath() + "/" + uniqueFilename;
			file.transferTo(new File(filePath));

			// Save the challenge metadata to the database
			Challenges challenge = new Challenges(title, description, uniqueFilename);

			// Save challenge data into the repository
			challengeRepository.save(challenge); // Save challenge to the database

			// Add success message to the model
			model.addAttribute("success", "Challenge created successfully.");
			return "createChallenge"; // Return to the challenge creation page with a success message
		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("error", "An error occurred while creating the challenge.");
			return "createChallenge"; // Return to the challenge creation page with an error message
		}
	}

	@GetMapping("/allChallenges")
	public String getAllChallenges(Model model) {
		List<Challenges> challenges = challengeRepository.findAll();
		model.addAttribute("challenges", challenges);

		// Fetch the status from the session
		String status = (String) session.getAttribute("regStatus");

		// Check the status and add appropriate message
		if ("yes".equals(status)) {
			model.addAttribute("message", "You have already requested to participate in this challenge.");
			session.removeAttribute("regStatus"); // Optional: Clear the status to prevent repeated messages
		} else if ("success".equals(status)) {
			model.addAttribute("message", "Participation request submitted successfully.");
			session.removeAttribute("regStatus"); // Optional: Clear the status to prevent repeated messages
		} else {
			model.addAttribute("message", ""); // Default empty message
		}

		return "challenges"; // This refers to the view named 'challenges.html'
	}

	@GetMapping("/adminChallenges")
	public String getAllChallenge(Model model) {
		List<Challenges> challenges = challengeService.getAllChallenges(); // Service method to fetch challenges
		model.addAttribute("challenges", challenges);
		return "adminChallenges"; // Returns the adminChallenges.html view
	}

	@GetMapping("/editChallenge/{id}")
	public String editChallengeForm(@PathVariable("id") Long challengeId, Model model) {
		Challenges challenge = challengeService.getChallengeById(challengeId);
		model.addAttribute("challenge", challenge);
		return "editChallenge"; // The name of the HTML template to render the edit form
	}

	@PostMapping("/editChallenge")
	public String updateChallenge(@RequestParam("id") Long challengeId,
			@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("title") String title,
			@RequestParam("description") String description, Model model) {
		try {
			// Debugging step to verify inputs
			System.out.println("Title: " + title);
			System.out.println("Description: " + description);

			// Find the existing challenge
			Challenges existingChallenge = challengeRepository.findById(challengeId)
					.orElseThrow(() -> new RuntimeException("Challenge not found"));

			// Validate inputs
			if (title == null || title.isEmpty()) {
				model.addAttribute("error", "Challenge title cannot be empty.");
				return "editChallenge";
			}
			if (description == null || description.isEmpty()) {
				model.addAttribute("error", "Challenge description cannot be empty.");
				return "editChallenge";
			}

			// Update metadata
			existingChallenge.setChallengeTitle(title);
			existingChallenge.setDescription(description);

			// Handle file upload if a new file is provided
			if (file != null && !file.isEmpty()) {
				if (!file.getContentType().startsWith("image/")) {
					model.addAttribute("error", "Only image files are allowed.");
					return "editChallenge";
				}

				// Set the upload directory
				File uploadDirectory = new File(uploadDir);
				if (!uploadDirectory.exists()) {
					uploadDirectory.mkdirs(); // Create the directory if it does not exist
				}

				// Generate a unique filename
				String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
				String filePath = uploadDirectory.getAbsolutePath() + File.separator + uniqueFilename;

				// Save the file
				file.transferTo(new File(filePath));

				// Update the challenge with the new file path
				existingChallenge.setImagePath(uniqueFilename);
			}

			// Save updated challenge
			challengeRepository.save(existingChallenge);

			Challenges ch = challengeRepository.findById(challengeId)
					.orElseThrow(() -> new RuntimeException("Challenge not found"));

			model.addAttribute("success", "Challenge updated successfully.");
			model.addAttribute("challenge", ch);
			return "editChallenge";
		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("error", "An error occurred while updating the challenge.");
			return "editChallenge";
		} catch (RuntimeException e) {
			model.addAttribute("error", e.getMessage());
			return "editChallenge";
		}
	}

	@PostMapping("/deleteChallenge/{id}")
	public String deleteChallenge(@PathVariable("id") Long challengeId, Model model) {
		try {
			// Check if the challenge exists
			Challenges challenge = challengeRepository.findById(challengeId)
					.orElseThrow(() -> new RuntimeException("Challenge not found"));

			// Delete the challenge
			challengeRepository.delete(challenge);

			// Redirect with a success message
			model.addAttribute("success", "Challenge deleted successfully.");
			return "redirect:/adminChallenges"; // Redirect to the challenges list page
		} catch (RuntimeException e) {
			model.addAttribute("error", e.getMessage());
			return "redirect:/adminChallenges"; // Redirect back to the challenges list page with an error
		}
	}

	@GetMapping("/participateRequest/{id}")
	public String participateInChallenge(@PathVariable("id") Long challengeId, Model model) {
		try {
			// Fetch user details from session
			String userEmail = (String) session.getAttribute("userEmail");
			if (userEmail == null) {

				return "login";
			}

			// Check if user already requested participation in this challenge
			Optional<ChallengeRequests> existingRequest = challengeRequestRepository
					.findByChallengeIdAndUserEmail(challengeId, userEmail);
			if (existingRequest.isPresent()) {
				session.setAttribute("regStatus", "yes");
				return "redirect:/allChallenges";
			}

			// Create a new ChallengeRequests object
			ChallengeRequests newRequest = new ChallengeRequests();
			newRequest.setChallengeId(challengeId);

			// Fetch additional user details if stored in session
			String userRequested = (String) session.getAttribute("fullname"); // Assume "userName" is stored in session
			newRequest.setUserRequested(userRequested != null ? userRequested : "Anonymous");
			
			 // Fetch the challenge name from the ChallengeRepository
	        Optional<Challenges> challenge = challengeRepository.findById(challengeId);
	        if (!challenge.isPresent()) {
	            model.addAttribute("message", "Challenge not found");
	            return "redirect:/allChallenges";
	        }
	        String challengeName = challenge.get().getChallengeTitle();// Assuming the challenge object has a getChallengeName method
	        
	        newRequest.setChallengeName(challengeName);
			newRequest.setUserEmail(userEmail);
			newRequest.setCount(0); // Default count is 0
			newRequest.setStatus("pending"); // Default status is "pending"

			// Save the new request to the database
			challengeRequestRepository.save(newRequest);

			session.setAttribute("regStatus", "success");
			return "redirect:/allChallenges";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "An error occured while sending the request");
			return "redirect:/allchallenges";
		}
	}
	
	@PostMapping("/acceptRequest/{id}")
	public String acceptRequest(@PathVariable("id") Long id, Model model) {
	    try {
	        // Fetch the ChallengeRequest by ID from the database
	        Optional<ChallengeRequests> optionalRequest = challengeRequestRepository.findById(id);
	        
	        // Check if the request exists
	        if (optionalRequest.isPresent()) {
	            ChallengeRequests request = optionalRequest.get();
	            
	            // Update the status to "accepted"
	            request.setStatus("accepted");
	            
	            // Save the updated request back to the repository
	            challengeRequestRepository.save(request);
	            
	            // Optionally, add a success message to the model
	            model.addAttribute("message", "Request has been accepted successfully!");
	        } else {
	            // If the request is not found, add an error message to the model
	            model.addAttribute("message", "Challenge request not found!");
	        }
	        
	        // Redirect to the admin dashboard or relevant page
	        return "redirect:/admindashboard";
	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("message", "An error occurred while accepting the request.");
	        return "redirect:/admindashboard";
	    }
	}

	@PostMapping("/rejectRequest/{id}")
	public String rejectRequest(@PathVariable("id") Long id, Model model) {
	    try {
	        // Fetch the ChallengeRequest by ID from the database
	        Optional<ChallengeRequests> optionalRequest = challengeRequestRepository.findById(id);
	        
	        // Check if the request exists
	        if (optionalRequest.isPresent()) {
	            ChallengeRequests request = optionalRequest.get();
	            
	            // Update the status to "rejected"
	            request.setStatus("rejected");
	            
	            // Save the updated request back to the repository
	            challengeRequestRepository.save(request);
	            
	            // Optionally, add a success message to the model
	            model.addAttribute("message", "Request has been rejected successfully!");
	        } else {
	            // If the request is not found, add an error message to the model
	            model.addAttribute("message", "Challenge request not found!");
	        }
	        
	        // Redirect to the admin dashboard or relevant page
	        return "redirect:/admindashboard";
	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("message", "An error occurred while rejecting the request.");
	        return "redirect:/admindashboard";
	    }
	}
	
	@GetMapping("/userRequests")
	public String getUserRequests(Model model, HttpSession session) {
	    try {
	        // Retrieve userEmail from session
	        String userEmail = (String) session.getAttribute("userEmail");

	        // Check if userEmail exists in session, otherwise redirect to login page
	        if (userEmail == null) {
	            return "login";  // Or any page to handle when user is not logged in
	        }

	        // Fetch all ChallengeRequests associated with the user's email
	        List<ChallengeRequests> userRequests = challengeRequestRepository.findByUserEmail(userEmail);

	        // Add the list of requests to the model for rendering in the view
	        model.addAttribute("userRequests", userRequests);
	        return "userRequestsPage";  // The view page to show user's requests
	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("message", "An error occurred while fetching the requests");
	        return "errorPage";  // The page to display error if fetching requests fails
	    }
	}

}
