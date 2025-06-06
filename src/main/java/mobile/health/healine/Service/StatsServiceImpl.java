package mobile.health.healine.Service;

import lombok.RequiredArgsConstructor;
import mobile.health.healine.Entity.ExerciseRecord;
import mobile.health.healine.Entity.InBody;
import mobile.health.healine.Entity.Member;
import mobile.health.healine.Entity.dto.InBodyResponseDto;
import mobile.health.healine.Entity.dto.StatsResponseDto;
import mobile.health.healine.Repository.ExerciseRecordRepository;
import mobile.health.healine.Repository.InBodyRepository;
import mobile.health.healine.Repository.MemberRecordRepository;
import mobile.health.healine.Repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mobile.health.healine.Filter.WeekRangeUtil;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final MemberRepository memberRepository;
    private final InBodyRepository inBodyRepository;

    @Override
    public List<StatsResponseDto> getDailyStats(String userId) {
        List<StatsResponseDto> statsDtoList = new ArrayList<>();

        LocalDate[] week = WeekRangeUtil.getThisWeekRange();
        LocalDate sunday = week[0];
        LocalDate saturday = week[1];

        for(LocalDate d = sunday; !d.isAfter(saturday); d = d.plusDays(1)) {
            List<ExerciseRecord> exerciseData = exerciseRecordRepository.findByMemberAndDate(
                    memberRepository.findByUserId(userId), d
            );

            // 하루 볼륨 합산: weight × (count × setCount)
            BigDecimal dailyTotal = exerciseData.stream()
                    .map(record -> {
                        BigDecimal weight   = record.getWeight() != null
                                ? record.getWeight()
                                : BigDecimal.ZERO;
                        int count    = (record.getCount()    != null) ? record.getCount()    : 0;
                        int setCount = (record.getSetCount() != null) ? record.getSetCount() : 0;
                        BigDecimal reps = BigDecimal.valueOf((long) count * setCount);
                        return weight.multiply(reps);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // DTO 생성 (startDate = endDate = d, periodType = "DAILY")
            StatsResponseDto dto = StatsResponseDto.builder()
                    .startDate(d)
                    .endDate(d)
                    .total(dailyTotal)
                    .periodType("DAILY")
                    .build();

            statsDtoList.add(dto);
        }
        return statsDtoList;
    }

    @Override
    public List<StatsResponseDto> getWeeklyStats(String userId, LocalDate date) {
        List<StatsResponseDto> result = new ArrayList<>();
        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            return result;
        }

        // 해당 월의 모든 주차 범위(일요일~토요일) 구하기
        List<LocalDate[]> weekRanges = WeekRangeUtil.getWeekRangesOfMonth(date);

        //각 주차 범위마다 DB 조회, 볼륨 합산, DTO 생성
        for (LocalDate[] range : weekRanges) {
            LocalDate weekStart = range[0];   // 주차 시작일(일요일)
            LocalDate weekEnd = range[1];     // 주차 종료일(토요일)

            //해당 주차 레코드 조회
            List<ExerciseRecord> records =
                    exerciseRecordRepository.findByMemberAndDateBetween(member, weekStart, weekEnd);

            //각 레코드별 볼륨(volume = weight × (count × setCount)) 계산 후 합산
            BigDecimal totalVolume = records.stream()
                    .map(record -> {
                        // weight, count, setCount 중 null이 있을 수 있으니 방어적 처리
                        BigDecimal weight = record.getWeight() != null
                                ? record.getWeight()
                                : BigDecimal.ZERO;
                        int count = (record.getCount() != null)
                                ? record.getCount()
                                : 0;
                        int setCount = (record.getSetCount() != null)
                                ? record.getSetCount()
                                : 0;

                        // weight × (count × setCount) 계산
                        BigDecimal reps = BigDecimal.valueOf((long) count * setCount);
                        return weight.multiply(reps);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            StatsResponseDto dto = StatsResponseDto.builder()
                    .startDate(weekStart)
                    .endDate(weekEnd)
                    .total(totalVolume)
                    .periodType("WEEKLY")
                    .build();

            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatsResponseDto> getMonthlyStats(String userId, LocalDate date) {
        List<StatsResponseDto> result = new ArrayList<>();

        //Member 조회
        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            return result;
        }

        // 해당 월, 1달 전, 2달 전 범위 구하기
        List<LocalDate[]> monthRanges = WeekRangeUtil.getCurrentAndPreviousTwoMonthRanges(date);

        for (int i = 0; i < monthRanges.size(); i++) {
            LocalDate monthStart = monthRanges.get(i)[0];
            LocalDate monthEnd   = monthRanges.get(i)[1];

            if (i == 0) {
                monthEnd = date.isBefore(monthEnd) ? date : monthEnd;
            }

            // 해당 월 범위의 레코드 조회
            List<ExerciseRecord> records =
                    exerciseRecordRepository.findByMemberAndDateBetween(member, monthStart, monthEnd);

            // 볼륨 계산 (weight × count × setCount)
            BigDecimal totalVolume = records.stream()
                    .map(record -> {
                        BigDecimal weight = record.getWeight() != null
                                ? record.getWeight()
                                : BigDecimal.ZERO;
                        int count    = (record.getCount()    != null) ? record.getCount()    : 0;
                        int setCount = (record.getSetCount() != null) ? record.getSetCount() : 0;
                        BigDecimal reps = BigDecimal.valueOf((long) count * setCount);
                        return weight.multiply(reps);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // DTO 생성 (periodType = "MONTHLY")
            StatsResponseDto dto = StatsResponseDto.builder()
                    .startDate(monthStart)
                    .endDate(monthEnd)
                    .total(totalVolume)
                    .periodType("MONTHLY")
                    .build();

            result.add(dto);
        }

        return result;
    }

    @Override
    public InBodyResponseDto getInBodyResponse(String userId) {
        Optional<InBody> newInBody = inBodyRepository.findTopByMemberUserIdOrderByDateDesc(userId);
        if (newInBody.isPresent()) {
            InBody inBody = newInBody.get();

            return InBodyResponseDto.builder()
                    .weight(inBody.getWeight())
                    .date(inBody.getDate())
                    .SMM(inBody.getSMM())
                    .LBM(inBody.getLBM())
                    .BMI(inBody.getBMI())
                    .fat_percent(inBody.getFat_percent())
                    .build();
        }

        return null;
    }

    @Override
    public List<InBodyResponseDto> getWeight(String userId) {
        return fetchRecentFour(userId);
    }

    @Override
    public List<InBodyResponseDto> getSMM(String userId) {
        return fetchRecentFour(userId);
    }

    @Override
    public List<InBodyResponseDto> getLBM(String userId) {
        return fetchRecentFour(userId);
    }

    @Override
    public List<InBodyResponseDto> getBMI(String userId) {
        return fetchRecentFour(userId);
    }

    @Override
    public List<InBodyResponseDto> getFatPercent(String userId) {
        return fetchRecentFour(userId);
    }

    private List<InBodyResponseDto> fetchRecentFour(String userId) {
        return inBodyRepository
                .findTop4ByMemberUserIdOrderByDateDesc(userId)
                .stream()
                .map(inBody -> new InBodyResponseDto().toDto(inBody))
                .collect(Collectors.toList());
    }
}
