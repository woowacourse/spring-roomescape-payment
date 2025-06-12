package roomescape.reservation.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import roomescape.common.exception.BadRequestException;
import roomescape.member.domain.Member;
import roomescape.theme.domain.Theme;
import roomescape.waiting.domain.ReservationInformation;
import roomescape.waiting.domain.Waiting;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ReservationInformation reservationInformation;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    protected Reservation() {
    }

    public Reservation(
            final Long id,
            final Member member,
            final ReservationInformation reservationInformation,
            final ReservationStatus status
    ) {
        this.id = id;
        this.member = member;
        this.reservationInformation = reservationInformation;
        this.status = status;
    }

    public Reservation(Member member, ReservationInformation reservationInformation, ReservationStatus status) {
        this(null, member, reservationInformation, status);
    }

    public Reservation(
            final Member member,
            final LocalDate date,
            final ReservationTime time,
            final Theme theme,
            final ReservationStatus status
    ) {
        this(member, new ReservationInformation(date, time, theme), status);
    }

    public static Reservation of(Waiting waiting) {
        return new Reservation(
                waiting.getMember(),
                waiting.getReservationInformation(),
                ReservationStatus.CONFIRMED
        );
    }

    public boolean isBefore(LocalDateTime compare) {
        return reservationInformation.isBefore(compare);
    }

    public void confirm() {
        if (status != ReservationStatus.PENDING) {
            throw new BadRequestException("예약 확정이 불가능한 상태입니다.");
        }
        status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        if (status == ReservationStatus.CANCELED) {
            throw new BadRequestException("예약 취소가 불가능한 상태입니다.");
        }
        status = ReservationStatus.CANCELED;
    }

    public LocalDate getDate() {
        return reservationInformation.getDate();
    }

    public ReservationTime getTime() {
        return reservationInformation.getTime();
    }

    public Theme getTheme() {
        return reservationInformation.getTheme();
    }
}
