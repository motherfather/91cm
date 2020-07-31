package com.nineone.nocm.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.nineone.nocm.domain.User;
import com.nineone.nocm.repository.UserRepository;

public class CustomAuthenticationSuccessHandler extends  SavedRequestAwareAuthenticationSuccessHandler {
	@Autowired
	private UserRepository userRepository;
	
	 public CustomAuthenticationSuccessHandler(String defaultTargetUrl) {
	        setDefaultTargetUrl(defaultTargetUrl);
	 }
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		System.out.println("뭔데 이상한 곳으로 리다이렉션되냐구");
		if(request.getParameter("email")!=null) {
			User user = userRepository.getUserfindByEmail(request.getParameter("email"));
			user.setPassword(null);
			HttpSession session = request.getSession();
			session.setAttribute("user", user);
		}
		getRedirectStrategy().sendRedirect(request, response, "/main");
	}
} 