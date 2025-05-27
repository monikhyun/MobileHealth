package mobile.health.healine.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.health.healine.Entity.Member;
import mobile.health.healine.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@Lazy
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findMemberByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("없거나 잘못된 아이디: " + userId));

        return User.builder()
                .username(member.getUserId())           // 로그인 ID
                .password(member.getPassword())         // 암호화된 비밀번호
                .authorities(member.getRole().name()) // ROLE_... 포맷
                .build();
    }


    public String userIdByUsername(String username) {
        return memberRepository.findMemberByUserId(username).orElseThrow().getUserId();
    }
}
