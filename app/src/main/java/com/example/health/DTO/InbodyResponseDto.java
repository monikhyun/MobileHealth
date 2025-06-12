package com.example.health.DTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

// 사용자 인바디 측정 결과를 응답으로 전달하기 위한 DTO 클래스
public class InbodyResponseDto implements Serializable {
    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;

    // 체중 (kg 단위)
    private BigDecimal weight;

    // 측정 날짜
    private LocalDate date;

    // 골격근량 (Skeletal Muscle Mass)
    private BigDecimal SMM;

    // 제지방량 (Lean Body Mass)
    private BigDecimal LBM;

    // 체질량지수 (Body Mass Index)
    private BigDecimal BMI;

    // 체지방률 (Fat Percentage)
    private BigDecimal fat_percent;

    // 모든 값을 초기화하는 생성자
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

    // 체중 반환
    public BigDecimal getWeight() {
        return weight;
    }

    // 측정 날짜 반환
    public LocalDate getDate() {
        return date;
    }

    // 골격근량 반환
    public BigDecimal getSMM() {
        return SMM;
    }

    // 제지방량 반환
    public BigDecimal getLBM() {
        return LBM;
    }

    // 체질량지수 반환
    public BigDecimal getBMI() {
        return BMI;
    }

    // 체지방률 반환
    public BigDecimal getFat_percent() {
        return fat_percent;
    }

    // 체중 설정
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    // 측정 날짜 설정
    public void setDate(LocalDate date) {
        this.date = date;
    }

    // 골격근량 설정
    public void setSMM(BigDecimal SMM) {
        this.SMM = SMM;
    }

    // 제지방량 설정
    public void setLBM(BigDecimal LBM) {
        this.LBM = LBM;
    }

    // 체질량지수 설정
    public void setBMI(BigDecimal BMI) {
        this.BMI = BMI;
    }

    // 체지방률 설정
    public void setFat_percent(BigDecimal fat_percent) {
        this.fat_percent = fat_percent;
    }
}