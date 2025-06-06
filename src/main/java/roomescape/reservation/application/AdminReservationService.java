package roomescape.reservation.application;

import static roomescape.reservation.domain.ReservationStatus.BOOKED;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.resource.AlreadyExistException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationSlot;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.domain.repository.WaitingRepository;
import roomescape.reservation.ui.dto.request.CreateBookedReservationRequest;
import roomescape.reservation.ui.dto.request.FilteredReservationsRequest;
import roomescape.reservation.ui.dto.response.ReservationResponse;
import roomescape.reservation.ui.dto.response.ReservationStatusResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminReservationService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;

    @Transactional
    public ReservationResponse create(final CreateBookedReservationRequest request) {
        log.info("관리자 예약 생성 요청 - memberId: {}, themeId: {}, timeId: {}, date: {}",
                request.memberId(), request.themeId(), request.timeId(), request.date());

        final ReservationTime time = reservationTimeRepository.getById(request.timeId());
        final Theme theme = themeRepository.getById(request.themeId());
        final Member member = memberRepository.getById(request.memberId());

        final Reservation reservation = createBookedReservation(request.date(), time, theme, member);

        log.info("관리자 예약 생성 완료 - reservationId: {}", reservation.getId());

        return ReservationResponse.from(reservation);
    }

    private Reservation createBookedReservation(
            final LocalDate date,
            final ReservationTime time,
            final Theme theme,
            final Member member
    ) {
        final ReservationSlot reservationSlot = ReservationSlot.of(date, time, theme);

        if (reservationRepository.existsByReservationSlot(reservationSlot)) {
            log.warn("중복 예약 시도 - themeId: {}, date: {}, time: {}", theme.getId(), date, time.getStartAt());
            throw new AlreadyExistException("해당 예약 슬롯에 예약이 있습니다.");
        }

        final Reservation reservation = Reservation.of(reservationSlot, member, BOOKED);

        log.info("예약 저장 완료 - memberId: {}, themeId: {}, time: {}, date: {}",
                member.getId(), theme.getId(), time.getStartAt(), date);

        return reservationRepository.save(reservation);
    }

    @Transactional
    public void deleteAsAdmin(final Long reservationId) {
        log.info("관리자 예약 삭제 요청 - reservationId: {}", reservationId);

        final Reservation reservation = reservationRepository.getById(reservationId);
        final Optional<Waiting> optionalWaiting = waitingRepository.findFirstByReservationSlotOrderByCreatedAt(
                reservation.getReservationSlot()
        );

        if (reservation.getPaymentInfo() != null) {
            log.info("예약 결제 연동 해제 - paymentKey: {}", reservation.getPaymentInfo().getPaymentKey());
            reservation.getPaymentInfo().disconnectReservation();
        }

        if (optionalWaiting.isPresent()) {
            final Waiting waiting = optionalWaiting.get();
            log.info("예약 삭제 후 대기 전환 - waitingId: {}, waitingMemberId: {}", waiting.getId(),
                    waiting.getMember().getId());
            reservation.updateMember(waiting.getMember());
            waitingRepository.delete(waiting);
            return;
        }

        reservationRepository.deleteById(reservationId);

        log.info("예약 삭제 완료 - reservationId: {}", reservationId);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        log.info("관리자 전체 예약 조회 요청");

        final List<ReservationResponse> results = reservationRepository.findAll()
                .stream()
                .map(ReservationResponse::from)
                .toList();

        log.info("전체 예약 조회 완료 - 개수: {}", results.size());

        return results;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllByFilter(final FilteredReservationsRequest request) {
        log.info("예약 필터 조회 요청 - themeId: {}, memberId: {}, from: {}, to: {}",
                request.themeId(), request.memberId(), request.dateFrom(), request.dateTo());

        if (request.dateFrom().isAfter(request.dateTo())) {
            log.warn("필터 조회 실패 - 시작일이 종료일보다 늦음");
            throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
        }

        final List<ReservationResponse> results = reservationRepository
                .findAllByThemeIdAndMemberIdAndDateRange(
                        request.themeId(), request.memberId(), request.dateFrom(), request.dateTo()
                )
                .stream()
                .map(ReservationResponse::from)
                .toList();

        log.info("예약 필터 조회 완료 - 결과 수: {}", results.size());

        return results;
    }

    @Transactional(readOnly = true)
    public List<ReservationStatusResponse> findAllReservationStatuses() {
        log.info("예약 상태 목록 조회 요청");

        final List<ReservationStatusResponse> statuses = Arrays.stream(ReservationStatus.values())
                .map(ReservationStatusResponse::from)
                .toList();

        log.info("예약 상태 목록 조회 완료 - 개수: {}", statuses.size());

        return statuses;
    }
}
