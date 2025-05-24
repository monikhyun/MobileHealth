package mobile.health.healine.Config;

// 2) Spring MVC Converter 구현
import mobile.health.healine.Entity.BodyPart;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToBodyPartConverter implements Converter<String, BodyPart> {
    @Override
    public BodyPart convert(String source) {
        try {
            return BodyPart.fromDisplayName(source);            // "가슴" → CHEST
        } catch (IllegalArgumentException e) {
            return BodyPart.valueOf(source.toUpperCase());      // fallback: "CHEST"
        }
    }
}
