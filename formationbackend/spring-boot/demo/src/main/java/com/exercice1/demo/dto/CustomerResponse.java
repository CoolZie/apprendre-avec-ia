package com.exercice1.demo.dto;

import java.time.LocalDateTime;

import com.exercice1.demo.model.Customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CustomerResponse {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private LocalDateTime createdAt;

        public CustomerResponse(Customer customer) {
                this.firstName = customer.getFirstName();
                this.lastName = customer.getFirstName();
                this.email = customer.getEmail();
                this.phone = customer.getPhone();
                this.address = customer.getAddress();
                this.createdAt = customer.getCreatedAt();
        }
}
