package in.shreyas.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import in.shreyas.entity.Category;
import in.shreyas.entity.UserDtls;
import in.shreyas.service.CategoryService;
import in.shreyas.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService uservice;
	
	@Autowired
	private CategoryService cservice;
	
	@GetMapping("/")
	public String home() {
		
		return "user/home";
	}
	
	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if(p != null) {
			String email = p.getName();
			UserDtls userDtls = uservice.getUserByEmail(email);
			m.addAttribute("user",userDtls);
		}
		List<Category> allActiveCategory = cservice.getAllActiveCategory();
		m.addAttribute("categorys",allActiveCategory);
	}
}
