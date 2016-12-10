package bltn.uc3m.tiw.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import bltn.uc3m.tiw.domains.User;
import bltn.uc3m.tiw.helpers.PasswordHashGenerator;

@Controller
public class UserController {
	
	@Autowired
	RestTemplate restTemplate;
	
	@RequestMapping(method = RequestMethod.GET, value = "/user/login")
	public String renderLoginPage() {
		return "login";
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/user/login")
	public String executeLogin(HttpServletRequest request) {
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
			return "success";
		} else {
			return "login";
		}
	}
	
	@RequestMapping("/user/logout")
	public String executeLogout(HttpServletRequest request) {
		request.getSession().removeAttribute("user");
		request.getSession().invalidate();
		return "login";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/user/{id}")
	public String userProfile(HttpServletRequest request, Model model, @PathVariable("id") Integer id) {
		// Query microservice for user with the given id 
		User user = restTemplate.postForObject("http://"
				+ "localhost:8081/user/{id}", null, User.class,
				id);
		
		User loggedInUser = (User) request.getSession(false).getAttribute("user");
		
		if (user != null) {
			// check the request isn't for a different user's profile 
			if (user.getUserID().equals(loggedInUser.getUserID())) {
				model.addAttribute(user);
				return "userProfile";
			}
		}
		return "index";
	}
	
	@RequestMapping("/user/{id}/delete")
	public String deleteUser(HttpServletRequest request, @PathVariable("id") Integer id) {
		User loggedInUser = (User) request.getSession(false).getAttribute("user");
		
		// Check the delete request is for the logged in user's own account 
		if (id.equals(loggedInUser.getUserID())) {
			// Send request for microservice to delete user with the given id 
			restTemplate.postForObject("http://"
					+ "localhost:8081/user/{id}/delete", null, boolean.class,
					id);
			
			return "login";
		} else {
			return "index";
		}
	}
}
