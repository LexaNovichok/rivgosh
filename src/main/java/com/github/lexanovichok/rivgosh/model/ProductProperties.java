package com.github.lexanovichok.rivgosh.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class ProductProperties {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int productId;

    private String propertyName;

    private String propertyValue;

    private String propertyPrice; //Как размер влияет на цену
}
