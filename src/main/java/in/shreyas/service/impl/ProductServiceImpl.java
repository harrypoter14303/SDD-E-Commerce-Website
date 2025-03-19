package in.shreyas.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import in.shreyas.entity.Product;
import in.shreyas.repository.ProductRepository;
import in.shreyas.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository repo;

	@Override
	public Product saveProduct(Product product) {

		return repo.save(product);
	}

	@Override
	public List<Product> getAllProducts() {

		return repo.findAll();
	}

	@Override
	public Boolean deleteProduct(Integer id) {

		Product p = repo.findById(id).orElse(null);

		if (!ObjectUtils.isEmpty(p)) {
			repo.deleteById(id);
			return true;
		}
		return false;
	}

	@Override
	public Product getProductById(Integer id) {

		return repo.findById(id).orElse(null);
	}

	@Override
	public Product updateProduct(Product p, MultipartFile image) {

		Product dbProduct = getProductById(p.getId());

		String imageName = image.isEmpty() ? dbProduct.getImage() : image.getOriginalFilename();

		dbProduct.setTitle(p.getTitle());
		dbProduct.setDescription(p.getDescription());
		dbProduct.setCategory(p.getCategory());
		dbProduct.setPrice(p.getPrice());
		dbProduct.setStock(p.getStock());
		dbProduct.setImage(imageName);
		dbProduct.setIsActive(p.getIsActive());
		dbProduct.setDiscount(p.getDiscount());
		
		// 5=100*(5/100); 100-5=95
		Double discount = p.getPrice()*(p.getDiscount()/100.0);
		Double discountPrice = p.getPrice()-discount;
		
		dbProduct.setDiscountPrice(discountPrice); 

		Product updateProduct = repo.save(dbProduct);

		if (!ObjectUtils.isEmpty(updateProduct)) {

			if (!image.isEmpty()) {
				File saveFile;
				try {
					saveFile = new ClassPathResource("static/img").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product" + File.separator
							+ image.getOriginalFilename());
					Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			return p;
		}
		return null;
	}

	@Override
	public List<Product> getAllActiveProducts(String category) {
		List<Product> products = null;
		
		if(ObjectUtils.isEmpty(category)) {
			products =  repo.findByIsActiveTrue();
		}
		else {
			products = repo.findByCategory(category);
		}
		
		return products;
	}
	
	

}
