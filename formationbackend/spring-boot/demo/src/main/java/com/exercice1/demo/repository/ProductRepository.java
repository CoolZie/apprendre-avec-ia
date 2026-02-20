package com.exercice1.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.exercice1.demo.model.Product;
import com.exercice1.demo.model.enums.Category;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);

    List<Product> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);

    Optional<Product> findByName(String name);

    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description,Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);

    Page<Product> findByPriceBetween(Double min, Double max, Pageable pageable);

    Page<Product> findByStockLessThan(Integer threshold, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:minStock IS NULL OR p.stock >= :minStock) AND " +
            "(:maxStock IS NULL OR p.stock <= :maxStock) AND " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> findByFilters(
            @Param("category") Category category,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minStock") Integer minStock,
            @Param("maxStock") Integer maxStock,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p GROUP BY p.category")
    List<Object[]> countByCategory();

    @Query("SELECT p.category, COUNT(p), AVG(p.price), SUM(p.price * p.stock) " +
            "FROM Product p GROUP BY p.category")
    List<Object[]> getStatisticsByCategory();

    long countByStockLessThan(Integer threshold);

    long countByStock(Integer stock);

    @Query("SELECT SUM(p.price * p.stock) FROM Product p")
    Double calculateTotalStockValue();

    @Query("SELECT AVG(p.price) FROM Product p")
    Double calculateAveragePrice();
}
