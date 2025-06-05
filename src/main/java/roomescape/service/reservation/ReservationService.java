package roomescape.service.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.response.MyPageReservationResponse;
import roomescape.dto.response.PendingReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.service.helper.MemberHelper;
import roomescape.service.helper.PaymentHelper;
import roomescape.service.helper.ReservationItemHelper;

import javax.naming.AuthenticationException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberHelper memberHelper;
    private final ReservationItemHelper itemHelper;
    private final PaymentHelper paymentHelper;

    @Transactional
    public ReservationResponse save(final CreateReservationRequest request) {
        return createReservation(request, ReservationStatus.ACCEPTED, false);
    }

    @Transactional
    public ReservationResponse pending(final CreateReservationRequest request) {
        return createReservation(request, ReservationStatus.PENDING, true);
    }

    private ReservationResponse createReservation(
            final CreateReservationRequest request,
            final ReservationStatus status,
            final boolean requiresExistingReservation
    ) {
        validateReservationAvailability(request.date(), request.timeId(), request.themeId(), requiresExistingReservation);
        final Member member = memberHelper.getById(request.memberId());
        final ReservationItem item = itemHelper.getOrCreate(request.date(), request.timeId(), request.themeId());
        validateDuplicateReservation(member, item);

        Reservation newReservation = Reservation.builder()
                .member(member)
                .reservationItem(item)
                .reservationStatus(status)
                .build();
        final Reservation saved = reservationRepository.save(newReservation);
        return ReservationResponse.from(saved);
    }

    private void validateReservationAvailability(
            final LocalDate date,
            final Long timeId,
            final Long themeId,
            final boolean requiresExistingReservation
    ) {
        final boolean reservationExists = itemHelper.isExistReservationItem(date, timeId, themeId);
        if (requiresExistingReservation && !reservationExists) {
            throw new IllegalArgumentException("[ERROR] 대기 예약은 기존 예약이 있을 때만 가능합니다.");
        }
        if (!requiresExistingReservation && reservationExists) {
            throw new IllegalArgumentException("[ERROR] 이미 예약된 시간입니다.");
        }
    }

    private void validateDuplicateReservation(final Member member, final ReservationItem reservationItem) {
        if (reservationRepository.existsByMemberAndReservationItem(member, reservationItem)) {
            throw new IllegalArgumentException("[ERROR] 이미 예약을 등록하였습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllFiltered(
            final Long memberId,
            final Long themeId,
            final LocalDate dateFrom,
            final LocalDate dateTo
    ) {
        final List<Reservation> reservations = reservationRepository.findByMemberIdAndThemeIdAndDateFromAndDateTo(
                memberId,
                themeId,
                dateFrom,
                dateTo
        );
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PendingReservationResponse> getAllPendings() {
        List<Reservation> pendingReservations = reservationRepository.findByReservationStatusOrderByIdDesc(ReservationStatus.PENDING);
        return pendingReservations.stream()
                .map(PendingReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MyPageReservationResponse> getAllBy(Long memberId) {
        final Member member = memberHelper.getById(memberId);
        List<Reservation> myReservations = reservationRepository.findByMemberId(member.getId());
        return myReservations.stream()
                .map(reservation -> {
                    if (reservation.getReservationStatus() == ReservationStatus.ACCEPTED) {
                        Payment payment = paymentHelper.getByReservationId(reservation.getId());
                        return MyPageReservationResponse.from(reservation, payment.getPaymentKey(), payment.getAmount());
                    }
                    return MyPageReservationResponse.from(reservation, null, null);
                })
                .toList();
    }

    @Transactional
    public void denyPending(Long reservationId) {
        Reservation pendingReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 예약입니다."));

        if (pendingReservation.getReservationStatus() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("[ERROR] 대기 상태의 예약만 거절할 수 있습니다.");
        }

        pendingReservation.denyAndChangeNextReservationToNotPaid();
    }

    @Transactional
    public void remove(Long reservationId) {
        Reservation targetReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 예약입니다."));

        targetReservation.denyAndChangeNextReservationToNotPaid();
        paymentHelper.deleteByReservationIdIfExist(reservationId);
        reservationRepository.deleteById(targetReservation.getId());
    }

    @Transactional
    public void remove(Long reservationId, Long memberId) throws AuthenticationException {
        Reservation targetReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 예약입니다."));
        Member member = memberHelper.getById(memberId);

        if (!targetReservation.isCreatedBy(member)) {
            throw new AuthenticationException("[ERROR] 해당 예약을 제거할 권한이 없습니다.]");
        }

        targetReservation.denyAndChangeNextReservationToNotPaid();
        paymentHelper.deleteByReservationIdIfExist(reservationId);
        reservationRepository.deleteById(targetReservation.getId());
    }
}
