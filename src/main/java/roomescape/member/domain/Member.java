package roomescape.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.http.HttpStatus;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    public Member() {
    }

    public Member(
            final String name,
            final String email,
            final String password,
            final Role role
    ) {
        this(null, name, email, password, role);
    }

    public Member(final Long id, final Member member) {
        this(id, member.name, member.email, member.password, member.role);
    }

    public Member(
            final Long id,
            final String name,
            final String email,
            final String password,
            final Role role
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;

        validateRole();
    }

    private void validateRole() {
        if (role == null) {
            throw new RoomEscapeException(ErrorType.REQUEST_DATA_BLANK, String.format("[values: %s]", this),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
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
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name=" + name +
                ", email=" + email +
                ", password=" + password +
                ", role=" + role +
                '}';
    }
}
