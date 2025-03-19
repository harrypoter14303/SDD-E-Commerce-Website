package in.shreyas.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import in.shreyas.entity.Category;
import in.shreyas.entity.Product;
import in.shreyas.entity.UserDtls;
import in.shreyas.service.CategoryService;
import in.shreyas.service.ProductService;
import in.shreyas.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@Autowired
	private UserService uservice;
	
	@Autowired
	private CategoryService cservice;
	
	@Autowired
	private ProductService pservice;
	
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

	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/signin")
	public String login() {
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@GetMapping("/product")
	public String product(Model m, @RequestParam(defaultValue = "",value="category") String category) {
		// System.out.println("category="+category);
		List<Category> categories = cservice.getAllActiveCategory();
		List<Product> products = pservice.getAllActiveProducts(category);
		m.addAttribute("categories", categories);
		m.addAttribute("products",products);
		m.addAttribute("paramValue",category);
		
		return "product";
	}
	
	@GetMapping("/vproduct/{id}")
	public String vproduct(@PathVariable int id, Model m) {
		Product productById = pservice.getProductById(id);
		m.addAttribute("p",productById);
		return "view_product";
	}
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file,HttpSession session) throws IOException {
		
		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(imageName);
		UserDtls saveUser = uservice.saveUser(user);
		
		if(!ObjectUtils.isEmpty(saveUser)) {
			File saveFile = new ClassPathResource("static/img").getFile();

			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category" + File.separator
					+ file.getOriginalFilename());

			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			session.setAttribute("succMsg", "Saved Successfully..!");
		}else {
			session.setAttribute("errorMsg","Server Error..!");
		}
		
		return "redirect:/register";
	}
	
	
	
	
}
