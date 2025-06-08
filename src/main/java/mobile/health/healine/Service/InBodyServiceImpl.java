package mobile.health.healine.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.health.healine.Entity.InBody;
import mobile.health.healine.Entity.Member;
import mobile.health.healine.Entity.dto.InBodyResponseDto;
import mobile.health.healine.Repository.InBodyRepository;
import mobile.health.healine.Repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class InBodyServiceImpl implements InBodyService {
    private final InBodyRepository inBodyRepository;
    private final MemberRepository memberRepository;

    // 인바디 기록
    @Override
    public void recordInBody(String userId, InBodyResponseDto inBodyResponseDto) {
        Member member = memberRepository.findByUserId(userId);
        inBodyRepository.save(InBody.builder()
                .member(member)
                .weight(inBodyResponseDto.getWeight())
                .SMM(inBodyResponseDto.getSMM())
                .LBM(inBodyResponseDto.getLBM())
                .BMI(inBodyResponseDto.getBMI())
                .fat_percent(inBodyResponseDto.getFat_percent())
                .build());
    }

    // 인바디 기록목록 불러오기
    @Override
    public List<InBodyResponseDto> getInBodyByUserId(String userId) {
        Member member = memberRepository.findByUserId(userId);
        Optional<List<InBody>> list = inBodyRepository.findAllByMember(member);
        List<InBodyResponseDto> inBodyResponseDtos = new ArrayList<>();
        if (list.isPresent()) {
            for (InBody inBody : list.get()) {
                InBodyResponseDto inBodyResponseDto = new InBodyResponseDto();
                inBodyResponseDto.toDto(inBody);
                inBodyResponseDtos.add(inBodyResponseDto);
            }
            return inBodyResponseDtos;
        }
        return null;
    }

    // 인바디 정보 불러오기
    @Override
    public InBodyResponseDto getInBody(String userId, LocalDate localDate) {
        Member member = memberRepository.findByUserId(userId);
        Optional<InBody> inBody = inBodyRepository.findInBodyByMemberAndDate(member, localDate);
        if (inBody.isPresent()) {
            InBody entity = inBody.get();
            InBodyResponseDto inBodyResponseDto = new InBodyResponseDto();
            inBodyResponseDto.toDto(entity);
            return inBodyResponseDto;
        }
        return InBodyResponseDto.builder().build();
    }

    // 인바디 수정하기
    @Override
    public void editInBody(String userId, InBodyResponseDto inBodyResponseDto) {
        LocalDate localDate = inBodyResponseDto.getDate();
        Member member = memberRepository.findByUserId(userId);
        Optional<InBody> inBody = inBodyRepository.findInBodyByMemberAndDate(member, localDate);
        if (inBody.isPresent()) {
            InBody entity = inBody.get();
            entity.setWeight(inBodyResponseDto.getWeight());
            entity.setSMM(inBodyResponseDto.getSMM());
            entity.setLBM(inBodyResponseDto.getLBM());
            entity.setBMI(inBodyResponseDto.getBMI());
            entity.setFat_percent(inBodyResponseDto.getFat_percent());
        }
    }
}
