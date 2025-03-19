package in.shreyas.service;

import java.util.List;

import in.shreyas.entity.UserDtls;

public interface UserService {
	
	public UserDtls saveUser(UserDtls user);
	
	public UserDtls getUserByEmail(String email);

	List<UserDtls> getUsers(String role);

	public Boolean updateAccountStatus(Integer id, Boolean status);

	public void increaseFailedAttempt(UserDtls user);
	
	public void userAccountLock(UserDtls user);
	
	public boolean unlockAccountTimeExpired(UserDtls user);
	
	public void resetAttempt(int userId);
}
