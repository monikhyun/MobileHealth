package mobile.health.healine.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final StringToBodyPartConverter conv;
    public WebConfig(StringToBodyPartConverter conv){ this.conv = conv; }
    @Override
    public void addFormatters(FormatterRegistry registry){
        registry.addConverter(conv);
    }
}
