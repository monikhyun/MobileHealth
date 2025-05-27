package mobile.health.healine.Service;

import lombok.RequiredArgsConstructor;
import mobile.health.healine.Entity.Follow;
import mobile.health.healine.Entity.FollowStatus;
import mobile.health.healine.Entity.Member;
import mobile.health.healine.Repository.FollowRepository;
import mobile.health.healine.Repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    // 팔로워 유저 조회
    @Override
    public List<Member> findFollowerUsers(String userId) {
        if(memberRepository.findMemberByUserId(userId).isEmpty()){
            return List.of();
        }
        return followRepository.findFollowers(memberRepository.findMemberByUserId(userId).get());
    }

    // 팔로잉 유저 조회
    @Override
    public List<Member> findFollowingUsers(String userId) {
        if(memberRepository.findMemberByUserId(userId).isEmpty()){
            return List.of();
        }
        return followRepository.findFollowings(memberRepository.findMemberByUserId(userId).get());
    }

    // 팔로우 요청
    @Override
    public void followRequest(String userId, String followerId) {
        Follow follow = Follow.builder()
                .fromMember(memberRepository.findMemberByUserId(userId).get())
                .toMember(memberRepository.findMemberByUserId(followerId).get())
                .status(FollowStatus.REQUESTED)
                .build();

        followRepository.save(follow);
    }

    // 팔로우 시 대상 닉네임 반환
    @Override
    public String getTargetName(String userId) {
        if(memberRepository.findMemberByUserId(userId).isEmpty()){
            return "";
        }
        return memberRepository.findMemberByUserId(userId).get().getUsername();
    }

    // 팔로우 요청 목록 조회
    @Override
    public List<Member> findFollowRequest(String userId) {
        if(memberRepository.findMemberByUserId(userId).isEmpty()){
            return List.of();
        }
        return followRepository.findFollowRequests(memberRepository.findMemberByUserId(userId).get());
    }

    // 팔로우 승인
    @Override
    public void followAccept(String userId, String followingId) {
        Follow follow = followRepository.findByFromMemberAndToMember(
                memberRepository.findMemberByUserId(followingId).get(),
                memberRepository.findMemberByUserId(userId).get()
        );
        follow.setStatus(FollowStatus.ACCEPTED);
        followRepository.save(follow);
    }

    // 팔로우 거절
    @Override
    public void followReject(String userId, String followingId) {
        Follow follow = followRepository.findByFromMemberAndToMember(
                memberRepository.findMemberByUserId(followingId).get(),
                memberRepository.findMemberByUserId(userId).get()
        );
        follow.setStatus(FollowStatus.REJECTED);
        followRepository.save(follow);
    }

    // 유저 검색
    @Override
    public List<Member> findUsers(String username) {
        return memberRepository.findMemberByUsernameContaining(username);
    }
}
