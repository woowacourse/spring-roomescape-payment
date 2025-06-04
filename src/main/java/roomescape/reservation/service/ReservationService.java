package roomescape.reservation.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.NotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.RegistrationSlot;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationPolicy;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSearchRequest;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.dto.CreateRegistrationCommand;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationPolicy reservationPolicy;

    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public List<ReservationResponse> findReservationsByCriteria(final ReservationSearchRequest request) {
        final List<Reservation> reservations = reservationRepository.findByCriteria(
                request.themeId(),
                request.memberId(), request.dateFrom(),
                request.dateTo()
        );
        return ReservationResponse.fromReservations(reservations);
    }

    public ReservationResponse getById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약입니다, id: " + reservationId));
        return new ReservationResponse(reservation);
    }

    @Transactional
    public ReservationResponse registerReservation(CreateRegistrationCommand command) {
        final ReservationTime reservationTime = getReservationTimeById(command.timeId());
        final Theme theme = getThemeById(command.themeId());
        final Member member = getMemberById(command.memberId());
        Reservation reservation = Reservation.createNew(
                member, new RegistrationSlot(reservationTime, theme, command.date()));

        validateCanRegistration(reservation);
        final Reservation saved = reservationRepository.save(reservation);
        return new ReservationResponse(saved);
    }

    private void validateCanRegistration(Reservation reservation) {
        boolean existsSameSlot = reservationRepository.existsSameSlot(
                reservation.getDate(), reservation.getTime().getId(), reservation.getTheme().getId());
        reservationPolicy.validateReservationAvailable(reservation, existsSameSlot);
    }

    @Transactional
    public void deleteById(final Long id) {
        final Reservation reservation = reservationRepository.findById(id)
                .orElse(null);
        if (reservation == null) {
            return;
        }
        reservation.delete();
    }

    private Member getMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 멤버입니다."));
    }

    private ReservationTime getReservationTimeById(final Long timeId) {
        return reservationTimeRepository.findById(timeId).orElseThrow(() -> new NotFoundException("존재하지 않는 예약 시간입니다."));
    }

    private Theme getThemeById(final Long themeId) {
        return themeRepository.findById(themeId).orElseThrow(() -> new NotFoundException("존재하지 않는 테마입니다."));
    }
}
