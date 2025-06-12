package com.example.health.Exercise;

/**
 * 운동 항목 하나를 표현하는 데이터 클래스
 * 운동 부위, 운동 이름, 수행 여부(done)를 담고 있음
 */
public class ExerciseItem {

    /**
     * 운동 부위 (예: 가슴, 등, 하체 등)
     */
    public final String bodyPart;

    /**
     * 운동 이름 (예: 벤치프레스, 스쿼트 등)
     */
    public final String exerciseName;

    /**
     * 운동 완료 여부 (true: 수행 완료, false: 미수행 또는 예정)
     */
    public final boolean done;

    /**
     * 생성자 - 운동 항목 정보 초기화
     * @param bodyPart 운동 부위 설정
     * @param exerciseName 운동 이름 설정
     * @param done 완료 여부 설정
     */
    public ExerciseItem(String bodyPart, String exerciseName, boolean done) {
        this.bodyPart = bodyPart;           // 운동 부위 설정
        this.exerciseName = exerciseName;   // 운동 이름 설정
        this.done = done;                   // 완료 여부 설정
    }

    /**
     * 운동 부위 반환
     * @return 운동 부위 문자열
     */
    public String getBodyPart() {
        return bodyPart;
    }

    /**
     * 운동 이름 반환
     * @return 운동 이름 문자열
     */
    public String getExerciseName() {
        return exerciseName;
    }

    /**
     * 완료 여부 반환
     * @return 운동 완료 여부 (true: 완료, false: 미완료)
     */
    public boolean isDone() {
        return done;
    }
}
