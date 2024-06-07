package roomescape.domain.member;

import jakarta.persistence.Embeddable;
import roomescape.exception.member.InvalidMemberPasswordLengthException;

import java.util.Objects;

@Embeddable
public class MemberPassword {
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 16;

    private String password;

    protected MemberPassword() {
    }

    public MemberPassword(String password) {
        validate(password);
        this.password = password;
    }

    private void validate(String password) {
        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            throw new InvalidMemberPasswordLengthException();
        }
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemberPassword that = (MemberPassword) o;
        return Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(password);
    }
}
