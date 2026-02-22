package com.exercice1.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.exercice1.demo.model.Customer;
import com.exercice1.demo.model.Order;
import com.exercice1.demo.repository.CustomerRepository;
import com.exercice1.demo.repository.OrderRepository;
import com.exercice1.security.model.User;
import com.exercice1.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component("securityUtils")
@RequiredArgsConstructor
public class SecurityUtils {
    
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public boolean isCurrentUser(Long userId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return false;
        }

        return user.getId().equals(userId);
    }
    
    /**
     * Vérifie si l'utilisateur connecté est le propriétaire du Customer
     */
    public boolean isOwner(Long customerId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return false;
        }
        
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return false;
        }
        
        // Vérifier si le customer est associé à cet utilisateur
        // (suppose que Customer a un email correspondant à User.email)
        return customer.getEmail().equals(user.getEmail());
    }
    
    /**
     * Vérifie si l'utilisateur connecté est le propriétaire de la commande
     */
    public boolean isOrderOwner(Long orderId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return false;
        }
        
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return false;
        }
        
        // Vérifier si la commande appartient au customer de cet utilisateur
        return order.getCustomer().getEmail().equals(user.getEmail());
    }
}