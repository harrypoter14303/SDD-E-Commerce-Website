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
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService service;

	@Autowired
	private ProductService pservice;
	
	@Autowired
	private UserService uservice;

	@GetMapping("/")
	public String index() {
		return "admin/index";
	}
	
	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if(p != null) {
			String email = p.getName();
			UserDtls userDtls = uservice.getUserByEmail(email);
			m.addAttribute("user",userDtls);
		}
		List<Category> allActiveCategory = service.getAllActiveCategory();
		m.addAttribute("categorys",allActiveCategory);
	}

	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) {
		List<Category> categories = service.getAllCategory();
		m.addAttribute("categories", categories);
		return "admin/add_product";
	}

	@GetMapping("/category")
	public String category(Model m) {
		m.addAttribute("categorys", service.getAllCategory());
		return "admin/category";
	}

	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);

		Boolean existCategory = service.existCategory(category.getName());

		if (existCategory) {
			session.setAttribute("errorMsg", "Category Exists..!");
		} else {
			Category saveCategory = service.saveCategory(category);

			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not Saved (or) Internal Server error");
			} else {

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category" + File.separator
						+ file.getOriginalFilename());

//				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				session.setAttribute("succMsg", "Saved Successfully..!");
			}
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {

		Boolean deleteCategory = service.deleteCategory(id);

		if (deleteCategory) {
			session.setAttribute("succMsg", "Deleted Successfully..!");
		} else {
			session.setAttribute("errorMsg", "Delete Error..!");
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {
		m.addAttribute("category", service.getCategoryById(id));
		return "admin/eidt";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		Category oldCategory = service.getCategoryById(category.getId());
		String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

		if (!ObjectUtils.isEmpty(category)) {
			oldCategory.setName(category.getName());
			oldCategory.setIsActive(category.getIsActive());
			oldCategory.setImageName(imageName);
		}

		Category updateCategory = service.saveCategory(oldCategory);

		if (!ObjectUtils.isEmpty(updateCategory)) {

			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category" + File.separator
						+ file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			session.setAttribute("succMsg", "Category Updated..!");
		} else {
			session.setAttribute("errorMsg", "Server Error..!");
		}

		return "redirect:/admin/loadEditCategory/" + category.getId();
	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());

		Product saveProduct = pservice.saveProduct(product);

		if (!ObjectUtils.isEmpty(saveProduct)) {

			File saveFile = new ClassPathResource("static/img").getFile();

			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product" + File.separator
					+ image.getOriginalFilename());

			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			session.setAttribute("succMsg", "Saved Successfully..!");
		} else {
			session.setAttribute("errorMsg", "Server Error..!");
		}

		return "redirect:/admin/loadAddProduct";
	}

	@GetMapping("/products")
	public String loadViewProduct(Model m) {
		m.addAttribute("products", pservice.getAllProducts());
		return "admin/products";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {

		Boolean delete = pservice.deleteProduct(id);

		if (delete) {
			session.setAttribute("succMsg", "Deleted Successfully..!");
		} else {
			session.setAttribute("errorMsg", "Server Error..!");
		}

		return "redirect:/admin/products";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", pservice.getProductById(id));
		m.addAttribute("category", service.getAllCategory());
		return "admin/edit_product";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product p, @RequestParam("file") MultipartFile image,
			HttpSession session, Model m) {
	
		if(p.getDiscount()<0 || p.getDiscount()>100) {
			session.setAttribute("errorMsg","Invalid Value..!");
		}
		else {
			Product updateProduct = pservice.updateProduct(p, image);
			if(!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("succMsg", "Updated Successfully..!");
			}
			else {
				session.setAttribute("errorMsg", "Server Error..!");
			}
		}

		return "redirect:/admin/editProduct/" + p.getId();
	}
	
	@GetMapping("/users")
	 public String getAllUsers(Model m) {
		List<UserDtls> users = uservice.getUsers("ROLE_USER");
		m.addAttribute("users",users);
		 return "/admin/users";
	 }
	
	@GetMapping("/updateStatus")
	public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session) {
		Boolean f = uservice.updateAccountStatus(id, status);
		if(f) {
			session.setAttribute("succMsg", "Status Updated..!");
		}
		else {
			session.setAttribute("errorMsg", "Server Error..!");
		}
		return "redirect:/admin/users";
		
	}

}
