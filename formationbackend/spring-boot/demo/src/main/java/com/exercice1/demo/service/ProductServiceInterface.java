package com.exercice1.demo.service;

import java.util.List;

import com.exercice1.demo.dto.CategoryStatistics;
import com.exercice1.demo.dto.PagedResponse;
import com.exercice1.demo.dto.ProductRequest;
import com.exercice1.demo.dto.ProductResponse;
import com.exercice1.demo.dto.ProductStatistics;
import com.exercice1.demo.model.enums.Category;

public interface ProductServiceInterface {
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductList(String name);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    List<ProductResponse> getProductListByCategory(Category category);
    public ProductResponse updateStock(Long id, Integer quantity);
    public PagedResponse<ProductResponse> getAllProducts(int page, int size, String sortBy, String direction);
    public PagedResponse<ProductResponse> searchProducts(String keyword, int page, int size, String sortBy, String direction);
    public PagedResponse<ProductResponse> getProductsByCategory(String category, int page, int size, String sortBy, String direction);
    public PagedResponse<ProductResponse> getProductsByPriceRange(Double min, Double max, int page, int size, String sortBy, String direction);
    public PagedResponse<ProductResponse> getLowStockProducts(Integer threshold, int page, int size, String sortBy, String direction);
    public PagedResponse<ProductResponse> filterProducts(String category, Double minPrice, Double maxPrice,Integer minStock, Integer maxStock, String keyword,int page, int size, String sortBy, String direction);
    public ProductStatistics getStatistics();
    public List<CategoryStatistics> getStatisticsByCategory();
}
