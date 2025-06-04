package roomescape.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Embedded
    private Password password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String sessionId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberRole role;

    @Builder
    public Member(String email, String password, String name, MemberRole role) {
        this.email = email;
        this.password = Password.encode(password);
        this.name = name;
        this.role = role;
        this.sessionId = "";
    }

    public void updateSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
