package com.exercice1.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exercice1.demo.dto.OrderRequest;
import com.exercice1.demo.dto.OrderResponse;
import com.exercice1.demo.dto.OrderStatisticsResponse;
import com.exercice1.demo.dto.PagedResponse;
import com.exercice1.demo.model.enums.OrderStatus;
import com.exercice1.demo.service.OrderService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
     @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN') or @securityUtils.isOrderOwner(#id, authentication)")
    public OrderResponse getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping
     @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public PagedResponse<OrderResponse> getAllorder(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        return orderService.getAllOrders(page, size, sortBy, direction);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public OrderResponse pathOrder(@PathVariable Long id, @RequestParam String status) {
        return orderService.updateOrderStatus(id, OrderStatus.valueOf(status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return "Commande annulee";
    }

    @GetMapping("/status/{status}")
    public PagedResponse<OrderResponse> getOrdersByStatus(@PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.getOrdersByStatus(OrderStatus.valueOf(status), page, size);
    }

    @GetMapping("/statistics")
    public OrderStatisticsResponse getOrderStatistics() {
        return orderService.getOrderStatistics();
    }

}

/*
 * POST /api/orders - Créer une commande
 * GET /api/orders/{id} - Détail d'une commande
 * GET /api/orders - Liste paginée des commandes
 * PATCH /api/orders/{id}/status - Modifier le statut
 * DELETE /api/orders/{id} - Annuler une commande
 * GET /api/orders/status/{status} - Commandes par statut
 * GET /api/orders/statistics - Statistiques
 */