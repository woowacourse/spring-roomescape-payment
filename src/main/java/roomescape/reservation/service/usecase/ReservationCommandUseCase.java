package roomescape.reservation.service.usecase;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.AlreadyExistException;
import roomescape.common.exception.NotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.service.usecase.MemberQueryUseCase;
import roomescape.payment.domain.Payment;
import roomescape.payment.service.usecase.PaymentQueryUseCase;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.converter.ReservationConverter;
import roomescape.reservation.service.dto.CreateReservationServiceRequest;
import roomescape.reservation.service.dto.CreateReservationWithPaymentServiceRequest;
import roomescape.theme.domain.Theme;
import roomescape.theme.service.usecase.ThemeQueryUseCase;
import roomescape.time.domain.ReservationTime;
import roomescape.time.service.usecase.ReservationTimeQueryUseCase;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandUseCase {

    private final ReservationRepository reservationRepository;
    private final ReservationQueryUseCase reservationQueryUseCase;
    private final ReservationTimeQueryUseCase reservationTimeQueryUseCase;
    private final ThemeQueryUseCase themeQueryUseCase;
    private final MemberQueryUseCase memberQueryUseCase;
    private final PaymentQueryUseCase paymentQueryUseCase;

    public Reservation create(final CreateReservationServiceRequest request) {
        validateExistReservation(request.date(), request.timeId(), request.themeId());

        final ReservationTime reservationTime = reservationTimeQueryUseCase.get(
                request.timeId());

        final Theme theme = themeQueryUseCase.get(request.themeId());

        final Member member = memberQueryUseCase.get(request.memberId());

        return reservationRepository.save(
                ReservationConverter.toDomain(
                        request,
                        member,
                        reservationTime,
                        theme
                )
        );
    }

    public Reservation create(final CreateReservationWithPaymentServiceRequest request) {
        validateExistReservation(request.date(), request.timeId(), request.themeId());

        final ReservationTime reservationTime = reservationTimeQueryUseCase.get(
                request.timeId());

        final Theme theme = themeQueryUseCase.get(request.themeId());

        final Member member = memberQueryUseCase.get(request.memberId());

        final Payment payment = paymentQueryUseCase.get(request.paymentId());

        return reservationRepository.save(
                ReservationConverter.toDomain(
                        request,
                        member,
                        reservationTime,
                        theme,
                        payment
                )
        );
    }

    public void delete(final Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new NotFoundException("id: %d에 해당하는 예약을 찾을 수 없습니다.", id);
        }
        reservationRepository.deleteById(id);
    }

    private void validateExistReservation(final LocalDate date, final Long timeId, final Long themeId) {
        if (reservationQueryUseCase.existsByParams(
                ReservationDate.from(date),
                timeId,
                themeId)
        ) {
            throw new AlreadyExistException("추가하려는 예약이 이미 존재합니다.");
        }
    }
}
