package bltn.uc3m.tiw.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

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
		
		// Validate user details 		
		boolean detailsValid = restTemplate.postForObject("http://"
				+ "localhost:8081/{email}/authenticateLogin", hashedPassword, boolean.class,
				email);
		
		if (detailsValid) {
			request.getSession().setAttribute("user", email);
			return "success";
		} else {
			return "login";
		}
	}
	
	@RequestMapping("/user/logout")
	public String executeLogout(HttpServletRequest request) {
		if (request.getSession(false) != null) {
			request.getSession().invalidate();
		}
		return "login";
	}
}
