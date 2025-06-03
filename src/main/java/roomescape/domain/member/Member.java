package roomescape.domain.member;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import roomescape.infrastructure.error.exception.MemberException;

import java.util.Objects;

@Entity
public class Member {

    private static final int MAX_NAME_LENGTH = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Embedded
    private Email email;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    public Member(final String name, final Email email, final String password, final MemberRole role) {
        this(null, name, email, password, role);
    }

    public Member(final String name, final Email email, final String password) {
        this(null, name, email, password, MemberRole.NORMAL);
    }

    public Member(final Long id, final String name, final Email email, final String password, final MemberRole role) {
        validateName(name);
        validatePassword(password);
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    protected Member() {
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new MemberException("사용자명은 비어있을 수 없습니다.");
        }
        if (MAX_NAME_LENGTH < name.length()) {
            throw new MemberException("사용자명은 %d자 이하여야 합니다.".formatted(MAX_NAME_LENGTH));
        }
    }

    private void validatePassword(final String password) {
        if (password.isBlank()) {
            throw new MemberException("비밀번호는 비어있을 수 없습니다.");
        }
    }

    public boolean isNotPassword(final String password) {
        return !this.password.equals(password);
    }

    public boolean isAdmin() {
        return this.role == MemberRole.ADMIN;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public MemberRole getRole() {
        return role;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Member member = (Member) o;
        return Objects.equals(id, member.id)
               && Objects.equals(name, member.name)
               && Objects.equals(email, member.email)
               && Objects.equals(password, member.password)
               && role == member.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password, role);
    }
}
