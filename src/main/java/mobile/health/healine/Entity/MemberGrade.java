package mobile.health.healine.Entity;

public enum MemberGrade {
    SEED,
    SPROUT,
    STEMS,
    TREE;

    public MemberGrade getNext() {
        return switch (this) {
            case SEED -> SPROUT;
            case SPROUT -> STEMS;
            case STEMS -> TREE;
            case TREE -> null; // 최종 등급
        };
    }
    public static long requiredDays(MemberGrade grade) {
        return switch (grade) {
            case SEED -> 10;
            case SPROUT -> 30;
            case STEMS -> 60;
            case TREE -> 60;
        };
    }
}
