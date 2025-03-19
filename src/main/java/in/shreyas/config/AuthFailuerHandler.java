package in.shreyas.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import in.shreyas.entity.UserDtls;
import in.shreyas.repository.UserRepository;
import in.shreyas.service.UserService;
import in.shreyas.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailuerHandler extends SimpleUrlAuthenticationFailureHandler {
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private UserService uservice;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		 String email = request.getParameter("username");
		 
		 UserDtls userDtls = repo.findByEmail(email);
		 
		 if(userDtls.getIsEnable()) {
			 
			 if(userDtls.getAccountNonLocked()) {
				 
				 if(userDtls.getFailedAttempt() <= AppConstant.ATTEMPT_TIME) {
					 uservice.increaseFailedAttempt(userDtls);
				 }else {
					 uservice.userAccountLock(userDtls);
					 exception = new LockedException("Account Locked..!");
				 }
				 
			 }else {
				 
				 if(uservice.unlockAccountTimeExpired(userDtls)) {
					 exception = new LockedException("Account UnLocked :)");
				 }else {
					 exception = new LockedException("Account Locked..!");
				 } 
			 }
			 
		 }else {
			 exception = new LockedException("Account Not Active..!");
		 }
		
		super.onAuthenticationFailure(request, response, exception);
	}
	
	

}
