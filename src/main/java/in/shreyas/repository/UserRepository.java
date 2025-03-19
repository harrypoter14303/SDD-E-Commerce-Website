package in.shreyas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.shreyas.entity.UserDtls;

public interface UserRepository extends JpaRepository<UserDtls, Integer>{
	
	public UserDtls findByEmail(String email);

	public List<UserDtls> findByRole(String role);

}
