package roomescape.global.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import roomescape.member.domain.MemberRole;

@Converter(autoApply = true)
public class JpaMemberRoleConverter implements AttributeConverter<MemberRole, String> {

    @Override
    public String convertToDatabaseColumn(final MemberRole role) {
        if (role == null) {
            return null;
        }
        return role.getPrimaryType();
    }

    @Override
    public MemberRole convertToEntityAttribute(final String type) {
        return MemberRole.from(type);
    }
}
