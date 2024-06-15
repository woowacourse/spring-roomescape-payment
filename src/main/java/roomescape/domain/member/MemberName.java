package roomescape.domain.member;

import jakarta.persistence.Embeddable;
import roomescape.exception.member.InvalidMemberNameLengthException;

@Embeddable
public record MemberName(String name) {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 5;

    public MemberName(String name) {
        this.name = name;
        validate(name);
    }

    private void validate(String name) {
        if (name.length() < MIN_LENGTH || name.length() > MAX_LENGTH) {
            throw new InvalidMemberNameLengthException();
        }
    }
}