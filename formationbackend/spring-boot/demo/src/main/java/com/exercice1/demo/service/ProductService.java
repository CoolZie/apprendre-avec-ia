package com.exercice1.demo.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exercice1.demo.dto.CategoryStatistics;
import com.exercice1.demo.dto.PagedResponse;
import com.exercice1.demo.dto.ProductRequest;
import com.exercice1.demo.dto.ProductResponse;
import com.exercice1.demo.dto.ProductStatistics;
import com.exercice1.demo.exception.DuplicateResourceException;
import com.exercice1.demo.exception.InvalidDataException;
import com.exercice1.demo.exception.ResourceNotFoundException;
import com.exercice1.demo.model.Category;
import com.exercice1.demo.model.Product;
import com.exercice1.demo.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService implements ProductServiceInterface {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::new) // Plus élégant !
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id : " + id));
        return new ProductResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        boolean exists = productRepository.existsByName(request.getName());
        if (!exists) {
            Product product = productRepository.save(request.toEntity());
            return new ProductResponse(product);
        }
        throw new DuplicateResourceException("Un produit avec le nom " + request.getName() + " existe déjà");

    }

    @Override
    @Transactional
    public ProductResponse updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));
        Integer newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new InvalidDataException(
                    "Stock insuffisant. Stock actuel: " + product.getStock() + ", quantité demandée: " + quantity);
        }
        product.setStock(newStock);
        productRepository.save(product);
        return new ProductResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id : " + id));
        product.setCategory(Category.valueOf(request.getCategory()));
        product.setDescription(request.getDescription());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        productRepository.save(product);
        return new ProductResponse(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id : " + id));
        productRepository.delete(product);
    }

    @Override
    public List<ProductResponse> getProductList(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductListByCategory(Category category) {
        return productRepository.findByCategory(category).stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public PagedResponse<ProductResponse> getAllProducts(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(sortBy);
        if (direction.equals("DESC")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productRepository.findAll(pageable);
        Page<ProductResponse> productsResponse = products.map(ProductResponse::new);
        return new PagedResponse<>(productsResponse);
    }

    @Override
    public PagedResponse<ProductResponse> searchProducts(String keyword, int page, int size, String sortBy,
            String direction) {
        Sort sort = Sort.by(sortBy);
        if (direction.equals("DESC")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Product> productPaged = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, pageable);
        Page<ProductResponse> productsResponse = productPaged.map(ProductResponse::new);
        return new PagedResponse<ProductResponse>(productsResponse);
    }

    @Override
    public PagedResponse<ProductResponse> getProductsByCategory(String category, int page, int size, String sortBy,
            String direction) {
        Sort sort = Sort.by(sortBy);
        if (direction.equals("DESC")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Product> productPaged = productRepository.findByCategory(Category.valueOf(category.toUpperCase()),
                pageable);
        Page<ProductResponse> productsResponse = productPaged.map(ProductResponse::new);
        return new PagedResponse<ProductResponse>(productsResponse);
    }

    @Override
    public PagedResponse<ProductResponse> getProductsByPriceRange(Double min, Double max, int page, int size,
            String sortBy, String direction) {
        Sort sort = Sort.by(sortBy);
        if (direction.equals("DESC")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Product> productPaged = productRepository.findByPriceBetween(min, max, pageable);
        Page<ProductResponse> productsResponse = productPaged.map(ProductResponse::new);
        return new PagedResponse<ProductResponse>(productsResponse);
    }

    @Override
    public PagedResponse<ProductResponse> getLowStockProducts(Integer threshold, int page, int size, String sortBy,
            String direction) {
        Sort sort = Sort.by(sortBy);
        if (direction.equals("DESC")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Product> productPaged = productRepository.findByStockLessThan(threshold, pageable);
        Page<ProductResponse> productsResponse = productPaged.map(ProductResponse::new);
        return new PagedResponse<ProductResponse>(productsResponse);
    }

    @Override
    public PagedResponse<ProductResponse> filterProducts(String category, Double minPrice, Double maxPrice,
            Integer minStock, Integer maxStock, String keyword, int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(sortBy);
        if (direction.equals("DESC")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        Category cat = category != null ? Category.valueOf(category.toUpperCase()) : null;
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Product> productPaged = productRepository.findByFilters(cat, minPrice,
                maxPrice, minStock, maxStock, keyword, pageable);
        Page<ProductResponse> productsResponse = productPaged.map(ProductResponse::new);
        return new PagedResponse<ProductResponse>(productsResponse);
    }

    @Override
    public ProductStatistics getStatistics() {
        long totalProducts = productRepository.count();
        double totalValue = productRepository.calculateTotalStockValue();
        double averagePrice = productRepository.calculateAveragePrice();
        Map<String, Long> categoryCounts = productRepository.countByCategory().stream()
                .collect(Collectors.toMap(
                        obj -> obj[0].toString(),
                        obj -> (Long) obj[1]));
        long lowStockCount = productRepository.countByStockLessThan(10);
        long outOfStockCount = productRepository.countByStock(0);
        return ProductStatistics.builder()
                .averagePrice(averagePrice)
                .categoryCounts(categoryCounts)
                .lowStockCount(lowStockCount)
                .outOfStockCount(outOfStockCount)
                .totalProducts(totalProducts)
                .totalValue(totalValue).build();
    }

    @Override
    public List<CategoryStatistics> getStatisticsByCategory() {
        return productRepository.getStatisticsByCategory().stream()
                .map(obj -> CategoryStatistics.builder()
                        .category(obj[0].toString())
                        .productCount((Long) obj[1])
                        .averagePrice((Double) obj[2])
                        .totalValue((Double) obj[3])
                        .build()

                )
                .collect(Collectors.toList());
    }

}
