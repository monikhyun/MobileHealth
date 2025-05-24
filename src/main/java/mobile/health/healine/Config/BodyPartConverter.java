package mobile.health.healine.Config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import mobile.health.healine.Entity.BodyPart;

@Converter(autoApply = true)
public class BodyPartConverter implements AttributeConverter<BodyPart, String> {

    @Override
    public String convertToDatabaseColumn(BodyPart attribute) {
        // DB에 저장할 때 displayName 사용
        return attribute != null ? attribute.getDisplayName() : null;
    }

    @Override
    public BodyPart convertToEntityAttribute(String dbData) {
        // DB에서 꺼낼 때 displayName으로 enum 찾기
        if (dbData == null) return null;
        for (BodyPart bp : BodyPart.values()) {
            if (bp.getDisplayName().equals(dbData)) {
                return bp;
            }
        }
        throw new IllegalArgumentException("Unknown BodyPart displayName: " + dbData);
    }
}