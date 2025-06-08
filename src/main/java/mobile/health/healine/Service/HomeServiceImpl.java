package mobile.health.healine.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.health.healine.Entity.DailyExerciseLog;
import mobile.health.healine.Entity.Member;
import mobile.health.healine.Entity.dto.DailyLogDto;
import mobile.health.healine.Repository.DailyExerciseLogRepository;
import mobile.health.healine.Repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final DailyExerciseLogRepository dailyExerciseLogRepository;
    private final MemberRepository memberRepository;

    @Override
    public DailyLogDto getDailyLog(String userId) {
        Member member = memberRepository.findByUserId(userId);
        Optional<DailyExerciseLog> log = dailyExerciseLogRepository.findByMemberAndDate(member, LocalDate.now());
        if (log.isPresent()) {
            DailyExerciseLog dailyExerciseLog = log.get();
            return DailyLogDto.builder()
                    .date(dailyExerciseLog.getDate())
                    .totalExerciseMinutes(dailyExerciseLog.getTotalExerciseMinutes())
                    .build();
        }
        return DailyLogDto.builder()
                .date(LocalDate.now())
                .totalExerciseMinutes(0)
                .build();
    }
}
