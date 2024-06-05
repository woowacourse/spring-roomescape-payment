package roomescape.service;

import static roomescape.domain.reservation.ReservationStatus.RESERVED;
import static roomescape.domain.reservation.ReservationStatus.STANDBY;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.dto.CreateReservationResponse;
import roomescape.controller.dto.CreateUserReservationRequest;
import roomescape.controller.dto.FindMyReservationResponse;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.global.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentInfoRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.repository.dto.MyReservationDto;
import roomescape.service.dto.TossPaymentResponseDto;

@Service
public class UserReservationService {

    private final TossPaymentService tossPaymentService;
    private final PaymentInfoService paymentInfoService;

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final PaymentInfoRepository paymentInfoRepository;

    public UserReservationService(
        TossPaymentService tossPaymentService,
        PaymentInfoService paymentInfoService,
        ReservationRepository reservationRepository,
        ReservationTimeRepository reservationTimeRepository,
        ThemeRepository themeRepository,
        MemberRepository memberRepository,
        PaymentInfoRepository paymentInfoRepository
    ) {
        this.tossPaymentService = tossPaymentService;
        this.paymentInfoService = paymentInfoService;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.paymentInfoRepository = paymentInfoRepository;
    }

    @Transactional
    public CreateReservationResponse reserve(Long memberId, CreateUserReservationRequest request) {
        validateDuplication(request.date(), request.timeId(), request.themeId());
        TossPaymentResponseDto paymentInfo = tossPaymentService.pay(
            request.orderId(), request.amount(), request.paymentKey());

        Reservation reservation = save(memberId, request.date(), request.timeId(), request.themeId(), RESERVED);
        paymentInfoService.save(paymentInfo, reservation);

        return CreateReservationResponse.from(reservation);
    }

    @Transactional
    public CreateReservationResponse standby(Long memberId, LocalDate date, Long timeId, Long themeId) {
        validateAlreadyBookedByMember(memberId, date, timeId, themeId);
        Reservation reservation = save(memberId, date, timeId, themeId, STANDBY);
        return CreateReservationResponse.from(reservation);
    }

    private Reservation save(Long memberId, LocalDate date, Long timeId, Long themeId, ReservationStatus status) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RoomescapeException("입력한 사용자 ID에 해당하는 데이터가 존재하지 않습니다."));
        ReservationTime time = reservationTimeRepository.findById(timeId)
            .orElseThrow(() -> new RoomescapeException("입력한 시간 ID에 해당하는 데이터가 존재하지 않습니다."));
        Theme theme = themeRepository.findById(themeId)
            .orElseThrow(() -> new RoomescapeException("입력한 테마 ID에 해당하는 데이터가 존재하지 않습니다."));

        LocalDateTime createdAt = LocalDateTime.now();
        validatePastReservation(date, time);

        return reservationRepository.save(new Reservation(member, date, createdAt, time, theme, status));
    }

    private void validateDuplication(LocalDate date, Long timeId, Long themeId) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(date, timeId, themeId)) {
            throw new RoomescapeException("해당 시간에 예약이 이미 존재합니다.");
        }
    }

    private void validateAlreadyBookedByMember(Long memberId, LocalDate date, Long timeId, Long themeId) {
        if (reservationRepository.existsByMemberIdAndDateAndTimeIdAndThemeIdAndStatus(
            memberId, date, timeId, themeId, RESERVED)) {
            throw new RoomescapeException("이미 예약하셨습니다. 대기 없이 이용 가능합니다.");
        }

        if (reservationRepository.existsByMemberIdAndDateAndTimeIdAndThemeIdAndStatus(
            memberId, date, timeId, themeId, STANDBY)) {
            throw new RoomescapeException("이미 대기중인 예약입니다.");
        }
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
    }

    @Transactional(readOnly = true)
    public List<FindMyReservationResponse> findMyReservationsWithRank(Long memberId) {
        List<MyReservationDto> data = reservationRepository.findReservationsWithRankByMemberId(memberId);

        return data.stream()
            .map(d -> FindMyReservationResponse.from(d.reservation(), d.rank(), d.paymentInfo()))
            .toList();
    }

    @Transactional
    public CreateReservationResponse updateStatusToReserved(Long id, Member member) {
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new RoomescapeException("예약이 존재하지 않습니다."));
        if (reservation.isReserved()) {
            throw new RoomescapeException("이미 결제된 예약입니다.");
        }
        if (reservation.isNotReservedBy(member)) {
            throw new RoomescapeException("자신의 예약만 결제할 수 있습니다.");
        }
        if (reservationRepository.countByTimeAndThemeAndDateAndCreatedAtBefore(
            reservation.getTime(),
            reservation.getTheme(),
            reservation.getDate(),
            reservation.getCreatedAt()) > 0
        ) {
            throw new RoomescapeException("결제대기 상태에서만 결제할 수 있습니다.");
        }

        reservation.reserve();
        return CreateReservationResponse.from(reservation);
    }
}
