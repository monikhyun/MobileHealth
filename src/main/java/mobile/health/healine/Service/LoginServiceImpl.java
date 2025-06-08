package mobile.health.healine.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.health.healine.Entity.Member;
import mobile.health.healine.Repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService{

    private final MemberRepository memberRepository;
    @Override
    public boolean validate(String id, String password) {
        if(memberRepository.findByUserId(id) == null){
            return false;
        }
        else {
            if(memberRepository.findByUserId(id).getPassword().equals(password)){
                return true;
            }
            return false;
        }
    }

    @Override
    public Member findMemberByUserId(String userId) {
        return memberRepository.findByUserId(userId);
    }
}
