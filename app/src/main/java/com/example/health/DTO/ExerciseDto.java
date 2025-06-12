// src/main/java/com/example/health/DTO/ExerciseDto.java
package com.example.health.DTO;

public class ExerciseDto {
    public final String bodyPart;
    public final String exerciseName;
    public final boolean isFavorite;  // 추가된 필드

    // 기존 생성자 (favorite 정보 없이도 기존 코드가 동작하도록)
    public ExerciseDto(String bodyPart, String exerciseName) {
        this(bodyPart, exerciseName, false);
    }

    // 새로 오버로드된 생성자
    public ExerciseDto(String bodyPart, String exerciseName, boolean isFavorite) {
        this.bodyPart = bodyPart;
        this.exerciseName = exerciseName;
        this.isFavorite = isFavorite;
    }

    public String getBodyPart()     { return bodyPart; }
    public String getExerciseName() { return exerciseName; }
    public boolean isFavorite()     { return isFavorite; }
}