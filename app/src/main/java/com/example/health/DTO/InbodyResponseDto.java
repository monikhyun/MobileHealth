package com.example.health.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InbodyResponseDto {

    private BigDecimal weight;
    private LocalDate date;
    private BigDecimal SMM;
    private BigDecimal LBM;
    private BigDecimal BMI;
    private BigDecimal fat_percent;

    // 생성자
    public InbodyResponseDto(BigDecimal weight, LocalDate date, BigDecimal SMM, BigDecimal LBM, BigDecimal BMI, BigDecimal fat_percent) {
        this.weight = weight;
        this.date = date;
        this.SMM = SMM;
        this.LBM = LBM;
        this.BMI = BMI;
        this.fat_percent = fat_percent;
    }

    // 기본 생성자
    public InbodyResponseDto() {}

    // Getter
    public BigDecimal getWeight() {
        return weight;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getSMM() {
        return SMM;
    }

    public BigDecimal getLBM() {
        return LBM;
    }

    public BigDecimal getBMI() {
        return BMI;
    }

    public BigDecimal getFat_percent() {
        return fat_percent;
    }

    // Setter
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setSMM(BigDecimal SMM) {
        this.SMM = SMM;
    }

    public void setLBM(BigDecimal LBM) {
        this.LBM = LBM;
    }

    public void setBMI(BigDecimal BMI) {
        this.BMI = BMI;
    }

    public void setFat_percent(BigDecimal fat_percent) {
        this.fat_percent = fat_percent;
    }
}