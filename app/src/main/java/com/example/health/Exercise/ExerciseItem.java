package com.example.health.Exercise;

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

