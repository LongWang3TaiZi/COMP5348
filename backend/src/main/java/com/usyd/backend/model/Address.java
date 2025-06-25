package com.usyd.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue
    private long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private int postCode;

    @Column(nullable = false)
    private String address;

    @Override
    public String toString() {
        return  address + "," + postCode + "," + city + "," + state + "," + country;
    }
}
