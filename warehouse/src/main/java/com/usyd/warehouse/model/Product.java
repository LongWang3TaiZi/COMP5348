package com.usyd.warehouse.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Version
    private Long version;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private Set<WarehouseProduct> warehouseProducts;

    private LocalDateTime modifyTime;
}
