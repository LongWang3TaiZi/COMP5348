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
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;

    @OneToMany(mappedBy = "warehouse")
    @JsonManagedReference
    private Set<WarehouseProduct> warehouseProducts;

    public Warehouse(String name, String location) {
        this.name = name;
        this.location = location;
    }

    @Version
    private Long version;

    private LocalDateTime modifyTime;
}
