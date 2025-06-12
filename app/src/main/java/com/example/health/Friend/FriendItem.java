package com.example.health.Friend;

// 친구 목록에서 개별 친구 항목을 나타내는 데이터 클래스
// 사용자 ID, 닉네임, 등급, 프로필 이미지 URL, 친구 요청 여부 등을 포함
public class FriendItem {

    // 사용자의 고유 ID (서버 식별용)
    private String userid;

    // 사용자 닉네임
    private String username;

    // 사용자 등급 (예: 일반, 실버, 골드 등)
    private String grade;

    // 사용자 프로필 이미지 URL
    private String imageUrl;

    // 친구 요청 상태 (true: 이미 요청 보냄, false: 아직 요청 안 함)
    private boolean requested;

    // 생성자: 요청 상태를 기본값 false로 초기화
    public FriendItem(String userid, String username, String grade, String imageUrl) {
        this.userid = userid;
        this.username = username;
        this.grade = grade;
        this.imageUrl = imageUrl;
        this.requested = false; // 기본값 설정
    }

    // 생성자: 요청 상태를 포함하여 전체 초기화
    public FriendItem(String userid, String username, String grade, String imageUrl, boolean requested) {
        this.userid = userid;
        this.username = username;
        this.grade = grade;
        this.imageUrl = imageUrl;
        this.requested = requested;
    }

    // 사용자 ID 반환
    public String getUserid() {
        return userid;
    }

    // 사용자 닉네임 반환
    public String getUsername() {
        return username;
    }

    // 프로필 이미지 URL 반환
    public String getImageUrl() {
        return imageUrl;
    }

    // 등급 반환
    public String getGrade() {
        return grade;
    }

    // 친구 요청 여부 반환
    public boolean isRequested() {
        return requested;
    }

    // 친구 요청 여부 설정
    public void setRequested(boolean requested) {
        this.requested = requested;
    }
}