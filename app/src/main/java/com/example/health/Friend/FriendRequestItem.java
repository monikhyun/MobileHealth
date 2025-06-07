package com.example.health.Friend;

public class FriendRequestItem {
    private String userId;
    private String username;
    private String grade;
    private String imageUrl;

    public FriendRequestItem(String userId, String username, String grade, String imageUrl) {
        this.userId = userId;
        this.username = username;
        this.grade = grade;
        this.imageUrl = imageUrl;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getGrade() { return grade; }
    public String getImageUrl() { return imageUrl; }
}
