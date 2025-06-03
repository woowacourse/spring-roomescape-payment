package roomescape.reservation.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import roomescape.member.domain.Member;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Member member;

    @Embedded
    private RegistrationSlot registrationSlot;

    @Builder
    private Reservation(
            final Long id,
            @NonNull final Member member,
            @NonNull final RegistrationSlot registrationSlot
    ) {
        this.id = id;
        this.member = member;
        this.registrationSlot = registrationSlot;
    }

    public static Reservation createNew(final Member member, final RegistrationSlot registrationSlot) {
        return new Reservation(null, member, registrationSlot);
    }

    public LocalDate getDate() {
        return registrationSlot.getDate();
    }

    public ReservationTime getTime() {
        return registrationSlot.getTime();
    }

    public Theme getTheme() {
        return registrationSlot.getTheme();
    }

    public boolean isPast() {
        return registrationSlot.isPast();
    }
}
