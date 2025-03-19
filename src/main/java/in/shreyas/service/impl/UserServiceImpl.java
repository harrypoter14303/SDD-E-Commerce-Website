package in.shreyas.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.shreyas.entity.UserDtls;
import in.shreyas.repository.UserRepository;
import in.shreyas.service.UserService;
import in.shreyas.util.AppConstant;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDtls saveUser(UserDtls user) {
		user.setRole("ROLE_USER");
		user.setIsEnable(true);
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		return repo.save(user);
	}

	@Override
	public UserDtls getUserByEmail(String email) {
		
		return repo.findByEmail(email);
	}

	@Override
	public List<UserDtls> getUsers(String role) {
		
		return repo.findByRole(role);
	}

	

	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {
		
		Optional<UserDtls> findByuser =  repo.findById(id);
		
		if(findByuser.isPresent()) {
			UserDtls userDtls = findByuser.get();
			userDtls.setIsEnable(status);
			repo.save(userDtls);
			return true;
		}
		
		return false;
	}

	@Override
	public void increaseFailedAttempt(UserDtls user) {
		int attempt = user.getFailedAttempt() + 1;
		user.setFailedAttempt(attempt);
		repo.save(user);
	}

	@Override
	public void userAccountLock(UserDtls user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		repo.save(user);
	}

	@Override
	public boolean unlockAccountTimeExpired(UserDtls user) {
		
		long lockTime = user.getLockTime().getTime();
		long unLockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
		
		long currentTime = System.currentTimeMillis();// Returns the current time in milliseconds
		
		if(unLockTime < currentTime) {
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			repo.save(user);
			return true;
		}
		
		return false;
	}

	@Override
	public void resetAttempt(int userId) {
		
		
	}

}
