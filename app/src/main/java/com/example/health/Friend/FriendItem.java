package com.example.health.Friend;

public class FriendItem {
    private String userid;
    private String username;
    private String grade;
    private String imageUrl;
    private boolean requested; // 추가됨

    public FriendItem(String userid, String username, String grade, String imageUrl) {
        this.userid = userid;
        this.username = username;
        this.grade = grade;
        this.imageUrl = imageUrl;
        this.requested = false; // 기본값 설정
    }

    // requested 포함하는 생성자 (선택사항)
    public FriendItem(String userid, String username, String grade, String imageUrl, boolean requested) {
        this.userid = userid;
        this.username = username;
        this.grade = grade;
        this.imageUrl = imageUrl;
        this.requested = requested;
    }

    // Getter / Setter
    public String getUserid() {
        return userid;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getGrade() {
        return grade;
    }

    public boolean isRequested() {
        return requested;
    }

    public void setRequested(boolean requested) {
        this.requested = requested;
    }
}