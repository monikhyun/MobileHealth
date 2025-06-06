package mobile.health.healine.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mobile.health.healine.Entity.BodyPart;
import mobile.health.healine.Entity.Exercise;
import mobile.health.healine.Entity.ExerciseRecord;
import mobile.health.healine.Entity.InBody;
import mobile.health.healine.Entity.Member;
import mobile.health.healine.Entity.ROLE;
import mobile.health.healine.Entity.Gender;
import mobile.health.healine.Repository.ExerciseRepository;
import mobile.health.healine.Repository.ExerciseRecordRepository;
import mobile.health.healine.Repository.MemberRepository;
import mobile.health.healine.Repository.InBodyRepository;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * InitDataService: 애플리케이션 시작 시,
 *   1) 운동 목록(Exercise) 초기화
 *   2) 테스트 유저(mjc) 초기화
 *   3) 2025년 3~6월에 걸친 ExerciseRecord(운동 기록) 초기화
 *   4) 2025년 4~6월에 걸친 InBody(체성분) 초기화
 *
 * • 운동 기록(seedMonth)
 *   – 3월/4월/5월/6월 (6월은 오늘까지) 동안,
 *     '월~토'(일요일 제외)에 대표 운동 수행
 *   – 월: 스쿼트(하체), 화: 이두 컬(팔), 수: 오버헤드 프레스(어깨),
 *     목: 랫 풀 다운(등), 금: 벤치 프레스(가슴), 토: 데드리프트(하체)
 *
 * • InBody 기록(seedInBodyForTestUser)
 *   – 4월 1일, 4월 15일, 5월 1일, 5월 15일, 6월 1일, 6월 15일(오늘 이전인 날짜만)
 *   – 체중은 점진적으로 감소, 골격근량은 점진적으로 증가, LBM은 비슷한 범위 유지
 *   – BMI/체지방률은 점진적으로 감소시키는 이상적인 흐름
 */
@Service
@RequiredArgsConstructor
public class InitDataService {

    private final ExerciseRepository     exerciseRepository;
    private final MemberRepository       memberRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final InBodyRepository       inBodyRepository;

    @Transactional
    public void initializeExercisesAndTestUser() {
        // 1) Exercise 목록 초기화
        if (exerciseRepository.count() == 0) {
            seedExercises();
        }

        // 2) 테스트용 Member(mjc) 초기화
        Member mjc = createTestUserIfNotExists();

        // 3) 테스트용 ExerciseRecord(운동 기록) 초기화
        if (exerciseRecordRepository.countByMember(mjc) == 0) {
            seedExerciseRecordsForTestUser(mjc);
        }

        // 4) 테스트용 InBody(체성분) 초기화
        //    - 이미 해당 유저에게 InBody 데이터가 있으면 건너뛴다.
        if (inBodyRepository.countByMember(mjc) == 0) {
            seedInBodyForTestUser(mjc);
        }
    }

    /**
     * 운동 목록을 한 번만 저장한다.
     */
    private void seedExercises() {
        List<Exercise> exercises = List.of(
                // BACK (등)
                Exercise.builder()
                        .category(BodyPart.BACK)
                        .exerciseName("풀 업")
                        .description(
                                "1. 어깨를 뒤로 내리며 견갑골을 먼저 조여라.\n" +
                                        "2. 가슴을 앞으로 내밀고 팔을 완전히 펴며 풀업 동작을 끝까지 수행하라."
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.BACK)
                        .exerciseName("랫 풀 다운")
                        .description(
                                "1. 가슴을 살짝 세우고 상체를 10~15도 뒤로 기울인 상태로 당겨라.\n" +
                                        "2. 바를 목 뒤가 아닌 가슴 상단 방향으로 끌어내려라."
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.BACK)
                        .exerciseName("벤트 오버 로우")
                        .description(
                                "1. 허리를 곧게 펴고 엉덩이를 뒤로 빼며 힌지 자세를 유지하라。\n" +
                                        "2. 팔꿈치를 몸통 쪽으로 당길 때 등 근육이 수축된 느낌을 집중하라."
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.BACK)
                        .exerciseName("원 암 로우")
                        .description(
                                "1. 벤치에 한쪽 무릎과 손을 대고, 반대쪽 팔로 바닥과 평행하게 당겨라。\n" +
                                        "2. 잡아당길 때 팔꿈치를 몸 뒤로 최대한 끌어당겨라."
                        ).build(),

                // LOWER_BODY (하체)
                Exercise.builder()
                        .category(BodyPart.LOWER_BODY)
                        .exerciseName("스쿼트")
                        .description(
                                "1. 무릎이 발끝을 넘지 않게 엉덩이를 뒤로 빼며 앉아라。\n" +
                                        "2. 허리를 곧게 펴고 내려갈 때 숨 들이쉬고, 올라오며 내쉬어라."
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.LOWER_BODY)
                        .exerciseName("런지")
                        .description(
                                "1. 앞발에 체중을 실어 무릎이 90도 각도로 내려가게 하라。\n" +
                                        "2. 뒤꿈치가 들리지 않도록 유도하고, 곧게 서서 시작 자세로 복귀하라."
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.LOWER_BODY)
                        .exerciseName("레그 프레스")
                        .description(
                                "1. 발판 중앙에 발을 두고 무릎이 완전히 펴지지 않도록 살짝 굽힌 상태를 유지하라。\n" +
                                        "2. 동작 내내 허리가 패드에 밀착되게 고정하라."
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.LOWER_BODY)
                        .exerciseName("레그 익스텐션")
                        .description(
                                "1. 무릎 관절에 과부하가 가지 않도록 부드럽게 올라올라가라。\n" +
                                        "2. 상단에서 1초간 정지 후 천천히 내리며 근육 긴장을 유지하라."
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.LOWER_BODY)
                        .exerciseName("레그 컬")
                        .description(
                                "1. 허벅지 뒤 근육이 수축됨을 느끼며 천천히 당겨라。\n" +
                                        "2. 모션 탑에서 잠시 멈추고, 저항을 느끼며 천천히 돌아가라."
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.LOWER_BODY)
                        .exerciseName("데드리프트")
                        .description(
                                "1. 바벨을 들어올릴 때 허리를 과도하게 펴지 말고 자연스러운 척추 중립을 유지하라。\n" +
                                        "2. 다리 힘을 먼저 사용하여 올라오며, 상체는 바벨을 몸에 붙인 채 유지하라."
                        ).build(),

                // SHOULDERS (어깨)
                Exercise.builder()
                        .category(BodyPart.SHOULDERS)
                        .exerciseName("오버헤드 프레스")
                        .description(
                                "1. 바벨을 귀 옆에서 머리 위로 곧게 밀어 올려라。\n" +
                                        "2. 하강 시 가슴 위로 내려와 어깨에 최대 자극이 가게 유지하라。"
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.SHOULDERS)
                        .exerciseName("사이드 레이즈")
                        .description(
                                "1. 팔꿈치를 약간 굽힌 채로 어깨 높이까지 들어올려라。\n" +
                                        "2. 상단에서 팔이 너무 높아지면 어깨 관절에 부담이 가니 90도 이하로 유지하라。"
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.SHOULDERS)
                        .exerciseName("프론트 레이즈")
                        .description(
                                "1. 덤벨을 손바닥이 아래로 향하게 들고 어깨 높이까지 올려라。\n" +
                                        "2. 동작 중 몸통이 흔들리지 않도록 복근에 힘을 주어 고정하라。"
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.SHOULDERS)
                        .exerciseName("리버스 플라이")
                        .description(
                                "1. 상체를 숙이고 견갑골을 모은 상태로 덤벨을 옆으로 들어라。\n" +
                                        "2. 팔이 가슴과 평행이 되도록 당기며 후면 삼각근을 집중 자극하라。"
                        ).build(),

                // CHEST (가슴)
                Exercise.builder()
                        .category(BodyPart.CHEST)
                        .exerciseName("벤치 프레스")
                        .description(
                                "1. 바벨을 가슴 중앙에 내린 뒤 손목이 과도하게 꺾이지 않게 하라。\n" +
                                        "2. 바닥을 밀듯 어깨뼈를 서로 모으며 안정된 자세를 유지하라。"
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.CHEST)
                        .exerciseName("인클라인 벤치 프레스")
                        .description(
                                "1. 벤치 각도를 30~45도로 설정해 상부 흉근을 자극하라。\n" +
                                        "2. 손목·팔꿈치가 일직선이 되도록 내려올 때 어깨에 부담이 가지 않게 하라。"
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.CHEST)
                        .exerciseName("체스트 플라이")
                        .description(
                                "1. 덤벨을 넓게 펼칠 때 팔꿈치를 살짝 굽힌 채로 유지하라。\n" +
                                        "2. 중앙에서 만날 때 가슴 근육이 수축되는 느낌을 집중하라。"
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.CHEST)
                        .exerciseName("케이블 크로스오버")
                        .description(
                                "1. 손잡이를 당길 때 가슴 중심선에서 만나게 한 뒤 잠시 정지하라。\n" +
                                        "2. 상단·하단 각각 높이를 바꿔 다양한 각도로 가슴을 자극하라。"
                        ).build(),

                // ARMS (팔)
                Exercise.builder()
                        .category(BodyPart.ARMS)
                        .exerciseName("이두 컬")
                        .description(
                                "1. 팔꿈치를 고정하고 이두근으로만 들어올리는 데 집중하라。\n" +
                                        "2. 상단에서 1초간 정지해 이두근 수축을 느껴라。"
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.ARMS)
                        .exerciseName("해머 컬")
                        .description(
                                "1. 손바닥이 서로 마주 보게 덤벨을 잡고, 팔꿈치를 옆구리에 고정하라。\n" +
                                        "2. 팔 전체를 들어올릴 때 팔목이 흔들리지 않게 주의하라。"
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.ARMS)
                        .exerciseName("삼두 푸쉬 다운")
                        .description(
                                "1. 팔꿈치를 몸 옆에 고정하고 손목이 꺾이지 않게 내려라。\n" +
                                        "2. 저항을 느끼며 손잡이를 아래로 완전히 밀어라。"
                        ).build(),
                Exercise.builder()
                        .category(BodyPart.ARMS)
                        .exerciseName("딥스")
                        .description(
                                "1. 어깨가 올라가지 않게 견갑골을 내린 상태로 수행하라。\n" +
                                        "2. 몸을 앞으로 기울이면 가슴 자극, 수직 자세는 삼두근 자극이 강화된다。"
                        ).build()
        );

        exerciseRepository.saveAll(exercises);
    }

    /**
     * 테스트용 회원(mjc)이 없다면 생성해서 리턴한다.
     */
    private Member createTestUserIfNotExists() {
        Member existing = memberRepository.findByUserId("mjc");
        if (existing != null) {
            return existing;
        }

        Member testUser = Member.builder()
                .userId("mjc")
                .username("헬린이")
                .password("1234")
                .role(ROLE.ROLE_USER)
                .gender(Gender.MALE)
                .height(BigDecimal.valueOf(170.5))
                .weight(BigDecimal.valueOf(65.3))
                .imageUrl("https://picsum.photos/200")
                .grade("브론즈")
                .build();

        return memberRepository.save(testUser);
    }

    /**
     * 2025년 3월/4월/5월/6월(6월은 오늘까지) 동안,
     * '월~토요일'에 매일 다른 부위 운동을 수행하며 중량을 조금씩 늘려가도록
     * ExerciseRecord를 생성하고 저장한다.
     *
     * • 월요일 → LOWER_BODY (“스쿼트”)
     * • 화요일 → ARMS       (“이두 컬”)
     * • 수요일 → SHOULDERS  (“오버헤드 프레스”)
     * • 목요일 → BACK      (“랫 풀 다운”)
     * • 금요일 → CHEST     (“벤치 프레스”)
     * • 토요일 → LOWER_BODY (“데드리프트”)
     * • 일요일 → 휴식 (기록 생성 안 함)
     */
    private void seedExerciseRecordsForTestUser(Member mjc) {
        List<ExerciseRecord> records = new ArrayList<>();

        // 1) 2025년 3월(1~30일)
        seedMonth(mjc, 2025, 3, 1, 30, records);

        // 2) 2025년 4월(1~30일)
        seedMonth(mjc, 2025, 4, 1, 30, records);

        // 3) 2025년 5월(1~30일)
        seedMonth(mjc, 2025, 5, 1, 30, records);

        // 4) 2025년 6월(1~오늘까지)
        LocalDate today = LocalDate.now();
        int endDayOfJune = (today.getYear() == 2025 && today.getMonthValue() == 6)
                ? today.getDayOfMonth()
                : 30;
        seedMonth(mjc, 2025, 6, 1, endDayOfJune, records);

        exerciseRecordRepository.saveAll(records);
    }

    /**
     * 특정 연도(year)와 월(month)의 날짜(fromDay~toDay) 사이에,
     * 매일(일요일 제외) “요일별 대표 운동”을 수행하도록
     * buildRecord(...)를 이용해 ExerciseRecord를 생성하여 리스트에 추가한다.
     *
     * @param mjc         테스트 유저(Member)
     * @param year        연도 (예: 2025)
     * @param month       월 (1~12)
     * @param fromDay     시작일 (1~31)
     * @param toDay       종료일 (1~31)
     * @param collector   최종 저장할 List<ExerciseRecord>
     */
    private void seedMonth(Member mjc,
                           int year,
                           int month,
                           int fromDay,
                           int toDay,
                           List<ExerciseRecord> collector) {

        for (int day = fromDay; day <= toDay; day++) {
            LocalDate date;
            try {
                date = LocalDate.of(year, month, day);
            } catch (Exception e) {
                // 유효하지 않은 날짜(예: 2025-02-30) 건너뜀
                continue;
            }

            DayOfWeek dow = date.getDayOfWeek();
            if (dow == DayOfWeek.SUNDAY) {
                // 일요일은 휴식
                continue;
            }

            int baseWeightMultiplier;
            switch (month) {
                case 3: baseWeightMultiplier = 1; break;
                case 4: baseWeightMultiplier = 2; break;
                case 5: baseWeightMultiplier = 3; break;
                case 6: baseWeightMultiplier = 4; break;
                default: baseWeightMultiplier = 1; break;
            }

            // 월요일 → 스쿼트
            if (dow == DayOfWeek.MONDAY) {
                String exerciseName = "스쿼트";
                BodyPart part = BodyPart.LOWER_BODY;
                BigDecimal w1 = BigDecimal.valueOf(50 + baseWeightMultiplier * 5 + day * 0.2);
                BigDecimal w2 = w1.add(BigDecimal.valueOf(10));
                BigDecimal w3 = w1.add(BigDecimal.valueOf(15));
                collector.add(buildRecord(mjc, exerciseName, part, date, 1, 10, w1));
                collector.add(buildRecord(mjc, exerciseName, part, date, 2,  8, w2));
                collector.add(buildRecord(mjc, exerciseName, part, date, 3,  6, w3));
            }
            // 화요일 → 이두 컬
            else if (dow == DayOfWeek.TUESDAY) {
                String exerciseName = "이두 컬";
                BodyPart part = BodyPart.ARMS;
                BigDecimal w1 = BigDecimal.valueOf(8 + baseWeightMultiplier * 2 + day * 0.05);
                BigDecimal w2 = w1.add(BigDecimal.valueOf(2));
                BigDecimal w3 = w1.add(BigDecimal.valueOf(4));
                collector.add(buildRecord(mjc, exerciseName, part, date, 1, 12, w1));
                collector.add(buildRecord(mjc, exerciseName, part, date, 2, 10, w2));
                collector.add(buildRecord(mjc, exerciseName, part, date, 3,  8, w3));
            }
            // 수요일 → 오버헤드 프레스
            else if (dow == DayOfWeek.WEDNESDAY) {
                String exerciseName = "오버헤드 프레스";
                BodyPart part = BodyPart.SHOULDERS;
                BigDecimal w1 = BigDecimal.valueOf(12 + baseWeightMultiplier * 2 + day * 0.05);
                BigDecimal w2 = w1.add(BigDecimal.valueOf(5));
                BigDecimal w3 = w1.add(BigDecimal.valueOf(8));
                collector.add(buildRecord(mjc, exerciseName, part, date, 1, 10, w1));
                collector.add(buildRecord(mjc, exerciseName, part, date, 2,  8, w2));
                collector.add(buildRecord(mjc, exerciseName, part, date, 3,  6, w3));
            }
            // 목요일 → 랫 풀 다운
            else if (dow == DayOfWeek.THURSDAY) {
                String exerciseName = "랫 풀 다운";
                BodyPart part = BodyPart.BACK;
                BigDecimal w1 = BigDecimal.valueOf(25 + baseWeightMultiplier * 3 + day * 0.1);
                BigDecimal w2 = w1.add(BigDecimal.valueOf(5));
                BigDecimal w3 = w1.add(BigDecimal.valueOf(10));
                collector.add(buildRecord(mjc, exerciseName, part, date, 1, 12, w1));
                collector.add(buildRecord(mjc, exerciseName, part, date, 2, 10, w2));
                collector.add(buildRecord(mjc, exerciseName, part, date, 3,  8, w3));
            }
            // 금요일 → 벤치 프레스
            else if (dow == DayOfWeek.FRIDAY) {
                String exerciseName = "벤치 프레스";
                BodyPart part = BodyPart.CHEST;
                BigDecimal w1 = BigDecimal.valueOf(30 + baseWeightMultiplier * 3 + day * 0.1);
                BigDecimal w2 = w1.add(BigDecimal.valueOf(5));
                BigDecimal w3 = w1.add(BigDecimal.valueOf(10));
                collector.add(buildRecord(mjc, exerciseName, part, date, 1, 10, w1));
                collector.add(buildRecord(mjc, exerciseName, part, date, 2,  8, w2));
                collector.add(buildRecord(mjc, exerciseName, part, date, 3,  6, w3));
            }
            // 토요일 → 데드리프트
            else if (dow == DayOfWeek.SATURDAY) {
                String exerciseName = "데드리프트";
                BodyPart part = BodyPart.LOWER_BODY;
                BigDecimal w1 = BigDecimal.valueOf(60 + baseWeightMultiplier * 5 + day * 0.2);
                BigDecimal w2 = w1.add(BigDecimal.valueOf(10));
                BigDecimal w3 = w1.add(BigDecimal.valueOf(20));
                collector.add(buildRecord(mjc, exerciseName, part, date, 1, 10, w1));
                collector.add(buildRecord(mjc, exerciseName, part, date, 2,  8, w2));
                collector.add(buildRecord(mjc, exerciseName, part, date, 3,  6, w3));
            }
        }
    }

    private ExerciseRecord buildRecord(Member member,
                                       String exerciseName,
                                       BodyPart part,
                                       LocalDate date,
                                       Integer setCount,
                                       Integer count,
                                       BigDecimal weight) {
        return ExerciseRecord.builder()
                .member(member)
                .exerciseName(exerciseName)
                .bodyPart(part)
                .date(date)
                .setCount(setCount)
                .count(count)
                .weight(weight)
                .done(true)
                .build();
    }

    /**
     * 2025년 4월 1일~6월 15일(오늘 이전 날짜만)까지 2주 간격으로 InBody 데이터를 생성한다.
     */
    private void seedInBodyForTestUser(Member mjc) {
        List<InBody> inBodies = new ArrayList<>();

        // 데이터 포인트 날짜 목록 (4/1, 4/15, 5/1, 5/15, 6/1, 6/15)
        List<LocalDate> candidateDates = List.of(
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 15),
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 15),
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 6, 7)
        );

        LocalDate today = LocalDate.now();

        for (LocalDate date : candidateDates) {
            if (date.isAfter(today)) {
                // 오늘 이후의 날짜는 건너뜀
                continue;
            }

            // 날짜별로 “체중 감소, 골격근량 증가, LBM 유지/소폭 변화, BMI 및 체지방률 감소” 패턴
            // → 실제 수치는 예시일 뿐이며, 필요에 따라 조정할 수 있다.
            InBody inBody;
            if (date.equals(LocalDate.of(2025, 4, 1))) {
                inBody = InBody.builder()
                        .member(mjc)
                        .date(date)
                        .weight(BigDecimal.valueOf(75.0))       // 4/1 체중
                        .SMM(BigDecimal.valueOf(30.0))          // 4/1 골격근량
                        .LBM(BigDecimal.valueOf(60.0))          // 4/1 제지방량
                        .BMI(BigDecimal.valueOf(25.9))          // 4/1 BMI
                        .fat_percent(BigDecimal.valueOf(20.0))  // 4/1 체지방률
                        .build();
            }
            else if (date.equals(LocalDate.of(2025, 4, 15))) {
                inBody = InBody.builder()
                        .member(mjc)
                        .date(date)
                        .weight(BigDecimal.valueOf(74.0))
                        .SMM(BigDecimal.valueOf(30.5))
                        .LBM(BigDecimal.valueOf(60.2))
                        .BMI(BigDecimal.valueOf(25.5))
                        .fat_percent(BigDecimal.valueOf(19.5))
                        .build();
            }
            else if (date.equals(LocalDate.of(2025, 5, 1))) {
                inBody = InBody.builder()
                        .member(mjc)
                        .date(date)
                        .weight(BigDecimal.valueOf(73.0))
                        .SMM(BigDecimal.valueOf(31.0))
                        .LBM(BigDecimal.valueOf(60.1))
                        .BMI(BigDecimal.valueOf(25.2))
                        .fat_percent(BigDecimal.valueOf(19.0))
                        .build();
            }
            else if (date.equals(LocalDate.of(2025, 5, 15))) {
                inBody = InBody.builder()
                        .member(mjc)
                        .date(date)
                        .weight(BigDecimal.valueOf(72.0))
                        .SMM(BigDecimal.valueOf(31.5))
                        .LBM(BigDecimal.valueOf(60.0))
                        .BMI(BigDecimal.valueOf(24.9))
                        .fat_percent(BigDecimal.valueOf(18.5))
                        .build();
            }
            else if (date.equals(LocalDate.of(2025, 6, 1))) {
                inBody = InBody.builder()
                        .member(mjc)
                        .date(date)
                        .weight(BigDecimal.valueOf(71.0))
                        .SMM(BigDecimal.valueOf(32.0))
                        .LBM(BigDecimal.valueOf(60.3))
                        .BMI(BigDecimal.valueOf(24.5))
                        .fat_percent(BigDecimal.valueOf(18.0))
                        .build();
            }
            else {
                inBody = InBody.builder()
                        .member(mjc)
                        .date(date)
                        .weight(BigDecimal.valueOf(70.0))
                        .SMM(BigDecimal.valueOf(32.5))
                        .LBM(BigDecimal.valueOf(60.2))
                        .BMI(BigDecimal.valueOf(24.2))
                        .fat_percent(BigDecimal.valueOf(17.5))
                        .build();
            }

            inBodies.add(inBody);
        }

        inBodyRepository.saveAll(inBodies);
    }
}