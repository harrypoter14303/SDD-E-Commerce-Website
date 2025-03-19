package in.shreyas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.shreyas.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findByIsActiveTrue();

	List<Product> findByCategory(String category);

}
