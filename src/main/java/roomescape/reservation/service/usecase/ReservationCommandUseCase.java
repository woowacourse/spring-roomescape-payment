package roomescape.reservation.service.usecase;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.BadRequestException;
import roomescape.common.exception.ConflictException;
import roomescape.member.domain.Member;
import roomescape.member.service.usecase.MemberQueryUseCase;
import roomescape.payment.domain.PaymentHistory;
import roomescape.payment.repository.PaymentHistoryRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.ReservationWait;
import roomescape.reservation.log.ReservationProbe;
import roomescape.reservation.log.ReservationWaitingProbe;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.converter.ReservationConverter;
import roomescape.reservation.service.dto.CreateReservationServiceRequest;
import roomescape.theme.domain.Theme;
import roomescape.theme.service.usecase.ThemeQueryUseCase;
import roomescape.time.domain.ReservationTime;
import roomescape.time.service.usecase.ReservationTimeQueryUseCase;

@Service
@RequiredArgsConstructor
public class ReservationCommandUseCase {

    private final ReservationRepository reservationRepository;
    private final ReservationQueryUseCase reservationQueryUseCase;
    private final ReservationWaitCommandUseCase reservationWaitCommandUseCase;
    private final ReservationTimeQueryUseCase reservationTimeQueryUseCase;
    private final ThemeQueryUseCase themeQueryUseCase;
    private final MemberQueryUseCase memberQueryUseCase;
    private final ReservationWaitQueryUseCase reservationWaitQueryUseCase;
    private final ReservationProbe reservationProbe;
    private final ReservationWaitingProbe reservationWaitingProbe;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Transactional
    public Reservation create(final CreateReservationServiceRequest createReservationServiceRequest) {
        validateReservationNotExists(createReservationServiceRequest);
        final ReservationDate reservationDate = ReservationDate.from(createReservationServiceRequest.date());
        final ReservationTime reservationTime = reservationTimeQueryUseCase.get(
                createReservationServiceRequest.timeId());
        validatePast(reservationDate, reservationTime);

        final Theme theme = themeQueryUseCase.get(createReservationServiceRequest.themeId());
        final Member member = memberQueryUseCase.get(createReservationServiceRequest.memberId());

        Reservation reservation = ReservationConverter.toDomain(createReservationServiceRequest, member,
                reservationTime, theme);
        Reservation savedReservation = reservationRepository.save(reservation);
        reservationProbe.create(savedReservation);
        return savedReservation;
    }

    private void validateReservationNotExists(final CreateReservationServiceRequest createReservationServiceRequest) {
        if (reservationQueryUseCase.existsByParams(
                ReservationDate.from(createReservationServiceRequest.date()),
                createReservationServiceRequest.timeId(),
                createReservationServiceRequest.themeId())) {

            throw new ConflictException("추가하려는 예약이 이미 존재합니다.");
        }
    }

    private void validatePast(final ReservationDate date, final ReservationTime time) {
        final LocalDateTime now = LocalDateTime.now();
        if (date.isAfter(now.toLocalDate())) {
            return;
        }
        if (date.isBefore(now.toLocalDate())) {
            throw new BadRequestException("지난 날짜는 예약할 수 없습니다.");
        }
        if (time.isBefore(now.toLocalTime())) {
            throw new BadRequestException("이미 지난 시간에는 예약할 수 없습니다.");
        }
    }

    @Transactional
    public void delete(final Long id) {
        final Reservation reservation = reservationQueryUseCase.get(id);
        cancelPayment(reservation);
        reservationRepository.delete(reservation);
        adjustWaitingIfExists(reservation);
        reservationProbe.delete(reservation);
    }

    private void cancelPayment(Reservation reservation) {
        // 결제 취소했다고 가정
        paymentHistoryRepository.findByReservation(reservation)
                .ifPresent(PaymentHistory::cancel);
    }

    private void adjustWaitingIfExists(Reservation reservation) {
        Optional<ReservationWait> firstWait = reservationWaitQueryUseCase.findByParamsAt(
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId(),
                0
        );
        firstWait.ifPresent(this::promotionReservationWait);
    }

    private void promotionReservationWait(final ReservationWait firstReservationWait) {
        reservationWaitCommandUseCase.delete(firstReservationWait.getId());
        Reservation savedReservation = reservationRepository.save(firstReservationWait.toReservation());
        reservationWaitingProbe.promote(firstReservationWait);
        reservationProbe.create(savedReservation);
    }
}
