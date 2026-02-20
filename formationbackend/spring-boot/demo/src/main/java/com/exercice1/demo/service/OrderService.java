package com.exercice1.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.exercice1.demo.dto.OrderItemRequest;
import com.exercice1.demo.dto.OrderRequest;
import com.exercice1.demo.dto.OrderResponse;
import com.exercice1.demo.dto.OrderStatisticsResponse;
import com.exercice1.demo.dto.PagedResponse;
import com.exercice1.demo.exception.InsufficientStockException;
import com.exercice1.demo.exception.OrderCancelledException;
import com.exercice1.demo.model.Customer;
import com.exercice1.demo.model.Order;
import com.exercice1.demo.model.OrderItem;
import com.exercice1.demo.model.Product;
import com.exercice1.demo.model.enums.OrderStatus;
import com.exercice1.demo.repository.CustomerRepository;
import com.exercice1.demo.repository.OrderItemRepository;
import com.exercice1.demo.repository.OrderRepository;
import com.exercice1.demo.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Customer customer = customerRepository.findById(request.customerId()).orElseThrow();
        Order order = orderRepository.save(new Order(null, customer,null, OrderStatus.PENDING, null, null));
        List<OrderItem> lOrderItems = createOrderItems(order, request.items());
        Double sumOrderTotal = lOrderItems.stream().mapToDouble((iOrder) -> iOrder.getSubtotal()).sum();
        order.setItems(lOrderItems);
        order.setTotalAmount(sumOrderTotal);
        orderRepository.save(order);
        return new OrderResponse(order);

    }

    @Transactional
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        return new OrderResponse(order);
    }

    @Transactional
    public PagedResponse<OrderResponse> getAllOrders(int page, int size, String sortBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Order> orderPaged = orderRepository.findAll(pageRequest);
        Page<OrderResponse> orderResponse = orderPaged.map(OrderResponse::new);
        return new PagedResponse<OrderResponse>(orderResponse);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id).orElseThrow();
        if (!order.getStatus().equals(OrderStatus.DELIVERED)) {
            order.setStatus(newStatus);
            orderRepository.save(order);
            return new OrderResponse(order);
        } else {
            throw new OrderCancelledException(id);
        }

    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Transactional
    public PagedResponse<OrderResponse> getOrdersByStatus(OrderStatus status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Order> orderPaged = orderRepository.findByStatus(status, pageRequest);
        Page<OrderResponse> orderResponse = orderPaged.map(OrderResponse::new);
        return new PagedResponse<OrderResponse>(orderResponse);
    }

    @Transactional
    public OrderStatisticsResponse getOrderStatistics() {
        Long totalVente = orderRepository.countByStatus(OrderStatus.DELIVERED);
        Double SumTotalVente = orderRepository.calculateTotalRevenue();
        return new OrderStatisticsResponse(totalVente, SumTotalVente);
    }

    @Transactional
    private List<OrderItem> createOrderItems(Order order, List<OrderItemRequest> itemRequests) {
        List<OrderItem> lOrderItems = itemRequests.stream()
                .map((orderItem) -> {
                    Product product = productRepository.findById(orderItem.productId()).orElseThrow();
                    if (product.getStock() >= orderItem.quantity()) {
                        product.setStock(product.getStock() - orderItem.quantity());
                        productRepository.save(product);
                    } else {
                        throw new InsufficientStockException(product.getName(), orderItem.quantity(),
                                product.getStock());
                    }
                    return new OrderItem(null, order, product, orderItem.quantity(), product.getPrice(), null);
                })
                .collect(Collectors.toList());
        return orderItemRepository.saveAll(lOrderItems);
    }
}
