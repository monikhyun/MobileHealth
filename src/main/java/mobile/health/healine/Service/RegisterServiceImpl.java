package mobile.health.healine.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.health.healine.Entity.Member;
import mobile.health.healine.Entity.MemberGrade;
import mobile.health.healine.Entity.ROLE;
import mobile.health.healine.Entity.dto.ResisterDto;
import mobile.health.healine.Repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final MemberRepository memberRepository;

    @Override
    public boolean validate(String id) {
        if(memberRepository.findByUserId(id) == null){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean nickValidate(String nick) {
        if(memberRepository.findByUserId(nick) == null){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void save(ResisterDto resisterDto) {
        Member member = Member.builder()
                .userId(resisterDto.getUserId())
                .password(resisterDto.getPassword())
                .username(resisterDto.getUsername())
                .gender(resisterDto.getGender())
                .grade(MemberGrade.SEED)
                .role(ROLE.ROLE_USER)
                .build();

        memberRepository.save(member);
    }
}
