package roomescape.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import roomescape.member.exception.InvalidMemberException;

@Embeddable
public record MemberName(
        @Column(name = "name", nullable = false, unique = true, length = 10)
        String value
) {
    private static final int MAX_NAME_LENGTH = 10;

    public static MemberName from(String name) {
        validate(name);
        return new MemberName(name);
    }

    private static void validate(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidMemberException("이름은 비어있을 수 없습니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new InvalidMemberException("이름은 10글자 이하이어야합니다.");
        }
    }
}
