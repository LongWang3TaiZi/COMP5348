package com.usyd.backend.repository;

import com.usyd.backend.model.Address;
import com.usyd.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
