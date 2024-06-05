package roomescape.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.member.Email;
import roomescape.domain.member.Member;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;

public interface MemberRepository extends JpaRepository<Member, Long> {

    default Member findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new RoomEscapeException(
                ErrorCode.USER_NOT_FOUND_BY_ID,
                "user_id = " + id
        ));
    }

    default Member findByEmailOrThrow(Email email) {
        return findByEmail(email).orElseThrow(() -> new RoomEscapeException(
                ErrorCode.USER_NOT_FOUND_BY_EMAIL,
                "email = " + email.getEmail()
        ));
    }

    Optional<Member> findByEmail(Email email);
}
