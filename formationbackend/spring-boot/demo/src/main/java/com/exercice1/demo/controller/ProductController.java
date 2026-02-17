package com.exercice1.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exercice1.demo.dto.CategoryStatistics;
import com.exercice1.demo.dto.PagedResponse;
import com.exercice1.demo.dto.ProductRequest;
import com.exercice1.demo.dto.ProductResponse;
import com.exercice1.demo.dto.ProductStatistics;
import com.exercice1.demo.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

@GetMapping("/search")
    public ResponseEntity<PagedResponse<ProductResponse>> searchProducts(
        @RequestParam(required = true) String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "name") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(productService.searchProducts(keyword,page, size, sortBy, direction));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<PagedResponse<ProductResponse>> getProductsByCategory(
        @PathVariable(required = true) String category,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "name") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(productService.getProductsByCategory(category,page, size, sortBy, direction));
    }

    @GetMapping("/price-range")
    public ResponseEntity<PagedResponse<ProductResponse>> getProductsByPriceRange(
        @RequestParam(required = true) Double min,
        @RequestParam(required = true) Double max,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "name") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(productService.getProductsByPriceRange(min,max,page, size, sortBy, direction));
    }

     @GetMapping("/low-stock")
    public ResponseEntity<PagedResponse<ProductResponse>> getLowStockProducts(
        @RequestParam(defaultValue = "10") Integer threshold,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "name") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(productService.getLowStockProducts(threshold,page, size, sortBy, direction));
    }

      @GetMapping("/filter")
    public ResponseEntity<PagedResponse<ProductResponse>> filterProducts(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(required = false) Integer minStock,
        @RequestParam(required = false) Integer maxStock,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "name") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(productService.filterProducts(category, minPrice, maxPrice, minStock, maxStock, keyword, page, size, sortBy, direction));
    }

       @GetMapping("/statistics")
    public ResponseEntity<ProductStatistics> getStatistics() {
        return ResponseEntity.ok(productService.getStatistics());
    }

       @GetMapping("/statistics/by-category")
    public ResponseEntity<List<CategoryStatistics>> getStatisticsByCategory() {
        return ResponseEntity.ok(productService.getStatisticsByCategory());
    }

 /*    


7. GET /api/products/statistics/by-category
Retour : List<CategoryStatistics>
 */

    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> getAllProductsWithPagination(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "name") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(productService.getAllProducts(page, size, sortBy, direction));

    }

    @GetMapping("/v1/search")
    public ResponseEntity<List<ProductResponse>> getProductList(@RequestParam String name) {
        return ResponseEntity.ok(productService.getProductList(name));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));

    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@RequestBody @Valid ProductRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok(productService.updateProduct(id, request));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();

    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> patchStockProduct(@PathVariable Long id, @RequestParam Integer quantity) {
        ProductResponse product = productService.updateStock(id, quantity);
        return ResponseEntity.ok().body(product);

    }


}
