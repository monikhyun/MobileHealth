package mobile.health.healine.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import mobile.health.healine.Service.InitDataService;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final InitDataService initService;

    @Bean
    public ApplicationRunner dataInitializerRunner() {
        return args -> {
            // 애플리케이션 시작 시 InitService를 통해 초기 데이터를 한 번만 로드
            initService.initializeExercisesAndTestUser();
        };
    }
}