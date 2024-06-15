package roomescape.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import roomescape.exception.member.InvalidMemberEmailPatternException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Embeddable
public record MemberEmail(
        @Column(name = "email", unique = true) String address) {
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    public MemberEmail(String address) {
        this.address = address;
        validate(address);
    }

    private void validate(String address) {
        Matcher matcher = ADDRESS_PATTERN.matcher(address);
        if (!matcher.matches()) {
            throw new InvalidMemberEmailPatternException();
        }
    }
}
