package roomescape.domain.member;

import static roomescape.exception.RoomescapeExceptionCode.EMPTY_NAME;
import static roomescape.exception.RoomescapeExceptionCode.INVALID_NAME_FORMAT;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.persistence.Embeddable;

import roomescape.exception.RoomescapeException;

@Embeddable
public class Name {

    private static final Pattern NAME_PATTERN = Pattern.compile("^\\d+$");

    private String name;

    public Name() {
    }

    public Name(final String name) {
        validate(name);
        this.name = name;
    }

    private void validate(final String name) {
        if (name == null || name.isBlank()) {
            throw new RoomescapeException(EMPTY_NAME);
        }
        final Matcher matcher = NAME_PATTERN.matcher(name);
        if (matcher.matches()) {
            throw new RoomescapeException(INVALID_NAME_FORMAT);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof Name other
                && Objects.equals(getName(), other.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
