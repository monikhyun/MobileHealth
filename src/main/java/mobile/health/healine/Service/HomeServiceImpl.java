package mobile.health.healine.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.health.healine.Entity.DailyExerciseLog;
import mobile.health.healine.Entity.Member;
import mobile.health.healine.Entity.MemberGrade;
import mobile.health.healine.Entity.dto.DailyLogDto;
import mobile.health.healine.Entity.dto.GradeDto;
import mobile.health.healine.Entity.dto.ProfileDto;
import mobile.health.healine.Entity.dto.ProfileResponseDto;
import mobile.health.healine.Repository.DailyExerciseLogRepository;
import mobile.health.healine.Repository.ExerciseRecordRepository;
import mobile.health.healine.Repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final DailyExerciseLogRepository dailyExerciseLogRepository;
    private final MemberRepository memberRepository;
    private final CloudinaryService cloudinaryService;
    private final ExerciseRecordRepository exerciseRecordRepository;

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

    // 회원정보 수정
    @Override
    public void updateProfile(String userId, ProfileDto profileDto) {
        Member member = memberRepository.findByUserId(userId);
        member.setHeight(profileDto.getHeight());
        member.setWeight(profileDto.getWeight());
        member.setAge(profileDto.getAge());
        member.setGender(profileDto.getGender());
        MultipartFile imageFile = profileDto.getImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(imageFile);
            member.setImageUrl(imageUrl);
        }
        memberRepository.save(member);
    }

    // 등급 정보 가져오기
    @Override
    public GradeDto getGrade(String userId) {
        Member member = memberRepository.findByUserId(userId);
        long days = exerciseRecordRepository.countDistinctByDate(member); // 누적 운동일 수

        MemberGrade currentGrade = MemberGrade.fromDays(days);
        MemberGrade nextGrade = currentGrade.getNext();

        Long count;
        if (nextGrade != null) {
            long required = nextGrade.getRequiredDaysToReach();
            count = required - days;
            if (count < 0) count = 0L;
        } else {
            count = 0L; // 마지막 등급이므로 추가 필요 없음
        }

        // 현재 등급 갱신
        if (member.getGrade() != currentGrade) {
            member.setGrade(currentGrade);
        }

        return GradeDto.builder()
                .memberGrade(currentGrade)
                .nextGrade(nextGrade)
                .count(count)
                .build();
    }

    @Override
    public ProfileResponseDto getProfile(String userId) {
        Member member = memberRepository.findByUserId(userId);
        return ProfileResponseDto.builder()
                .name(member.getUsername())
                .age(member.getAge())
                .gender(member.getGender())
                .height(member.getHeight())
                .weight(member.getWeight())
                .image(member.getImageUrl())
                .grade(member.getGrade())
                .build();
    }
}
