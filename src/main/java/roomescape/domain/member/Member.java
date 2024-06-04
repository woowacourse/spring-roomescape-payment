package roomescape.domain.member;

import static roomescape.exception.RoomescapeExceptionCode.UNAUTHORIZED;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import roomescape.exception.RoomescapeException;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @Column(nullable = false)
    private Name name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    protected Member() {
    }

    public Member(final Long id, final Name name, final String email) {
        this(id, name, email, null, null);
    }

    public Member(final Name name, final String email, final String password, final Role role) {
        this(null, name, email, password, role);
    }

    public Member(final Long id, final Name name, final String email, final String password, final Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void checkIncorrectPassword(final String other) {
        if (!Objects.equals(getPassword(), other)) {
            throw new RoomescapeException(UNAUTHORIZED);
        }
    }

    public Long getId() {
        return id;
    }

    public String getNameString() {
        return name.getName();
    }

    public Name getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof Member other
                && Objects.equals(getId(), other.getId())
                && Objects.equals(getName(), other.getName())
                && Objects.equals(getEmail(), other.getEmail())
                && Objects.equals(getPassword(), other.getPassword())
                && Objects.equals(getRole(), other.getRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password);
    }
}
