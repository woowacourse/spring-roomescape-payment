package roomescape.member.auth.vo;

import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;

@FieldNameConstants(level = AccessLevel.PRIVATE)
public record MemberInfo(
        Long id,
        String name,
        String email,
        Role role
) {

    public MemberInfo {
        validate(id, name, email, role);
    }

    public static MemberInfo from(final Member member) {
        return new MemberInfo(
                member.getId(),
                member.getName().getValue(),
                member.getEmail().getValue(),
                member.getRole()
        );
    }

    private void validate(final Long id,
                          final String name,
                          final String email,
                          final Role role) {
        Validator.of(MemberInfo.class)
                .notNullField(Fields.id, id)
                .notNullField(Fields.name, name)
                .notNullField(Fields.email, email)
                .notNullField(Fields.role, role);
    }
}
