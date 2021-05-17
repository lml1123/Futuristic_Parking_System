package com.example.futuristic;

import java.util.Date;

public class Transaction {
    private String recordRef, transactionMode;
    private Double newBalance, oldBalance, paymentAmount, topUpAmount;
    private Date transactionTime, paymentTime;

    public String getRecordRef() {
        return recordRef;
    }

    public void setRecordRef(String recordRef) {
        this.recordRef = recordRef;
    }

    public String getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    public Double getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(Double newBalance) {
        this.newBalance = newBalance;
    }

    public Double getOldBalance() {
        return oldBalance;
    }

    public void setOldBalance(Double oldBalance) {
        this.oldBalance = oldBalance;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Double getTopUpAmount() {
        return topUpAmount;
    }

    public void setTopUpAmount(Double topUpAmount) {
        this.topUpAmount = topUpAmount;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }
}
