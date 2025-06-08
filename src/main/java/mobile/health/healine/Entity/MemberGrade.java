package mobile.health.healine.Entity;

public enum MemberGrade {
    SEED(0, 20, "SPROUT"),
    SPROUT(20, 40, "STEMS"),
    STEMS(40, 150, "TREE"),
    TREE(150, 150, null); // 마지막 등급은 min == max

    private final long minDays;
    private final long maxDays;
    private final String nextName;
    private MemberGrade next;

    MemberGrade(long minDays, long maxDays, String nextName) {
        this.minDays = minDays;
        this.maxDays = maxDays;
        this.nextName = nextName;
    }

    public boolean matches(long days) {
        return days >= minDays && days < maxDays;
    }

    public MemberGrade getNext() {
        return next;
    }

    public long getRequiredDaysToReach() {
        return maxDays;
    }

    public static MemberGrade fromDays(long days) {
        for (MemberGrade grade : values()) {
            if (grade.matches(days)) return grade;
        }
        return TREE;
    }

    static {
        for (MemberGrade grade : values()) {
            if (grade.nextName != null) {
                grade.next = MemberGrade.valueOf(grade.nextName);
            }
        }
    }
}