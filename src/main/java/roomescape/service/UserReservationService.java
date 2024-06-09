package roomescape.service;

import static roomescape.domain.reservation.ReservationStatus.PAYMENT_RESERVED;
import static roomescape.domain.reservation.ReservationStatus.STANDBY;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.controller.dto.request.CreateReservationRequest;
import roomescape.controller.dto.response.CreateReservationResponse;
import roomescape.controller.dto.request.CreateUserReservationStandbyRequest;
import roomescape.controller.dto.response.FindMyReservationResponse;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.payment.Payment;
import roomescape.domain.theme.Theme;
import roomescape.global.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.repository.dto.ReservationWithRank;

@Service
public class UserReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;

    public UserReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository, MemberRepository memberRepository,
            PaymentRepository paymentRepository) {

        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public CreateReservationResponse reserve(CreateReservationRequest request, Long paymentId) {
        validateDuplication(request.date(), request.timeId(), request.themeId());
        return savePaymentReserved(
                request.memberId(), request.date(), request.timeId(), request.themeId(), paymentId);
    }

    private void validateDuplication(LocalDate date, Long timeId, Long themeId) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(date, timeId, themeId)) {
            throw new RoomescapeException("해당 시간에 예약이 이미 존재합니다.");
        }
    }

    @Transactional
    public CreateReservationResponse standby(Long memberId, CreateUserReservationStandbyRequest request) {
        validateAlreadyBookedByMember(memberId, request.date(), request.timeId(), request.themeId());
        return saveStandBy(memberId, request.date(), request.timeId(), request.themeId());
    }

    private void validateAlreadyBookedByMember(Long memberId, LocalDate date, Long timeId, Long themeId) {
        if (reservationRepository.existsByMemberIdAndDateAndTimeIdAndThemeId(
                memberId, date, timeId, themeId)) {
            throw new RoomescapeException("이미 예약 혹은 대기가 있습니다.");
        }
    }

    private CreateReservationResponse savePaymentReserved(Long memberId, LocalDate date, Long timeId,
                                                          Long themeId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RoomescapeException("결제되지 않았습니다."));

        return save(memberId, date, timeId, themeId, payment, PAYMENT_RESERVED);
    }

    private CreateReservationResponse saveStandBy(Long memberId, LocalDate date, Long timeId, Long themeId) {
        return save(memberId, date, timeId, themeId, null, STANDBY);
    }

    private CreateReservationResponse save(Long memberId, LocalDate date, Long timeId, Long themeId,
                                           Payment payment, ReservationStatus status) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RoomescapeException("입력한 사용자 ID에 해당하는 데이터가 존재하지 않습니다."));
        ReservationTime time = reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new RoomescapeException("입력한 시간 ID에 해당하는 데이터가 존재하지 않습니다."));
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RoomescapeException("입력한 테마 ID에 해당하는 데이터가 존재하지 않습니다."));
        LocalDateTime createdAt = LocalDateTime.now();

        Reservation reservation = new Reservation(member, date, createdAt, time, theme, payment, status);
        validatePastReservation(date, time);

        return CreateReservationResponse.from(reservationRepository.save(reservation));
    }

    private void validatePastReservation(LocalDate date, ReservationTime time) {
        if (date.isBefore(LocalDate.now())) {
            throw new RoomescapeException("과거 예약을 추가할 수 없습니다.");
        }
        if (date.isEqual(LocalDate.now()) && time.isBeforeNow()) {
            throw new RoomescapeException("과거 예약을 추가할 수 없습니다.");
        }
    }

    @Transactional
    public void deleteStandby(Long id, Member member) {
        Reservation reservation = reservationRepository.findByIdAndStatus(id, STANDBY)
                .orElseThrow(() -> new RoomescapeException("예약대기가 존재하지 않아 삭제할 수 없습니다."));

        if (member.isNotAdmin() && reservation.isNotReservedBy(member)) {
            throw new RoomescapeException("자신의 예약만 삭제할 수 있습니다.");
        }

        reservationRepository.deleteById(reservation.getId());
        approveNextWaiting(reservation);
    }

    private void approveNextWaiting(Reservation reservation) {
        reservationRepository.findFirstByDateAndTimeIdAndThemeIdOrderByCreatedAtAsc(
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId()
        ).ifPresent(Reservation::reserve);
    }

    @Transactional(readOnly = true)
    public List<FindMyReservationResponse> findMyReservationsWithRank(Long memberId) {
        List<ReservationWithRank> reservations =
                reservationRepository.findReservationsWithRankByMemberId(memberId);

        return reservations.stream()
                .map(data -> FindMyReservationResponse.from(data.reservation(), data.rank()))
                .toList();
    }
}
