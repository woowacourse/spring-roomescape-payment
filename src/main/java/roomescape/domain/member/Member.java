package roomescape.domain.member;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(fluent = true)
@EqualsAndHashCode(of = "id")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;
    @Embedded
    private Email email;
    @Embedded
    private Password password;

    private Member(final Long id, final Name name, final Email email, final Password password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static Member createWithoutId(final String name, final String email, final String password) {
        return new Member(null, new Name(name), new Email(email), new Password(password));
    }

    public boolean isSamePassword(final String password) {
        String storedPassword = this.password.password();
        return storedPassword.equals(password);
    }

    public String getName() {
        return this.name.name();
    }

    public String getEmail() {
        return this.email.email();
    }

    public String getPassword() {
        return this.password.password();
    }
}
