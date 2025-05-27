package mobile.health.healine.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mobile.health.healine.Entity.Member;
import mobile.health.healine.Entity.dto.MemberDto;
import mobile.health.healine.Service.FollowServiceImpl;
import mobile.health.healine.Service.MemberDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/follow")
@Tag(name = "Follow", description = "팔로우 관련 API")
@RequiredArgsConstructor
public class FollowController {
    private final FollowServiceImpl followService;
    private final MemberDetailsService memberDetailsService;
    // 팔로워 목록 조회
    @GetMapping("/follower/{userId}")
    public ResponseEntity<List<MemberDto>> findFollowerUsers(@PathVariable String userId){
        List<Member> followerUsers = followService.findFollowerUsers(userId);
        List<MemberDto> followerDtos = followerUsers.stream()
                .map(member -> MemberDto.builder()
                        .userid(member.getUserId())
                        .username(member.getUsername())
                        .grade(member.getGrade())
                        .imageUrl(member.getImageUrl())
                        .build())
                .collect(Collectors.toList());
      return ResponseEntity.ok(followerDtos);
    }
    // 팔로잉 목록 조회
    @GetMapping("/following/{userId}")
    public ResponseEntity<List<MemberDto>> findFollowingUsers(@PathVariable String userId){
        List<Member> followerUsers = followService.findFollowingUsers(userId);
        List<MemberDto> followerDtos = followerUsers.stream()
                .map(member -> MemberDto.builder()
                        .userid(member.getUserId())
                        .username(member.getUsername())
                        .grade(member.getGrade())
                        .imageUrl(member.getImageUrl())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(followerDtos);
    }

    // 팔로우 요청
    @PostMapping("/request/{userId}/{followId}")
    public ResponseEntity<String> followRequest(@PathVariable String userId, @PathVariable String followId) {
        followService.followRequest(userId, followId);
        return ResponseEntity.ok(followService.getTargetName(followId)+"님에게 팔로우 요청!");
    }
    // 팔로우 요청 목록 조회
    @GetMapping("/{userID}/follow/request")
    public ResponseEntity<List<MemberDto>> findFollowRequest(@PathVariable String userID){
        List<Member> members =followService.findFollowRequest(userID);
        List<MemberDto> memberDtos = members.stream()
                .map(member -> MemberDto.builder()
                        .userid(member.getUserId())
                        .username(member.getUsername())
                        .grade(member.getGrade())
                        .imageUrl(member.getImageUrl())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(memberDtos);
    }
    // 팔로우 승인
    @PostMapping("/{userId}/follow/request/accept/{username}")
    public ResponseEntity<String> followAccept(@PathVariable String userId, @PathVariable String username){
        followService.followAccept(userId, memberDetailsService.userIdByUsername(username));
        return ResponseEntity.ok(username+"님의 팔로우 요청 수락");
    }

    // 팔로우 거절
    @PostMapping("/{userId}/follow/request/reject/{username}")
    public ResponseEntity<String> followReject(@PathVariable String userId, @PathVariable String username) {
        followService.followReject(userId, memberDetailsService.userIdByUsername(username));
        return ResponseEntity.ok(username + "님의 팔로우 요청 거절");
    }
    // 유저 찾기
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<MemberDto>> findUsers(@PathVariable String keyword){
        List<Member> members = followService.findUsers(keyword);
        List<MemberDto> memberDtos = members.stream()
                .map(member -> MemberDto.builder()
                        .userid(member.getUserId())
                        .username(member.getUsername())
                        .grade(member.getGrade())
                        .imageUrl(member.getImageUrl())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(memberDtos);
    }

}
