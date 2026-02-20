package com.exercice1.demo.dto;

import com.exercice1.demo.model.Customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequest {
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Le téléphone doit contenir 10 chiffres")
    private String phone;

    @Size(max = 500)
    private String address;

    public CustomerRequest(Customer customer) {
        firstName = customer.getFirstName();
        lastName = customer.getLastName();
        email = customer.getEmail();
        phone = customer.getPhone();
        address = customer.getAddress();
    }

   
}