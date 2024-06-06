package roomescape.domain;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class MemberPassword {

    private String password;

    public MemberPassword() {
    }

    public MemberPassword(String password) {
        validate(password);
        this.password = password;
    }

    private void validate(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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