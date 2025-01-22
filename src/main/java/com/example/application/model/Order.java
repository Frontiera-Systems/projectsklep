package com.example.application.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetails> orderDetails;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private String imie;
    private String nazwisko;
    private String adres;
    private String kodpocztowy;
    private String Miasto;
    private String telefon;
    private String kurier;
    private Long numerprzewozowy;

    public Double calculateTotalPrice() {
        return orderDetails.stream()
                .mapToDouble(od -> od.getPrice() * od.getQuantity())
                .sum();
    }

}
