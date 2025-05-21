package mobile.health.healine.Config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HealthApp API")
                        .version("v1.0")
                        .description("헬린이 APP API 문서"))
                .externalDocs(new ExternalDocumentation()
                        .description("프로젝트 GitHub")
                        .url("https://https://github.com/monikhyun/MobileHealth"));
    }
}
