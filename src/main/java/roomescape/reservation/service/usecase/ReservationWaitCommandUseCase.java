package roomescape.reservation.service.usecase;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.common.exception.BadRequestException;
import roomescape.common.exception.ConflictException;
import roomescape.common.exception.NotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.service.usecase.MemberQueryUseCase;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.ReservationWait;
import roomescape.reservation.log.ReservationWaitingProbe;
import roomescape.reservation.repository.ReservationWaitRepository;
import roomescape.reservation.service.converter.ReservationWaitConverter;
import roomescape.reservation.service.dto.CreateReservationServiceRequest;
import roomescape.theme.domain.Theme;
import roomescape.theme.service.usecase.ThemeQueryUseCase;
import roomescape.time.domain.ReservationTime;
import roomescape.time.service.usecase.ReservationTimeQueryUseCase;

@Service
@RequiredArgsConstructor
public class ReservationWaitCommandUseCase {

    private final ReservationWaitRepository reservationWaitRepository;
    private final ReservationQueryUseCase reservationQueryUseCase;
    private final ReservationTimeQueryUseCase reservationTimeQueryUseCase;
    private final ThemeQueryUseCase themeQueryUseCase;
    private final MemberQueryUseCase memberQueryUseCase;
    private final ReservationWaitingProbe reservationWaitingProbe;

    public ReservationWait create(final CreateReservationServiceRequest createReservationServiceRequest) {
        validateReservationExists(createReservationServiceRequest);
        validateReservationWaitNotExistsForMember(createReservationServiceRequest);

        final ReservationDate reservationDate = ReservationDate.from(createReservationServiceRequest.date());
        final ReservationTime reservationTime = reservationTimeQueryUseCase.get(
                createReservationServiceRequest.timeId()
        );
        validatePast(reservationDate, reservationTime);

        final Theme theme = themeQueryUseCase.get(createReservationServiceRequest.themeId());
        final Member member = memberQueryUseCase.get(createReservationServiceRequest.memberId());

        ReservationWait waiting = reservationWaitRepository.save(
                ReservationWaitConverter.toDomain(
                        createReservationServiceRequest,
                        member,
                        reservationTime,
                        theme
                )
        );
        reservationWaitingProbe.create(waiting);
        return waiting;
    }

    private void validateReservationExists(final CreateReservationServiceRequest createReservationServiceRequest) {
        final Reservation reservation = reservationQueryUseCase.getByParams(
                ReservationDate.from(createReservationServiceRequest.date()),
                createReservationServiceRequest.timeId(),
                createReservationServiceRequest.themeId()
        );
        if (reservation.getMember().getId() == createReservationServiceRequest.memberId()) {
            throw new ConflictException("이미 해당 예약을 한 사용자입니다.");
        }
    }

    private void validateReservationWaitNotExistsForMember(
            final CreateReservationServiceRequest createReservationServiceRequest
    ) {
        if (reservationWaitRepository.existsByInfoDateAndInfoTimeIdAndInfoThemeIdAndInfoMemberId(
                ReservationDate.from(createReservationServiceRequest.date()),
                createReservationServiceRequest.timeId(),
                createReservationServiceRequest.themeId(),
                createReservationServiceRequest.memberId()
        )) {
            throw new ConflictException("이미 해당 예약 대기를 한 사용자입니다.");
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

    public void delete(final long waitingId) {
        ReservationWait waiting = getById(waitingId);
        reservationWaitRepository.delete(waiting);
        reservationWaitingProbe.delete(waiting);
    }

    private ReservationWait getById(final long waitingId) {
        return reservationWaitRepository.findById(waitingId)
                .orElseThrow(NotFoundException::new);
    }
}
