package mobile.health.healine.Config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import mobile.health.healine.Entity.BodyPart;
import mobile.health.healine.Entity.Exercise;
import mobile.health.healine.Repository.ExerciseRepository;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    public ApplicationRunner exerciseInitializer(ExerciseRepository repo) {
        return args -> {
            if (repo.count() > 0) return;

            List<Exercise> exercises = List.of(
                    // BACK (등)
                    Exercise.builder()
                            .category(BodyPart.BACK)
                            .exerciseName("Pull-Up")
                            .description(
                                    "1. 어깨를 귀쪽이 아닌 뒤로 내리며 견갑골을 먼저 조여라.\n" +
                                            "2. 가슴을 앞으로 내밀고 팔을 완전히 펴며 풀업 동작을 끝까지 수행하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.BACK)
                            .exerciseName("Lat Pulldown")
                            .description(
                                    "1. 가슴을 살짝 세우고 상체를 10~15도 뒤로 기울인 상태로 당겨라.\n" +
                                            "2. 바를 목 뒤가 아닌 가슴 상단 방향으로 끌어내려라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.BACK)
                            .exerciseName("Bent-Over Row")
                            .description(
                                    "1. 허리를 곧게 펴고 엉덩이를 뒤로 빼며 힌지 자세를 유지하라.\n" +
                                            "2. 팔꿈치를 몸통 쪽으로 당길 때 등 근육이 수축된 느낌을 집중하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.BACK)
                            .exerciseName("Single-Arm Row")
                            .description(
                                    "1. 벤치에 한쪽 무릎과 손을 대고, 반대쪽 팔로 바닥과 평행하게 당겨라.\n" +
                                            "2. 잡아당길 때 팔꿈치를 몸 뒤로 최대한 끌어당겨라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.BACK)
                            .exerciseName("Seated Cable Row")
                            .description(
                                    "1. 엉덩이를 고정하고 가슴을 곧게 세운 상태로 당겨라.\n" +
                                            "2. 손목을 고정하고 팔꿈치가 등 뒤로 지나가게 집중하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.BACK)
                            .exerciseName("T-Bar Row")
                            .description(
                                    "1. 상체를 앞으로 숙일 때 허리가 굽지 않도록 주의하라.\n" +
                                            "2. 당길 때 어깨를 뒤로 모으듯 견갑골을 조여라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.BACK)
                            .exerciseName("Deadlift")
                            .description(
                                    "1. 바벨을 들어올릴 때 허리를 과도하게 펴지 말고 자연스러운 척추 중립을 유지하라.\n" +
                                            "2. 다리 힘을 먼저 사용하여 올라오며, 상체는 바벨을 몸에 붙인 채 유지하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.BACK)
                            .exerciseName("Straight-Arm Pulldown")
                            .description(
                                    "1. 팔을 곧게 유지하고 등 근육으로만 당긴다는 느낌으로 수행하라.\n" +
                                            "2. 손목이 꺾이지 않도록 고정하고, 끝까지 당겨 하단에서 잠시 정지하라."
                            ).build(),

                    // LOWER_BODY (하체)
                    Exercise.builder()
                            .category(BodyPart.LOWER_BODY)
                            .exerciseName("Squat")
                            .description(
                                    "1. 무릎이 발끝을 넘지 않게 엉덩이를 뒤로 빼며 앉아라.\n" +
                                            "2. 허리를 곧게 펴고 내려갈 때 숨 들이쉬고, 올라오며 내쉬어라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.LOWER_BODY)
                            .exerciseName("Lunge")
                            .description(
                                    "1. 앞발에 체중을 실어 무릎이 90도 각도로 내려가게 하라.\n" +
                                            "2. 뒤꿈치가 들리지 않도록 유도하고, 곧게 서서 시작 자세로 복귀하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.LOWER_BODY)
                            .exerciseName("Leg Press")
                            .description(
                                    "1. 발판 중앙에 발을 두고 무릎이 완전히 펴지지 않도록 살짝 굽힌 상태를 유지하라.\n" +
                                            "2. 동작 내내 허리가 패드에 밀착되게 고정하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.LOWER_BODY)
                            .exerciseName("Leg Extension")
                            .description(
                                    "1. 무릎 관절에 과부하가 가지 않도록 부드럽게 올라올라가라.\n" +
                                            "2. 상단에서 1초간 정지 후 천천히 내리며 근육 긴장을 유지하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.LOWER_BODY)
                            .exerciseName("Leg Curl")
                            .description(
                                    "1. 허벅지 뒤 근육이 수축됨을 느끼며 천천히 당겨라.\n" +
                                            "2. 모션 탑에서 잠시 멈추고, 저항을 느끼며 천천히 돌아가라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.LOWER_BODY)
                            .exerciseName("Calf Raise")
                            .description(
                                    "1. 발끝 위로 올라설 때 발뒤꿈치를 최대한 들어 올려라.\n" +
                                            "2. 바닥에 닿을 때 완전히 내리지 말고 약간 떠있는 상태를 유지하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.LOWER_BODY)
                            .exerciseName("Hip Thrust")
                            .description(
                                    "1. 견고한 벤치를 허리에 대고 엉덩이를 높이 들어올려라.\n" +
                                            "2. 최상단에서 엉덩이에 힘을 준 상태로 1초간 정지하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.LOWER_BODY)
                            .exerciseName("Glute Bridge")
                            .description(
                                    "1. 바닥에 누워 무릎을 세우고 엉덩이를 밀어 올려라.\n" +
                                            "2. 최상단에서 둔근 수축을 느끼며 1초간 멈췄다 내리라."
                            ).build(),

                    // SHOULDERS (어깨)
                    Exercise.builder()
                            .category(BodyPart.SHOULDERS)
                            .exerciseName("Overhead Press")
                            .description(
                                    "1. 바벨을 귀 옆에서 머리 위로 곧게 밀어 올려라.\n" +
                                            "2. 하강 시 가슴 위로 내려와 어깨에 최대 자극이 가게 유지하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.SHOULDERS)
                            .exerciseName("Lateral Raise")
                            .description(
                                    "1. 팔꿈치를 약간 굽힌 채로 어깨 높이까지 들어올려라.\n" +
                                            "2. 상단에서 팔이 너무 높아지면 어깨 관절에 부담이 가니 90도 이하로 유지하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.SHOULDERS)
                            .exerciseName("Front Raise")
                            .description(
                                    "1. 덤벨을 손바닥이 아래로 향하게 들고 어깨 높이까지 올려라.\n" +
                                            "2. 동작 중 몸통이 흔들리지 않도록 복근에 힘을 주어 고정하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.SHOULDERS)
                            .exerciseName("Rear Delt Fly")
                            .description(
                                    "1. 상체를 숙이고 견갑골을 모은 상태로 덤벨을 옆으로 들어라.\n" +
                                            "2. 팔이 가슴과 평행이 되도록 당기며 후면 삼각근을 집중 자극하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.SHOULDERS)
                            .exerciseName("Upright Row")
                            .description(
                                    "1. 바벨이나 케이블을 턱 높이까지 끌어올릴 때 손목이 과도하게 꺾이지 않게 하라.\n" +
                                            "2. 팔꿈치를 옆으로 최대한 올려 어깨 윗부분에 자극을 느껴라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.SHOULDERS)
                            .exerciseName("Arnold Press")
                            .description(
                                    "1. 시작 시 덤벨을 몸 쪽으로 회전시켜 손바닥이 얼굴을 향하게 하라.\n" +
                                            "2. 밀어 올리며 손바닥을 앞을 향하게 회전시키면서 어깨 전체를 자극하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.SHOULDERS)
                            .exerciseName("Shrug")
                            .description(
                                    "1. 덤벨을 잡고 어깨를 최대한 귀 방향으로 들어 올려라.\n" +
                                            "2. 상단에서 1초간 정지 후 천천히 내려 목과 어깨 윗부분을 늘려라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.SHOULDERS)
                            .exerciseName("Cable Face Pull")
                            .description(
                                    "1. 로프 핸들을 얼굴 높이로 당기며 견갑골을 조여라.\n" +
                                            "2. 팔꿈치를 몸 옆보다 높게 유지해 후면 삼각근을 집중적으로 자극하라."
                            ).build(),

                    // CHEST (가슴)
                    Exercise.builder()
                            .category(BodyPart.CHEST)
                            .exerciseName("Bench Press")
                            .description(
                                    "1. 바벨을 가슴 중앙에 내린 뒤 손목이 과도하게 꺾이지 않게 하라.\n" +
                                            "2. 바닥을 밀듯 어깨뼈를 서로 모으며 안정된 자세를 유지하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.CHEST)
                            .exerciseName("Incline Bench Press")
                            .description(
                                    "1. 벤치 각도를 30~45도로 설정해 상부 흉근을 자극하라.\n" +
                                            "2. 손목·팔꿈치가 일직선이 되도록 내려올 때 어깨에 부담이 가지 않게 하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.CHEST)
                            .exerciseName("Chest Fly")
                            .description(
                                    "1. 덤벨을 넓게 펼칠 때 팔꿈치를 살짝 굽힌 채로 유지하라.\n" +
                                            "2. 중앙에서 만날 때 가슴 근육이 수축되는 느낌을 집중하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.CHEST)
                            .exerciseName("Push-Up")
                            .description(
                                    "1. 몸 전체를 일직선으로 유지하고, 가슴이 바닥에 닿기 직전까지 내리라.\n" +
                                            "2. 팔꿈치를 너무 벌리지 말고 45도 정도로 유지해 어깨 부상을 방지하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.CHEST)
                            .exerciseName("Cable Crossover")
                            .description(
                                    "1. 손잡이를 당길 때 가슴 중심선에서 만나게 한 뒤 잠시 정지하라.\n" +
                                            "2. 상단·하단 각각 높이를 바꿔 다양한 각도로 가슴을 자극하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.CHEST)
                            .exerciseName("Dumbbell Pullover")
                            .description(
                                    "1. 벤치에 가로로 누워 머리 뒤로 덤벨을 내릴 때 호흡을 들이쉬라.\n" +
                                            "2. 올릴 때 가슴을 모으듯 팔을 천천히 들어올려라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.CHEST)
                            .exerciseName("Smith Machine Press")
                            .description(
                                    "1. 스미스 머신 바가 수직으로만 움직이므로 가슴 중간 방향으로만 들었다 내리라.\n" +
                                            "2. 견갑골을 모아 어깨의 부담을 줄이고 가슴 수축감을 높이라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.CHEST)
                            .exerciseName("Decline Bench Press")
                            .description(
                                    "1. 벤치 각도를 15~30도로 설정해 하부 흉근을 집중 자극하라.\n" +
                                            "2. 바벨을 내릴 때 가슴 아래쪽에 닿도록 경로를 유지하라."
                            ).build(),

                    // ARMS (팔)
                    Exercise.builder()
                            .category(BodyPart.ARMS)
                            .exerciseName("Biceps Curl")
                            .description(
                                    "1. 팔꿈치를 고정하고 이두근으로만 들어올리는 데 집중하라.\n" +
                                            "2. 상단에서 1초간 정지해 이두근 수축을 느껴라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.ARMS)
                            .exerciseName("Hammer Curl")
                            .description(
                                    "1. 손바닥이 서로 마주 보게 덤벨을 잡고, 팔꿈치를 옆구리에 고정하라.\n" +
                                            "2. 팔 전체를 들어올릴 때 팔목이 흔들리지 않게 주의하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.ARMS)
                            .exerciseName("Triceps Pushdown")
                            .description(
                                    "1. 팔꿈치를 몸 옆에 고정하고 손목이 꺾이지 않게 내려라.\n" +
                                            "2. 저항을 느끼며 손잡이를 아래로 완전히 밀어라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.ARMS)
                            .exerciseName("Overhead Triceps Extension")
                            .description(
                                    "1. 양손으로 덤벨을 잡아 머리 뒤로 내릴 때 어깨가 올라가지 않게 고정하라.\n" +
                                            "2. 팔꿈치를 고정하고 이두근이 아닌 삼두근으로만 움직이도록 하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.ARMS)
                            .exerciseName("Concentration Curl")
                            .description(
                                    "1. 벤치에 앉아 팔꿈치를 무릎 안쪽에 고정하고 이두근을 집중 자극하라.\n" +
                                            "2. 천천히 들어올리고 천천히 내리며 근육 긴장을 유지하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.ARMS)
                            .exerciseName("Skullcrusher")
                            .description(
                                    "1. 벤치에 누워 바벨을 이마 위로 내릴 때 팔꿈치를 최대한 고정하라.\n" +
                                            "2. 팔을 펴며 삼두근이 완전히 수축되도록 끝까지 밀어라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.ARMS)
                            .exerciseName("Preacher Curl")
                            .description(
                                    "1. 프리처 벤치에 팔을 고정하고 상단에서 이두근 수축을 느껴라.\n" +
                                            "2. 천천히 내리며 근육이 완전히 이완되도록 컨트롤하라."
                            ).build(),
                    Exercise.builder()
                            .category(BodyPart.ARMS)
                            .exerciseName("Dips")
                            .description(
                                    "1. 어깨가 올라가지 않게 견갑골을 내린 상태로 수행하라.\n" +
                                            "2. 몸을 앞으로 기울이면 가슴 자극, 수직 자세는 삼두근 자극이 강화된다."
                            ).build()
            );

            repo.saveAll(exercises);
        };
    }
}