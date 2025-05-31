package roomescape.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import roomescape.exception.BusinessRuleViolationException;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity(name = "USERS")
public class User {

    private static final int NAME_MAX_LENGTH = 5;
    private static final int PASSWORD_MAX_LENGTH = 30;

    private static final String VALID_EMAIL_FORMAT = "\\w+@\\w+\\.\\w+";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private User(final Long id,
                 final String name,
                 final UserRole role,
                 final String email,
                 final String password) {

        validateName(name);
        validateEmail(email);
        validatePassword(password);

        this.id = id;
        this.name = name;
        this.role = role;
        this.email = email;
        this.password = password;
    }

    public static User ofExisting(final long id,
                                  final String name,
                                  final UserRole role,
                                  final String email,
                                  final String password) {
        return new User(id, name, role, email, password);
    }

    public static User register(final String name, final String email, final String password) {
        return new User(null, name, UserRole.USER, email, password);
    }

    public boolean matchesPassword(final String passwordToCompare) {
        return password.equals(passwordToCompare);
    }

    private void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessRuleViolationException("이름은 null이거나 공백일 수 없습니다.");
        }

        if (name.length() > NAME_MAX_LENGTH) {
            throw new BusinessRuleViolationException(String.format("이름은 %d자를 넘길 수 없습니다.", NAME_MAX_LENGTH));
        }
    }

    private void validateEmail(final String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessRuleViolationException("이메일은 null이거나 공백일 수 없습니다.");
        }
        if (!email.matches(VALID_EMAIL_FORMAT)) {
            throw new BusinessRuleViolationException("잘못된 형식의 이메일입니다 : " + email);
        }
    }

    private void validatePassword(final String password) {
        if (password == null || password.isBlank()) {
            throw new BusinessRuleViolationException("비밀번호는 null이거나 공백일 수 없습니다.");
        }

        if (password.length() > PASSWORD_MAX_LENGTH) {
            throw new BusinessRuleViolationException(String.format("비밀번호는 %d자를 넘길 수 없습니다.", PASSWORD_MAX_LENGTH));
        }
    }
}
