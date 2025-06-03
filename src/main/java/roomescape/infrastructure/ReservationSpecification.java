package roomescape.infrastructure;

import org.springframework.data.jpa.domain.Specification;
import roomescape.domain.Reservation;

import java.time.LocalDate;

public class ReservationSpecification {
    public static Specification<Reservation> memberIdEqual(Long memberId) {
        return (root, query, cb) -> {
            if (memberId == null) {
                return null;
            }
            return cb.equal(root.get("member").get("id"), memberId);
        };
    }

    public static Specification<Reservation> themeIdEqual(Long themeId) {
        return (root, query, cb) -> {
            if (themeId == null) {
                return null;
            }
            return cb.equal(root.get("theme").get("id"), themeId);
        };
    }

    public static Specification<Reservation> dateBetween(LocalDate dateFrom, LocalDate dateTo) {
        return (root, query, cb) -> {
            if (dateFrom == null || dateTo == null) {
                return null;
            }
            return cb.between(root.get("date"), dateFrom, dateTo);
        };
    }
}
