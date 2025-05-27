package roomescape.reservation.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import roomescape.member.domain.Member;
import roomescape.reservation.exception.InvalidStatusTransitionException;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member reserver;
    @Embedded
    private ReservationDateTime reservationDateTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Builder
    private Reservation(Member reserver, ReservationDateTime reservationDateTime, Theme theme,
                        ReservationStatus status) {
        this.reserver = reserver;
        this.reservationDateTime = reservationDateTime;
        this.theme = theme;
        this.status = status;
    }

    public static Reservation reserve(final Member reserver, final ReservationDateTime reservationDateTime,
                                      final Theme theme
    ) {
        return Reservation.builder()
                .reserver(reserver)
                .reservationDateTime(reservationDateTime)
                .theme(theme)
                .status(ReservationStatus.RESERVED)
                .build();
    }

    public static Reservation waiting(final Member reserver, final ReservationDateTime reservationDateTime,
                                      final Theme theme
    ) {
        return Reservation.builder()
                .reserver(reserver)
                .reservationDateTime(reservationDateTime)
                .theme(theme)
                .status(ReservationStatus.WAITING)
                .build();
    }

    public boolean isOwner(Long userId) {
        return reserver.isOwner(userId);
    }

    public void changeReserved() {
        if (status != ReservationStatus.WAITING) {
            throw new InvalidStatusTransitionException("대기 상태에서만 예약으로 변경할 수 있습니다.");
        }

        status = ReservationStatus.RESERVED;
    }

    public void cancelReservation() {
        if (status != ReservationStatus.RESERVED) {
            throw new InvalidStatusTransitionException("예약이 되어 있지 않습니다.");
        }

        status = ReservationStatus.CANCELED;
    }

    public void cancelWaiting() {
        if (status != ReservationStatus.WAITING) {
            throw new InvalidStatusTransitionException("대기 예약이 되어 있지 않습니다.");
        }

        status = ReservationStatus.CANCELED;
    }

    public String getReserverName() {
        return reserver.getName();
    }

    public String getThemeName() {
        return theme.getName();
    }

    public LocalDate getDate() {
        return reservationDateTime.getDate();
    }

    public LocalTime getStartAt() {
        return reservationDateTime.getStartAt();
    }

    public ReservationTime getReservationTime() {
        return reservationDateTime.getReservationTime();
    }

    public Long getTimeId() {
        return reservationDateTime.getTimeId();
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode() : getClass().hashCode();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        Reservation that = (Reservation) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }
}
