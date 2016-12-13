package bltn.uc3m.tiw.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import bltn.uc3m.tiw.domains.User;
import bltn.uc3m.tiw.helpers.PasswordHashGenerator;

@Controller
public class UserController {
	
	@Autowired
	RestTemplate restTemplate;
	
	// Display all users 
	@RequestMapping("/users/index")
	public String renderAllUsers(HttpServletRequest request, Model model) {
		User currentUser = (User) request.getSession(false).getAttribute("user");
		
		// Render all users if the logged in user has admin rights 
		if (currentUser.isAdmin()) {
			@SuppressWarnings("unchecked")
			List<User> users = restTemplate.getForObject("http://"
					+ "localhost:8081/users/index", List.class);
			
			model.addAttribute("users", users);
			return "users/index";
		} else { 
			return "index";
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/users/new")
	public String renderSignupPage() {
		return "users/new";
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/users/new")
	public String executeSignup(Model model, HttpServletRequest request) {
		Map<String, String[]> formParams = request.getParameterMap();
		
		// Replace plain-text password in params with its hashed counterpart 		
		String password = formParams.get("password")[0];
		String hashedPasswordArray[] = new String[1];
		String hashedPassword = PasswordHashGenerator.md5(password);
		hashedPasswordArray[0] = hashedPassword;
		formParams.replace("password", hashedPasswordArray);
		
		// Query microservice to create new user 
		User newUser = restTemplate.postForObject("http://"
				+ "localhost:8081/users/new", formParams, User.class);
		
		if (newUser != null) {
			return "redirect:/users/login";
		} else {
			model.addAttribute("error", "Email or password taken");
			return "users/new";
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/users/login")
	public String renderLoginPage() {
		return "users/login";
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/users/login")
	public String executeLogin(Model model, HttpServletRequest request) {
		// Fetch form params 
		String email = (String)request.getParameter("email");
		String password = (String)request.getParameter("password");
		String hashedPassword = PasswordHashGenerator.md5(password);
		
		// Query microservice for user  		
		User user = restTemplate.postForObject("http://"
				+ "localhost:8081/{email}/authenticateLogin", hashedPassword, User.class,
				email);
		
		if (user != null) {
			request.getSession().setAttribute("user", user);
			return "redirect:/users/"+user.getUserID();
		} else {
			model.addAttribute("error", "Email and/or password incorrect.");
			return "users/login";
		}
	}
	
	@RequestMapping("/users/logout")
	public String executeLogout(HttpServletRequest request) {
		request.getSession().removeAttribute("user");
		request.getSession().invalidate();
		return "users/login";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/users/{id}")
	public String userProfile(HttpServletRequest request, Model model, @PathVariable("id") Integer id) {
		// Query microservice for user with the given id 
		User user = restTemplate.postForObject("http://"
				+ "localhost:8081/users/{id}", null, User.class,
				id);
		
		User loggedInUser = (User) request.getSession(false).getAttribute("user");
		
		if (user != null) {
			// check the request isn't for a different user's profile or that the user is an admin
			if (user.getUserID().equals(loggedInUser.getUserID()) || loggedInUser.isAdmin()) {
				model.addAttribute(user);
				return "users/view";
			}
		}
		return "index";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/users/{id}/edit")
	public String editUser(Model model, HttpServletRequest request, @PathVariable("id") Integer id) {
		// Query microservice for user with the given id 
		User user = restTemplate.postForObject("http://"
				+ "localhost:8081/users/{id}", null, User.class,
				id);
		
		// Check the request isn't for a different user or that the user is an admin
		if (user.getUserID().equals(id) || user.isAdmin()) {
			model.addAttribute(user);
			return "users/edit";
		} else {
			return "index";
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/users/{id}/edit")
	public String updateUser(Model model, HttpServletRequest request, @PathVariable("id") Integer id) {
		User loggedInUser = (User) request.getSession(false).getAttribute("user");
		
		Map<String, String[]> formParams = request.getParameterMap();
		
		// Make sure request is for the logged in user or that the user is an admin
		if (loggedInUser.getUserID().equals(id) || loggedInUser.isAdmin()) {
			User user = restTemplate.postForObject("http://"
					+ "localhost:8081/users/{id}/update", formParams, User.class, id);
			
			// Update the logged in user's details if the update was a success
			if (user != null) {
				request.getSession(false).setAttribute("user", user);
				return "redirect:/users/"+id;
			} else {
				model.addAttribute("error", "Error updating details. All fields need at least 2 characters, apart from email (3)");
				model.addAttribute("user", loggedInUser);
				return "users/edit";
			}
		} else {
			return "index";
		}
	}
	
	@RequestMapping("/users/{id}/delete")
	public String deleteUser(HttpServletRequest request, @PathVariable("id") Integer id) {
		User loggedInUser = (User) request.getSession(false).getAttribute("user");
		
		// Check the delete request is for the logged in user's own account or that the user is an admin
		if (id.equals(loggedInUser.getUserID()) || loggedInUser.isAdmin()) {
			// Send request for microservice to delete user with the given id 
			restTemplate.postForObject("http://"
					+ "localhost:8081/users/{id}/delete", null, boolean.class,
					id);
			
			if (loggedInUser.getUserID().equals(id)) {
				request.getSession().removeAttribute("user");
				request.getSession().invalidate();
				return "redirect:/users/login";
			}
		}
		return "redirect:/users/index";
	}
}
