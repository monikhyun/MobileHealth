package mobile.health.healine.Entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BodyPart {
    BACK("등"),
    LOWER_BODY("하체"),
    SHOULDERS("어깨"),
    CHEST("가슴"),
    ARMS("팔");

    private final String displayName;
    public String getDisplayName() {
        return displayName;
    }
    BodyPart(String displayName) {
        this.displayName = displayName;
    }
    public static BodyPart fromDisplayName(String name){
        for (BodyPart bp : values()){
            if (bp.displayName.equals(name)) return bp;
        }
        throw new IllegalArgumentException("Unknown BodyPart: " + name);
    }
    @Override
    public String toString() {
        return displayName;
    }

    @JsonValue
    public String toJson() { return displayName; }
}
