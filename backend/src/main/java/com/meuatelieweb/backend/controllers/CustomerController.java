package com.meuatelieweb.backend.controllers;

import com.meuatelieweb.backend.domain.customer.CustomerService;
import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.SaveCustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.UpdateCustomerDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("customers")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @GetMapping
    public ResponseEntity<Page<CustomerDTO>> findAll(
            Pageable pageable,
            @RequestParam(name = "name", required = false)
            String name,
            @RequestParam(name = "email", required = false)
            String email,
            @RequestParam(name = "phone", required = false)
            String phone,
            @RequestParam(name = "isActive", required = false)
            Boolean isActive
    ) {
        return ResponseEntity.ok().body(service.findAll(pageable, name, email, phone, isActive));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> findById(@PathVariable UUID id) {
        CustomerDTO customerDTO = service.findById(id);

        return ResponseEntity.ok().body(customerDTO);
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> addCustomer(
            @RequestBody @Valid SaveCustomerDTO saveCustomerDTO,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        CustomerDTO savedCustomer = service.addCustomer(saveCustomerDTO);

        URI uri = uriComponentsBuilder
                .path("/{id}")
                .buildAndExpand(savedCustomer.getId())
                .toUri();

        return ResponseEntity.created(uri).body(savedCustomer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable
            UUID id,
            @RequestBody
            @Valid
            UpdateCustomerDTO updateCustomerDTO
    ) {
        CustomerDTO updatedCustomer = service.updateCustomer(id, updateCustomerDTO);

        return ResponseEntity.status(HttpStatus.OK).body(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        service.deleteCustomer(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
