package org.example.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Embeddable
public class Bail {

    private Long amount;
    @Column(name = "payment_method")
    private String paymentMethod;
    private LocalDate paymentDate;

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

}
