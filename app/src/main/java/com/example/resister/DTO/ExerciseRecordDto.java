package com.example.resister.DTO;

import java.math.BigDecimal;

public class ExerciseRecordDto {
    private Integer setCount;
    private Integer count;
    private BigDecimal weight;
    private Boolean done;

    // 생성자
    public ExerciseRecordDto(Integer setCount, Integer count, BigDecimal weight, Boolean done) {
        this.setCount = setCount;
        this.count    = count;
        this.weight   = weight;
        this.done     = done;
    }

    // Getter/Setter
    public Integer getSetCount() { return setCount; }
    public void setSetCount(Integer setCount) { this.setCount = setCount; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public Boolean getDone() { return done; }
    public void setDone(Boolean done) { this.done = done; }
}