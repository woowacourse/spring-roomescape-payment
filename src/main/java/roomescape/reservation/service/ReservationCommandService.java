package roomescape.reservation.service;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.client.dto.PaymentsConfirmRequest;
import roomescape.global.auth.LoginMember;
import roomescape.global.exception.custom.BadRequestException;
import roomescape.global.exception.custom.NotFoundException;
import roomescape.global.exception.custom.UnauthorizedException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.CreateReservationWithMemberRequest;
import roomescape.reservation.dto.CreateReservationWithPaymentRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.ReservationTimeRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.repository.WaitingRepository;

@Service
@Slf4j
public class ReservationCommandService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentService paymentService;

    public ReservationCommandService(ReservationRepository reservationRepository,
                                     ReservationTimeRepository reservationTimeRepository,
                                     ThemeRepository themeRepository,
                                     MemberRepository memberRepository, WaitingRepository waitingRepository,
                                     PaymentService paymentService) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.waitingRepository = waitingRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public ReservationResponse createMyReservationWithPayments(final CreateReservationWithPaymentRequest request,
                                                               final LoginMember loginMember) {
        log.info("결제 예약 생성 요청 - memberId: {}, themeId: {}, date: {}, timeId: {}", 
                loginMember.id(), request.themeId(), request.date(), request.timeId());
        final Member member = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new UnauthorizedException("예약자를 찾을 수 없습니다."));
        final Payment payment = paymentService.confirmAndSavePayment(new PaymentsConfirmRequest(request));
        final ReservationResponse response = createReservation(request.themeId(), request.timeId(), request.date(), member, payment);
        log.info("결제 예약 생성 완료 - reservationId: {}, memberId: {}", response.id(), member.getId());
        return response;
    }

    public ReservationResponse createReservationByAdmin(final CreateReservationWithMemberRequest request) {
        log.info("관리자 예약 생성 요청 - memberId: {}, themeId: {}, date: {}, timeId: {}", 
                request.memberId(), request.themeId(), request.date(), request.timeId());
        final Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new BadRequestException("예약자를 찾을 수 없습니다."));
        final ReservationResponse response = createReservation(request.themeId(), request.timeId(), request.date(), member, null);
        log.info("관리자 예약 생성 완료 - reservationId: {}, memberId: {}", response.id(), member.getId());
        return response;
    }

    public void cancelReservationById(final long id) {
        log.info("예약 취소 요청 - reservationId: {}", id);
        waitingRepository.findFirstByReservationIdOrderByCreatedAtAsc(id)
                .ifPresentOrElse(
                        (waiting) -> {
                            processWaitingToReservation(id, waiting);
                            log.info("예약 취소 및 대기자 예약 처리 완료 - reservationId: {}, waitingId: {}", id, waiting.getId());
                        },
                        () -> {
                            reservationRepository.deleteById(id);
                            log.info("예약 취소 완료 - reservationId: {}", id);
                        }
                );
    }

    private ReservationResponse createReservation(
            final long themeId,
            final long timeId,
            final LocalDate date,
            final Member member,
            final Payment payment) {
        final Reservation reservation = convertToReservation(themeId, timeId, date, member, payment);
        final Reservation savedReservation = reservationRepository.save(reservation);
        return new ReservationResponse(savedReservation);
    }

    private Reservation convertToReservation(
            final long themeId,
            final long timeId,
            final LocalDate date,
            final Member member,
            final Payment payment) {
        final Theme theme = validateAndGetTheme(themeId);
        final ReservationTime time = validateAndGetTime(timeId);
        validateDuplicateReservation(date, time.getId(), theme.getId());
        return Reservation.register(member, date, time, theme, payment);
    }

    private void processWaitingToReservation(final long id, final Waiting waiting) {
        final Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다."));
        reservation.updateMember(waiting.getMember());
        waitingRepository.delete(waiting);
    }

    private Theme validateAndGetTheme(final long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new BadRequestException("테마가 존재하지 않습니다."));
    }

    private ReservationTime validateAndGetTime(final long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new BadRequestException("예약 시간이 존재하지 않습니다."));
    }

    private void validateDuplicateReservation(final LocalDate date, final long timeId, final long themeId) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(date, timeId, themeId)) {
            throw new BadRequestException("해당 시간에 이미 예약이 존재합니다.");
        }
    }
}
