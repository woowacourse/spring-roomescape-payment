package roomescape.domain.member;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Table(name = "member")
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "email", unique = true)
    private String email;

    @NotNull
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "role")
    private Role role;

    public Member(String name, String email, String password, Role role) {
        this(null, name, email, password, role);
    }

    public Member(Long id, String name, String email, String password, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    protected Member() {
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

    public String getRole() {
        return role.toString();
    }

    public boolean isNotAdmin() {
        return role != Role.ADMIN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        if (id == null || member.id == null) {
            return Objects.equals(email, member.email);
        }
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return Objects.hash(email);
        }
        return Objects.hash(id);
    }
}
