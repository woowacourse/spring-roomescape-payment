package roomescape.member.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Embedded
    private MemberName name;

    @Embedded
    private Email email;

    @Embedded
    private Password password;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false)
    private MemberRole memberRole;

    public Member(final String name, final String email, final String password,
                  final MemberRole memberRole) {
        this.name = MemberName.from(name);
        this.email = Email.from(email);
        this.password = Password.from(password);
        this.memberRole = memberRole;
    }

    protected Member() {
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof final Member member)) {
            return false;
        }
        return Objects.equals(getId(), member.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.value();
    }

    public String getEmail() {
        return email.value();
    }

    public String getPassword() {
        return password.value();
    }

    public MemberRole getMemberRole() {
        return memberRole;
    }
}
