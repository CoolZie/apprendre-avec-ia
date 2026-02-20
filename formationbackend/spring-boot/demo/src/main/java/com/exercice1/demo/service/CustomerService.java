package com.exercice1.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.exercice1.demo.dto.CustomerRequest;
import com.exercice1.demo.dto.CustomerResponse;
import com.exercice1.demo.dto.OrderResponse;
import com.exercice1.demo.dto.PagedResponse;
import com.exercice1.demo.exception.CustomerException;
import com.exercice1.demo.model.Customer;
import com.exercice1.demo.model.Order;
import com.exercice1.demo.repository.CustomerRepository;
import com.exercice1.demo.repository.OrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request){
        Customer createdCustomer = Customer.builder()
        .address(request.getAddress())
        .email(request.getEmail())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .phone(request.getPhone())
        .build();
        Customer customer = customerRepository.save(createdCustomer);
        return new CustomerResponse(customer);
    }
    @Transactional
    public CustomerResponse getCustomerById(Long id){
        Customer customer = customerRepository.findById(id).orElseThrow();
        return new CustomerResponse(customer);
    }
    @Transactional
    public PagedResponse<CustomerResponse> getAllCustomers(int page, int size, String sortBy, String direction){
        Sort sort = direction.equals("DESC")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Customer> costumerPaged = customerRepository.findAll(pageRequest);
        Page<CustomerResponse> productsResponse = costumerPaged.map(CustomerResponse::new);
        return new PagedResponse<CustomerResponse>(productsResponse);

    }
    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request){
        Customer customer = customerRepository.findById(id).orElseThrow();
        customer.setAddress(request.getAddress());
        customer.setEmail(request.getEmail());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhone(request.getPhone());
        customerRepository.save(customer);
        return new CustomerResponse(customer);
    }
    @Transactional
    public void deleteCustomer(Long id){
        Customer customer = customerRepository.findById(id).orElseThrow();
        if (customer.getOrders().size() > 0) {
            throw new CustomerException("Client possede des commande");
        }
        customerRepository.delete(customer);
    }
    @Transactional
    public PagedResponse<OrderResponse> getCustomerOrders(Long customerId, int page, int size){
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Order> orderPaged = orderRepository.findByCustomerId(customerId,pageRequest);
        Page<OrderResponse> orderResponse = orderPaged.map(OrderResponse::new);
        return new PagedResponse<OrderResponse>(orderResponse);

    }
}
