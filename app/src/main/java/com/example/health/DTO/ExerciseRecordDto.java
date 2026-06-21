package com.example.health.DTO;

import java.math.BigDecimal;

// 운동 기록을 전달하기 위한 DTO 클래스
public class ExerciseRecordDto {
    // 세트 수 (예: 3세트)
    private Integer setCount;

    // 각 세트당 반복 횟수 (예: 10회)
    private Integer count;

    // 각 반복에서의 중량 (예: 40.5kg 등)
    private BigDecimal weight;

    // 운동 완료 여부 (true: 완료, false: 미완료)
    private Boolean done;

    // 모든 필드를 초기화하는 생성자
    public ExerciseRecordDto(Integer setCount, Integer count, BigDecimal weight, Boolean done) {
        this.setCount = setCount;
        this.count    = count;
        this.weight   = weight;
        this.done     = done;
    }

    // 세트 수 반환
    public Integer getSetCount() { return setCount; }

    // 세트 수 설정
    public void setSetCount(Integer setCount) { this.setCount = setCount; }

    // 반복 횟수 반환
    public Integer getCount() { return count; }

    // 반복 횟수 설정
    public void setCount(Integer count) { this.count = count; }

    // 중량 반환
    public BigDecimal getWeight() { return weight; }

    // 중량 설정
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    // 완료 여부 반환
    public Boolean getDone() { return done; }

    // 완료 여부 설정
    public void setDone(Boolean done) { this.done = done; }
}