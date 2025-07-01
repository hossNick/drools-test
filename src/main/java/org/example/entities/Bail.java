package org.example.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Embeddable
public class Bail {

    private String paymentMethod;
    private String paymentDate;
    private Long bailAmount;


    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Long getBailAmount() {
        return bailAmount;
    }

    public void setBailAmount(Long bailAmount) {
        this.bailAmount = bailAmount;
    }
}
