package bltn.uc3m.tiw.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import bltn.uc3m.tiw.domains.User;

public class UserAuthInteceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		String requestURI = arg0.getRequestURI();
		HttpSession session = arg0.getSession(false);
		
		// redirect if user's trying to access a page they aren't authorised for 
		if (session == null && !requestURI.equals("/users/login") && !requestURI.equals("/users/new")) {
			arg1.sendRedirect("/users/login");
		// Block logged in users' access to login and signup pages 
		} else if (session != null && (requestURI.equals("/users/login") || requestURI.equals("/users/new"))) {
			User user = (User) session.getAttribute("user");
			arg1.sendRedirect("/users/"+user.getUserID());
		}
	}

	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
		return true;
	}
}