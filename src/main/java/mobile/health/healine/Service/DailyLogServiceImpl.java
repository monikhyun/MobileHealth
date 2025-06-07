package mobile.health.healine.Service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
@RequiredArgsConstructor
@Transactional
public class DailyLogServiceImpl implements DailyLogService {

    private final MemberRepository memberRepository;
    private final DailyExerciseLogRepository dailyExerciseLogRepository;

    @Override
    public void save(String userId, LocalDate date, Integer time) {
        Member member = memberRepository.findByUserId(userId);

        Optional<DailyExerciseLog> optionalLog = dailyExerciseLogRepository.findByMemberAndDate(member, date);

        DailyExerciseLog log;
        if (optionalLog.isPresent()) {
            log = optionalLog.get();
            log.setTotalExerciseMinutes(time);
        } else {
            log = DailyExerciseLog.builder()
                    .member(member)
                    .date(date)
                    .totalExerciseMinutes(time)
                    .build();
        }

        dailyExerciseLogRepository.save(log);
    }

    @Override
    public DailyLogDto getDailyLog(String userId, LocalDate date) {
        Member member = memberRepository.findByUserId(userId);
        Optional<DailyExerciseLog> optionalLog = dailyExerciseLogRepository.findByMemberAndDate(member, date);
        if (optionalLog.isPresent()) {
            DailyExerciseLog log = optionalLog.get();
            return DailyLogDto.builder()
                    .date(log.getDate())
                    .totalExerciseMinutes(log.getTotalExerciseMinutes())
                    .build();
        }
        return DailyLogDto.builder()
                .date(date)
                .totalExerciseMinutes(0)
                .build();
    }
}
