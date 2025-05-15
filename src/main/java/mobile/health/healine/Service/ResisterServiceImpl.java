package mobile.health.healine.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.health.healine.Entity.Member;
import mobile.health.healine.Entity.dto.ResisterDto;
import mobile.health.healine.Repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ResisterServiceImpl implements ResisterService{

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
    public void save(ResisterDto resisterDto) {
        Member member = Member.builder()
                .userId(resisterDto.getUserId())
                .password(resisterDto.getUserPassword())
                .email(resisterDto.getUserEmail())
                .gender(resisterDto.getUserGender())
                .major(resisterDto.getUserMajor())
                .build();

        memberRepository.save(member);
    }
}
