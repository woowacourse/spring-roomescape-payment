package roomescape.member.domain;

import jakarta.persistence.*;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private MemberName name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Member(final String name, final String email, final String password, final Role role) {
        this.name = new MemberName(name);
        this.email = email;
        this.password = password;
        this.role = role;
    }

    protected Member() {

    }

    public static Member createWithoutId(final String name, final String email, final String password, final Role role) {
        return new Member(name, email, password, role);
    }

    public String getName() {
        return name.getName();
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role.getRole();
    }

    public Long getId() {
        return id;
    }
}
