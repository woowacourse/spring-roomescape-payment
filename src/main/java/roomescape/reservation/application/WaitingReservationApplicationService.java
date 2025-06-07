package roomescape.reservation.application;

import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.application.MemberDataService;
import roomescape.member.domain.Member;
import roomescape.reservation.application.dto.request.WaitingReservationCreateRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.presentation.dto.response.WaitingWebResponse;
import roomescape.reservationslot.application.ReservationSlotDataService;
import roomescape.reservationslot.domain.ReservationSlot;
import roomescape.reservationslot.presentation.dto.response.ReservationResponse;

@Service
@Transactional
@Slf4j
public class WaitingReservationApplicationService {

    private final ReservationSlotDataService reservationSlotDataService;
    private final MemberDataService memberDataService;
    private final ReservationDataService reservationDataService;

    public WaitingReservationApplicationService(final ReservationSlotDataService reservationSlotDataService,
                                                final MemberDataService memberDataService,
                                                final ReservationDataService reservationDataService) {
        this.reservationSlotDataService = reservationSlotDataService;
        this.memberDataService = memberDataService;
        this.reservationDataService = reservationDataService;
    }

    public ReservationResponse create(final WaitingReservationCreateRequest createRequest) {
        log.info("대기 예약 생성 시도: memberId={}, themeId={}, date={}, timeId={}",
                createRequest.memberId(), createRequest.themeId(), createRequest.date(), createRequest.timeId());

        ReservationSlot slot = reservationSlotDataService.getReservationSlotByDateAndTimeAndTheme(createRequest.date(),
                createRequest.timeId(), createRequest.themeId());
        Member member = memberDataService.getById(createRequest.memberId());
        Reservation reservation = slot.addReservation(member, LocalDateTime.now());
        Reservation savedReservation = reservationDataService.save(reservation);

        log.info("대기 예약 생성 완료: reservationId={}, memberId={}",
                savedReservation.getId(), createRequest.memberId());
        return ReservationResponse.from(savedReservation);
    }

    public List<WaitingWebResponse> findAll() {
        List<Reservation> reservations = reservationDataService.findAllWaitingReservations();
        return reservations.stream()
                .map(WaitingWebResponse::from)
                .toList();
    }

    public void cancelByReservationSlotIdAndMemberId(final Long reservationSlotId, final Long memberId) {
        log.info("대기 예약 취소 시도: reservationSlotId={}, memberId={}", reservationSlotId, memberId);

        Reservation reservation = reservationDataService.getByReservationSlotIdAndMemberId(reservationSlotId, memberId);
        reservationDataService.deleteByReservationSlotIdAndMemberId(reservationSlotId, memberId);
        ReservationSlot reservationSlot = reservationSlotDataService.getById(reservationSlotId);
        reservationSlot.getReservations().remove(reservation);

        log.info("대기 예약 취소 완료: reservationId={}, memberId={}", reservation.getId(), memberId);
    }

    public void cancel(final Long reservationId) {
        log.info("대기 예약 취소 시도: reservationId={}", reservationId);

        Reservation reservation = reservationDataService.getById(reservationId);
        reservationDataService.cancel(reservation);
        ReservationSlot reservationSlot = reservationSlotDataService.getById(reservation.getReservationSlot().getId());
        reservationSlot.getReservations().remove(reservation);

        log.info("대기 예약 취소 완료: reservationId={}, memberId={}", reservationId, reservation.getMember().getId());
    }
}
