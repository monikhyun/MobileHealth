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
        Member from = memberRepository.findMemberByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("보내는 유저 없음"));
        Member to = memberRepository.findMemberByUserId(followerId)
                .orElseThrow(() -> new IllegalArgumentException("받는 유저 없음"));

        boolean alreadyExists = followRepository.existsByFromMemberAndToMember(from, to);
        if (alreadyExists) {
            throw new IllegalStateException("이미 팔로우 요청이 존재합니다.");
        }

        Follow follow = Follow.builder()
                .fromMember(from)
                .toMember(to)
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
        Member toMember = memberRepository.findMemberByUserId(userId).orElseThrow();
        Member fromMember = memberRepository.findMemberByUserId(followingId).orElseThrow();

        // 1. 기존 요청을 ACCEPTED로 변경
        Follow follow = followRepository.findByFromMemberAndToMember(fromMember, toMember);
        follow.setStatus(FollowStatus.ACCEPTED);
        followRepository.save(follow);

        // 2. 역방향 팔로우가 이미 존재하는지 확인
        boolean existsReverse = followRepository.existsByFromMemberAndToMember(toMember, fromMember);
        if (!existsReverse) {
            // 3. 역방향 팔로우 추가
            Follow reverseFollow = Follow.builder()
                    .fromMember(toMember)
                    .toMember(fromMember)
                    .status(FollowStatus.ACCEPTED)
                    .build();
            followRepository.save(reverseFollow);
        }
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
    public List<Member> findUsers(String userId, String username) {
        return memberRepository.searchExcludingFollowed(username, userId);
    }

    // 팔로우 요청 취소
    @Override
    public void followCancel(String userId, String followerId) {
        Member from = memberRepository.findMemberByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("보낸 유저 없음"));
        Member to = memberRepository.findMemberByUserId(followerId)
                .orElseThrow(() -> new IllegalArgumentException("받는 유저 없음"));

        Follow follow = followRepository.findByFromMemberAndToMember(from, to);

        // 요청 상태일 경우만 삭제
        if (follow.getStatus() == FollowStatus.REQUESTED) {
            followRepository.delete(follow);
        } else {
            throw new IllegalStateException("요청 상태가 아니므로 취소할 수 없습니다.");
        }
    }
}
