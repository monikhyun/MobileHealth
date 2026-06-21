// src/main/java/com/example/health/DTO/ExerciseDto.java
package com.example.health.DTO;

// 운동 정보 전송용 DTO 클래스
public class ExerciseDto {
    // 운동 부위 (예: 가슴, 등, 하체 등)
    public final String bodyPart;

    // 운동 이름 (예: 벤치프레스, 풀업 등)
    public final String exerciseName;

    // 즐겨찾기 여부 (true: 즐겨찾기 등록됨, false: 등록되지 않음)
    public final boolean isFavorite;

    // 즐겨찾기 정보 없이 생성할 때 기본값은 false로 설정
    public ExerciseDto(String bodyPart, String exerciseName) {
        this(bodyPart, exerciseName, false);
    }

    // 모든 필드를 명시적으로 설정하는 생성자
    public ExerciseDto(String bodyPart, String exerciseName, boolean isFavorite) {
        this.bodyPart = bodyPart;
        this.exerciseName = exerciseName;
        this.isFavorite = isFavorite;
    }

    // 운동 부위 반환
    public String getBodyPart()     { return bodyPart; }

    // 운동 이름 반환
    public String getExerciseName() { return exerciseName; }

    // 즐겨찾기 여부 반환
    public boolean isFavorite()     { return isFavorite; }
}