package roomescape.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.member.Role;
import roomescape.domain.member.exception.MemberException;

@Getter
@NoArgsConstructor
@Entity
public class Member {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public Member(final Long id, final String name, final String email, final String password, final Role role) {
        validateName(name);
        validateEmail(email);
        validateEmailFormat(email);
        validatePassword(password);
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Member(final String name, final String email, final String password, final Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Member(final Long id, final String name, final String email, final Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = null;
    }

    private void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new MemberException("이름은 NULL, 공백이 허용되지 않습니다.");
        }
    }

    private void validateEmail(final String email) {
        if (email == null || email.isBlank()) {
            throw new MemberException("이메일은 NULL, 공백이 허용되지 않습니다.");
        }
    }

    private void validatePassword(final String password) {
        if (password == null || password.isBlank()) {
            throw new MemberException("비밀번호는 NULL, 공백이 허용되지 않습니다.");
        }
    }

    private void validateEmailFormat(final String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new MemberException("이메일 형식이 일치하지 않습니다.");
        }
    }
}
