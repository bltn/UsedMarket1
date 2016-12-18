package bltn.uc3m.tiw.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import bltn.uc3m.tiw.domains.Chat;
import bltn.uc3m.tiw.domains.User;

@Controller
public class ChatController {

	@Autowired
	RestTemplate restTemplate;
	
	/*
	 * Render chat page
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/chats/new/{recipientID}")
	public String newChatPage(Model model, @PathVariable("recipientID") Integer recipientID, HttpServletRequest request) {
		User user = (User) request.getSession(false).getAttribute("user");
		
		// See if a chat already exists between the 2 users 
		Chat chat = restTemplate.getForObject("http://"
				+ "localhost:8083/chats/{userID1}/{userID2}", Chat.class, recipientID, user.getUserID());
		if (chat != null) {
			model.addAttribute("chat", chat);
			// if (!chat.getUser1().equals(user))
			//		model.addAttribute("recipient", chat.getUser1())
			// else 
			// 		model.addAttribute("recipient", chat.getUser2())
			return "chats/view";
		} else {
			User recipient = restTemplate.getForObject("http://"
					+ "localhost:8081/users/{id}", User.class, recipientID);
			
			model.addAttribute("recipient", recipient);
			return "chats/new";
		}
	}
}
