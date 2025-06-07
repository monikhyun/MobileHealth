package mobile.health.healine.Service;

import mobile.health.healine.Entity.Member;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FollowService {
    // 팔로워 유저 조회
    List<Member> findFollowerUsers(String userId);
    // 팔로잉 유저 조회
    List<Member> findFollowingUsers(String userId);
    // 팔로우 요청
    void followRequest(String userId, String followerId);
    // 팔로우 요청 목록 조회
    List<Member> findFollowRequest(String userId);
    // 팔로우 수락
    void followAccept(String userId, String followerId);
    // 팔로우 거절
    void followReject(String userId, String followerId);
    // 유저 검색
    List<Member> findUsers(String userId,String username);
    // 팔로우 요청시 대상 닉네임 반환
    String getTargetName(String userId);
    // 팔로우 요청 취소
    void followCancel(String userId, String followerId);
}
