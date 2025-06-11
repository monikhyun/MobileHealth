<<<<<<<< HEAD:app/src/main/java/com/example/health/DTO/ExerciseDto.java
package com.example.health.DTO;
========
package com.example.resister.DTO;
>>>>>>>> feat/diet:app/src/main/java/com/example/resister/DTO/ExerciseDto.java

public class ExerciseDto {
    public final String bodyPart;
    public final String exerciseName;

    public ExerciseDto(String bodyPart, String exerciseName) {
        this.bodyPart = bodyPart;
        this.exerciseName = exerciseName;
    }
}
