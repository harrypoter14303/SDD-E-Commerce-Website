package in.shreyas.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import in.shreyas.entity.Category;
import in.shreyas.repository.CategoryRepository;
import in.shreyas.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService{

	@Autowired
	private CategoryRepository repo;
	
	@Override
	public Category saveCategory(Category category) {
		
		return repo.save(category);
	}

	@Override
	public List<Category> getAllCategory() {
		
		return repo.findAll();
	}

	@Override
	public Boolean existCategory(String name) {
		
		return repo.existsByName(name);
	}

	@Override
	public Boolean deleteCategory(int id) {
		Category category = repo.findById(id).orElse(null);
		
		if(!ObjectUtils.isEmpty(category)) {
			repo.delete(category);
			return true;
		}
		
		return false;
	}

	@Override
	public Category getCategoryById(int id) {
		Category category = repo.findById(id).orElse(null);
		return category;
	}

	@Override
	public List<Category> getAllActiveCategory() {
		
		List<Category> categories = repo.findByIsActiveTrue();
		
		return categories;
	}

}
