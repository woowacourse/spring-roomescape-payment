package roomescape.reservation.application;

import static roomescape.reservation.domain.ReservationStatus.BOOKED;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.auth.AuthorizationException;
import roomescape.exception.resource.AlreadyExistException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.payment.domain.PaymentDomainService;
import roomescape.payment.domain.PaymentInfo;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationSlot;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.ui.dto.request.AvailableReservationTimeRequest;
import roomescape.reservation.ui.dto.request.CreateBookedReservationWithPaymentRequest;
import roomescape.reservation.ui.dto.response.AvailableReservationTimeResponse;
import roomescape.reservation.ui.dto.response.ReservationResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentDomainService paymentDomainService;

    @Transactional
    public ReservationResponse create(
            final CreateBookedReservationWithPaymentRequest request,
            final Long memberId
    ) {
        log.info("예약 생성 요청 - memberId: {}, themeId: {}, timeId: {}, date: {}, paymentKey: {}",
                memberId, request.themeId(), request.timeId(), request.date(), request.paymentKey());

        final ReservationTime time = getReservationTime(request.date(), request.timeId());
        final Theme theme = themeRepository.getById(request.themeId());
        final Member member = memberRepository.getById(memberId);

        Reservation reservation = createReservedReservation(request.date(), time, theme, member);

        log.info("결제 승인 시도 - orderId: {}, paymentKey: {}", request.orderId(), request.paymentKey());

        paymentDomainService.approvePayment(
                new PaymentInfo(
                        request.paymentKey(),
                        request.orderId(),
                        request.amount(),
                        reservation
                )
        );

        log.info("예약 생성 및 결제 완료 - reservationId: {}", reservation.getId());

        return ReservationResponse.from(reservation);
    }

    private Reservation createReservedReservation(
            final LocalDate date,
            final ReservationTime time,
            final Theme theme,
            final Member member
    ) {
        final ReservationSlot reservationSlot = ReservationSlot.of(date, time, theme);

        if (reservationRepository.existsByReservationSlot(reservationSlot)) {
            log.warn("예약 중복 - themeId: {}, date: {}, time: {}", theme.getId(), date, time.getStartAt());
            throw new AlreadyExistException("해당 예약 슬롯에 예약이 있습니다.");
        }

        final Reservation reservation = Reservation.of(reservationSlot, member, BOOKED);

        log.info("예약 객체 생성 완료 - memberId: {}, themeId: {}, date: {}, time: {}",
                member.getId(), theme.getId(), date, time.getStartAt());

        return reservationRepository.save(reservation);
    }

    private ReservationTime getReservationTime(final LocalDate date, final Long timeId) {
        final ReservationTime reservationTime = reservationTimeRepository.getById(timeId);
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime reservationDateTime = LocalDateTime.of(date, reservationTime.getStartAt());

        if (reservationDateTime.isBefore(now)) {
            log.warn("예약 시간 검증 실패 - 요청 시각: {}, 현재 시각: {}", reservationDateTime, now);
            throw new IllegalArgumentException("예약 시간은 현재 시간보다 이후여야 합니다.");
        }

        return reservationTime;
    }

    @Transactional
    public void deleteIfOwner(final Long reservationId, final Long memberId) {
        log.info("예약 삭제 요청 - reservationId: {}, 요청자 memberId: {}", reservationId, memberId);

        final Reservation reservation = reservationRepository.getById(reservationId);
        final Member member = memberRepository.getById(memberId);

        PaymentInfo paymentInfo = reservation.getPaymentInfo();
        if (paymentInfo != null) {
            log.info("예약 결제 연동 해제 - paymentKey: {}", paymentInfo.getPaymentKey());
            reservation.getPaymentInfo().disconnectReservation();
        }

        if (!Objects.equals(reservation.getMember(), member)) {
            log.warn("예약 삭제 권한 없음 - 예약자 ID: {}, 요청자 ID: {}", reservation.getMember().getId(), memberId);
            throw new AuthorizationException("본인이 아니면 삭제할 수 없습니다.");
        }

        reservationRepository.deleteById(reservationId);

        log.info("예약 삭제 완료 - reservationId: {}", reservationId);
    }

    @Transactional(readOnly = true)
    public List<AvailableReservationTimeResponse> findAvailableReservationTimes(
            final AvailableReservationTimeRequest request
    ) {
        log.info("예약 가능 시간 조회 요청 - themeId: {}, date: {}", request.themeId(), request.date());

        final List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();
        final Theme theme = themeRepository.getById(request.themeId());

        final List<LocalTime> bookedTimes = reservationRepository.findAllByDateAndTheme(
                        request.date(),
                        theme
                ).stream()
                .map(reservation -> reservation.getReservationSlot().getTime().getStartAt())
                .toList();

        log.info("예약 가능 시간 조회 완료 - 전체 시간 수: {}, 예약된 시간 수: {}",
                reservationTimes.size(), bookedTimes.size());

        return reservationTimes.stream()
                .map(reservationTime ->
                        new AvailableReservationTimeResponse(
                                reservationTime.getId(),
                                reservationTime.getStartAt(),
                                bookedTimes.contains(reservationTime.getStartAt())
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse.ForMember> findReservationsByMemberId(final Long memberId) {
        log.info("사용자 예약 목록 조회 요청 - memberId: {}", memberId);

        final Member member = memberRepository.getById(memberId);

        final List<ReservationResponse.ForMember> reservations = reservationRepository.findAllByMember(member)
                .stream()
                .map(ReservationResponse.ForMember::from)
                .toList();

        log.info("예약 목록 조회 완료 - 개수: {}", reservations.size());

        return reservations;
    }
}
