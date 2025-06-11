<<<<<<<< HEAD:app/src/main/java/com/example/health/Exercise/ExerciseItem.java
package com.example.health.Exercise;
========
package com.example.resister.Request.Exercise;
>>>>>>>> feat/diet:app/src/main/java/com/example/resister/Request/Exercise/ExerciseItem.java

public  class ExerciseItem {
    public final String bodyPart;
    public final String exerciseName;
    public final boolean done;


    public ExerciseItem(String bodyPart, String exerciseName, boolean done) {
        this.bodyPart = bodyPart;
        this.exerciseName = exerciseName;
        this.done = done;

    }

    public String getBodyPart() {
        return bodyPart;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public boolean isDone() {
        return done;
    }
}

