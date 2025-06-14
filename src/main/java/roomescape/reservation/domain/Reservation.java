package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import roomescape.member.domain.Member;
import roomescape.reservation.util.BaseTimeEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "room_escape_information_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RoomEscapeInformation roomEscapeInformation;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Builder
    private Reservation(
            final Long id,
            @NonNull final RoomEscapeInformation roomEscapeInformation,
            @NonNull final Member member,
            @NonNull final ReservationStatus reservationStatus
    ) {
        this.id = id;
        this.roomEscapeInformation = roomEscapeInformation;
        this.member = member;
        this.status = reservationStatus;
    }

    public static Reservation of(final RoomEscapeInformation roomEscapeInformation, final Member member) {
        return Reservation.builder()
                .roomEscapeInformation(roomEscapeInformation)
                .member(member)
                .reservationStatus(ReservationStatus.BOOKED)
                .build();
    }

    public static Reservation booked(final RoomEscapeInformation roomEscapeInformation, final Member member) {
        return Reservation.builder()
                .roomEscapeInformation(roomEscapeInformation)
                .member(member)
                .reservationStatus(ReservationStatus.BOOKED)
                .build();
    }

    public void cancel() {
        if (this.status == ReservationStatus.CANCELLED) {
                throw new IllegalStateException("이미 취소된 예약입니다.");
        }
        this.status = ReservationStatus.CANCELLED;
    }

    public boolean isBooked() {
        return this.status.isBooked();
    }
}
