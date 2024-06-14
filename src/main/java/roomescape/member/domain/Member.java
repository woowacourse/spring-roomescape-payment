package roomescape.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.Optional;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Embedded
    private MemberName name;
    @Embedded
    private MemberEmail email;
    @Embedded
    private MemberPassword password;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRole role;

    private Member(Long id, String name, String email, MemberRole role) {
        this.id = Optional.ofNullable(id).orElseThrow(() ->
                new RoomEscapeException("사용자 id는 null 일 수 없습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));
        this.name = new MemberName(name);
        this.email = new MemberEmail(email);
        this.role = Optional.ofNullable(role).orElseThrow(() ->
                new RoomEscapeException("사용자 역할은 null 일 수 없습니다.", ExceptionTitle.ILLEGAL_USER_REQUEST));
    }

    public Member(Long id, String name, String email, String password, MemberRole role) {
        this(id, name, email, role);
        this.password = new MemberPassword(password);
    }

    public Member(Long id, String name, String email) {
        this(id, name, email, MemberRole.USER);
    }

    protected Member() {
    }

    public boolean isAdmin() {
        return role.isAdmin();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.name();
    }

    public String getEmail() {
        return email.email();
    }

    public MemberRole getRole() {
        return role;
    }

    public String getPassword() {
        return password.password();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
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
