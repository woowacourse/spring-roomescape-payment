package roomescape.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;
import roomescape.exception.RoomescapeException;

@Embeddable
public class Email {
    private static final Pattern EMAIL_REGEX = Pattern.compile("^(.+)@(\\S+)$");
    private static final int MIN_LENGTH = 11;
    private static final int MAX_LENGTH = 40;

    @Column(nullable = false, unique = true)
    private String email;

    protected Email() {
    }

    public Email(String email) {
        if (email == null || email.isBlank()) {
            throw new RoomescapeException("이메일은 필수 입력값 입니다.");
        }
        if (!EMAIL_REGEX.matcher(email).matches()) {
            throw new RoomescapeException("이메일 형식이 올바르지 않습니다.");
        }
        int emailLength = email.length();
        if (emailLength < MIN_LENGTH || MAX_LENGTH < emailLength) {
            throw new RoomescapeException(
                    String.format("이메일의 길이는 최소 %d자 이상 %d자 이하만 가능합니다. 현재 이메일 길이:%d", MIN_LENGTH, MAX_LENGTH,
                            emailLength)
            );
        }
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Email email1 = (Email) o;
        return Objects.equals(email, email1.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
