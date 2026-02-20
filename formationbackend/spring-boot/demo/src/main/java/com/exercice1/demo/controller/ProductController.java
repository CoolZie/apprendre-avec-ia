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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Gestion des produits", description = "API REST pour la gestion complète du catalogue de produits")
public class ProductController {
    private final ProductService productService;

    @Operation(
        summary = "Rechercher des produits", 
        description = "Recherche des produits par mot-clé dans le nom ou la description avec pagination et tri",
        tags = {"2. Recherche et filtrage"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recherche effectuée avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres de recherche invalides")
    })
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ProductResponse>> searchProducts(
            @RequestParam(required = true) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(productService.searchProducts(keyword, page, size, sortBy, direction));
    }

    @Operation(
        summary = "Obtenir les produits par catégorie", 
        description = "Retourne tous les produits d'une catégorie donnée avec pagination",
        tags = {"2. Recherche et filtrage"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produits trouvés avec succès"),
            @ApiResponse(responseCode = "400", description = "Catégorie invalide")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<PagedResponse<ProductResponse>> getProductsByCategory(
            @PathVariable(required = true) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(productService.getProductsByCategory(category, page, size, sortBy, direction));
    }

    @Operation(
        summary = "Filtrer par intervalle de prix", 
        description = "Retourne les produits dont le prix est compris entre min et max",
        tags = {"2. Recherche et filtrage"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produits filtrés avec succès"),
            @ApiResponse(responseCode = "400", description = "Intervalle de prix invalide (min > max)")
    })
    @GetMapping("/price-range")
    public ResponseEntity<PagedResponse<ProductResponse>> getProductsByPriceRange(
            @RequestParam(required = true) Double min,
            @RequestParam(required = true) Double max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(productService.getProductsByPriceRange(min, max, page, size, sortBy, direction));
    }

    @Operation(
        summary = "Obtenir les produits à stock faible", 
        description = "Retourne les produits dont le stock est inférieur ou égal au seuil spécifié",
        tags = {"2. Recherche et filtrage"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produits à stock faible trouvés"),
            @ApiResponse(responseCode = "400", description = "Seuil invalide")
    })
    @GetMapping("/low-stock")
    public ResponseEntity<PagedResponse<ProductResponse>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(productService.getLowStockProducts(threshold, page, size, sortBy, direction));
    }

    @Operation(
        summary = "Filtrer les produits (multicritères)", 
        description = "Applique plusieurs filtres simultanément : catégorie, prix min/max, stock min/max, mot-clé",
        tags = {"2. Recherche et filtrage"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtrage effectué avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres de filtrage invalides")
    })
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
            @RequestParam(defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(productService.filterProducts(category, minPrice, maxPrice, minStock, maxStock,
                keyword, page, size, sortBy, direction));
    }

    @Operation(
        summary = "Obtenir les statistiques globales", 
        description = "Calcule les statistiques sur l'ensemble du catalogue : nombre total, valeur totale, prix moyen, stock total",
        tags = {"3. Statistiques"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques calculées avec succès")
    })
    @GetMapping("/statistics")
    public ResponseEntity<ProductStatistics> getStatistics() {
        return ResponseEntity.ok(productService.getStatistics());
    }

    @Operation(
        summary = "Obtenir les statistiques par catégorie", 
        description = "Calcule les statistiques groupées par catégorie de produit",
        tags = {"3. Statistiques"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques par catégorie calculées")
    })
    @GetMapping("/statistics/by-category")
    public ResponseEntity<List<CategoryStatistics>> getStatisticsByCategory() {
        return ResponseEntity.ok(productService.getStatisticsByCategory());
    }

    /*
     * 
     * 
     * 7. GET /api/products/statistics/by-category
     * Retour : List<CategoryStatistics>
     */
    @Operation(
        summary = "Lister tous les produits avec pagination", 
        description = "Retourne la liste complète des produits avec pagination et tri personnalisable",
        tags = {"1. CRUD de base"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits récupérée")
    })
    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> getAllProductsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(productService.getAllProducts(page, size, sortBy, direction));

    }

    @Operation(
        summary = "Rechercher un produit par nom (ancienne version)", 
        description = "Endpoint de compatibilité v1 - Recherche un produit par son nom exact",
        tags = {"4. Compatibilité"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produits trouvés")
    })
    @GetMapping("/v1/search")
    public ResponseEntity<List<ProductResponse>> getProductList(@RequestParam String name) {
        return ResponseEntity.ok(productService.getProductList(name));

    }

    @Operation(
        summary = "Obtenir un produit par ID", 
        description = "Retourne le détail complet d'un produit à partir de son identifiant",
        tags = {"1. CRUD de base"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit trouvé"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));

    }

    @Operation(
        summary = "Créer un nouveau produit", 
        description = "Ajoute un nouveau produit au catalogue avec validation des données",
        tags = {"1. CRUD de base"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produit créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou produit déjà existant"),
            @ApiResponse(responseCode = "409", description = "Un produit avec ce nom existe déjà")
    })
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @Operation(
        summary = "Modifier un produit existant", 
        description = "Met à jour toutes les informations d'un produit",
        tags = {"1. CRUD de base"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@RequestBody @Valid ProductRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok(productService.updateProduct(id, request));

    }

    @Operation(
        summary = "Supprimer un produit", 
        description = "Supprime définitivement un produit du catalogue",
        tags = {"1. CRUD de base"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produit supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();

    }

    @Operation(
        summary = "Modifier le stock d'un produit", 
        description = "Ajuste la quantité en stock d'un produit (ajout ou retrait)",
        tags = {"1. CRUD de base"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Quantité invalide ou stock négatif"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> patchStockProduct(@PathVariable Long id, @RequestParam Integer quantity) {
        ProductResponse product = productService.updateStock(id, quantity);
        return ResponseEntity.ok().body(product);

    }

}
