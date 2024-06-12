package roomescape.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.DuplicateSaveException;
import roomescape.global.exception.IllegalReservationDateException;
import roomescape.global.exception.NoSuchRecordException;
import roomescape.global.exception.PaymentFailException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.payment.TossPaymentClient;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentMatcher;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.dto.PaymentCancelRequest;
import roomescape.payment.dto.PaymentCancelResponse;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationPending;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.ReservationWaiting;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.WaitingRankCalculator;
import roomescape.reservation.dto.MemberReservationAddRequest;
import roomescape.reservation.dto.MemberReservationResponse;
import roomescape.reservation.dto.MemberReservationWithPaymentAddRequest;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeRepository;

@Service
public class ReservationService {

    private static final String DEFAULT_CANCEL_REASON = "단순 변심";

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final PaymentRepository paymentRepository;
    private final TossPaymentClient tossPaymentClient;

    public ReservationService(MemberRepository memberRepository,
                              ReservationRepository reservationRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository,
                              PaymentRepository paymentRepository,
                              TossPaymentClient tossPaymentClient) {
        this.memberRepository = memberRepository;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.paymentRepository = paymentRepository;
        this.tossPaymentClient = tossPaymentClient;
    }

    public List<ReservationResponse> findAllReservation() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public MemberReservationResponse findById(Long id) {
        Reservation foundReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NoSuchRecordException("해당하는 예약이 존재하지 않습니다 ID: " + id));
        return new MemberReservationResponse(foundReservation);
    }

    public List<ReservationResponse> findAllWaitingReservation(Status status) {
        return reservationRepository.findAllReservationByStatus(status).stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public List<ReservationResponse> findAllByMemberAndThemeAndPeriod(Long memberId, Long themeId, LocalDate dateFrom,
                                                                      LocalDate dateTo) {
        return reservationRepository.findByMemberIdAndThemeIdAndDateValueBetween(memberId, themeId,
                        dateFrom, dateTo).stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public List<MemberReservationResponse> findAllByMemberId(Long memberId) {
        List<MemberReservationResponse> memberReservationResponse = new ArrayList<>();

        findAllMembersReservedReservation(memberReservationResponse, memberId);
        findAllMembersPendingReservation(memberReservationResponse, memberId);
        findAllMembersWaitingReservation(memberReservationResponse, memberId);

        return memberReservationResponse;
    }

    private void findAllMembersReservedReservation(List<MemberReservationResponse> responses, Long memberId) {
        List<Reservation> reservations = reservationRepository.findAllReservedByMemberId(memberId);
        PaymentMatcher paymentMatcher = new PaymentMatcher(paymentRepository.findAllByMemberId(memberId));
        for (Reservation reservation : reservations) {
            Payment payment = paymentMatcher.getPaymentByReservationId(reservation.getId());
            responses.add(
                    new MemberReservationResponse(
                            reservation.getId(),
                            reservation.getTheme().getName(),
                            reservation.getDate(),
                            reservation.getTime().getStartAt(),
                            reservation.getStatus().getValue(),
                            payment.getPaymentKey(),
                            payment.getOrderId(),
                            payment.getAmount()
                    )
            );
        }
    }

    private void findAllMembersPendingReservation(List<MemberReservationResponse> responses, Long memberId) {
        List<ReservationPending> reservations = reservationRepository.findAllReservationPendingByMemberId(memberId);
        for (ReservationPending reservation : reservations) {
            responses.add(
                    new MemberReservationResponse(
                            reservation.getId(),
                            reservation.getTheme().getName(),
                            reservation.getDate(),
                            reservation.getTime().getStartAt(),
                            reservation.getStatus().getValue()
                    )
            );
        }
    }

    private void findAllMembersWaitingReservation(List<MemberReservationResponse> responses, Long memberId) {
        List<ReservationWaiting> reservationWaitings
                = reservationRepository.findAllReservationWaitingByMemberId(memberId);

        for (ReservationWaiting reservationWaiting : reservationWaitings) {
            WaitingRankCalculator waitingRankCalculator
                    = new WaitingRankCalculator(
                    reservationRepository.findAllReservationWaitingByDateAndTimeAndTheme(reservationWaiting.getDate(),
                            reservationWaiting.getTime().getId(),
                            reservationWaiting.getTheme().getId()
                    )
            );

            responses.add(
                    new MemberReservationResponse(
                            reservationWaiting.getId(),
                            reservationWaiting.getTheme().getName(),
                            reservationWaiting.getDate(),
                            reservationWaiting.getTime().getStartAt(),
                            reservationWaiting.getStatus().getValue(),
                            waitingRankCalculator.calculateWaitingRank(reservationWaiting)
                    )
            );
        }
    }

    public ReservationResponse saveAdminReservation(Long memberId, MemberReservationAddRequest request) {
        validateDuplicatedReservation(request);
        return saveMemberReservation(memberId, request, Status.RESERVED);
    }

    @Transactional
    public ReservationResponse saveMemberReservation(Long memberId, MemberReservationWithPaymentAddRequest request) {
        MemberReservationAddRequest memberReservationAddRequest = request.extractMemberReservationAddRequest();
        PaymentConfirmRequest paymentConfirmRequest = request.extractPaymentConfirmRequest();

        validateDuplicatedReservation(memberReservationAddRequest);

        PaymentConfirmResponse paymentConfirmResponse = tossPaymentClient.confirmPayments(paymentConfirmRequest);
        if (paymentConfirmResponse.isPaymentNotFinished()) {
            throw new PaymentFailException("결제가 완료되지 않았습니다.");
        }
        ReservationResponse reservationResponse
                = saveMemberReservation(memberId, memberReservationAddRequest, Status.RESERVED);
        saveMemberPayment(reservationResponse, paymentConfirmResponse);
        return reservationResponse;
    }

    public ReservationResponse saveMemberWaitingReservation(Long memberId, MemberReservationAddRequest request) {
        validateDuplicatedWaitingReservation(memberId, request);
        return saveMemberReservation(memberId, request, Status.WAITING);
    }

    private void validateDuplicatedReservation(MemberReservationAddRequest request) {
        if (reservationRepository.existsByDateValueAndTimeIdAndThemeId(request.date(), request.timeId(),
                request.themeId())) {
            throw new DuplicateSaveException("중복되는 예약이 존재합니다.");
        }
    }

    private void validateDuplicatedWaitingReservation(Long memberId, MemberReservationAddRequest request) {
        if (reservationRepository.existsByDateValueAndTimeIdAndThemeIdAndMemberId(request.date(), request.timeId(),
                request.themeId(), memberId)) {
            throw new DuplicateSaveException("이미 회원님이 대기하고 있는 예약이 존재합니다.");
        }
    }

    private ReservationResponse saveMemberReservation(Long memberId,
                                                      MemberReservationAddRequest request,
                                                      Status status) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchRecordException("ID: " + memberId + " 해당하는 회원을 찾을 수 없습니다"));
        ReservationTime reservationTime = getReservationTime(request.timeId());
        validateReservingPastTime(request.date(), reservationTime.getStartAt());
        Theme theme = getTheme(request.themeId());

        Reservation reservation
                = new Reservation(member, request.date(), reservationTime, theme, status, LocalDateTime.now());
        Reservation saved = reservationRepository.save(reservation);
        return new ReservationResponse(saved);
    }

    private void saveMemberPayment(ReservationResponse reservationResponse,
                                   PaymentConfirmResponse paymentConfirmResponse) {
        Payment payment = new Payment(reservationResponse.id(),
                reservationResponse.member().id(),
                paymentConfirmResponse.paymentKey(),
                paymentConfirmResponse.orderId(),
                paymentConfirmResponse.totalAmount());
        paymentRepository.save(payment);
    }

    private void validateReservingPastTime(LocalDate date, LocalTime time) {
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        if (date.isBefore(nowDate) || (date.isEqual(nowDate) && time.isBefore(nowTime))) {
            throw new IllegalReservationDateException(
                    nowDate + " " + nowTime + ": 예약 날짜와 시간은 현재 보다 이전일 수 없습니다");
        }
    }

    private ReservationTime getReservationTime(long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new NoSuchRecordException("해당하는 예약시간이 존재하지 않습니다 ID: " + timeId));
    }

    private Theme getTheme(long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new NoSuchRecordException("해당하는 테마가 존재하지 않습니다 ID: " + themeId));
    }

    @Transactional
    public void removeReservation(long id) {
        Reservation reservationForDelete = reservationRepository.findById(id)
                .orElseThrow(() -> new NoSuchRecordException("해당하는 예약이 존재하지 않습니다 ID: " + id));
        if (reservationForDelete.isReserved()) {
            cancelPaymentByReservation(id, reservationForDelete);
        }
        reservationRepository.deleteById(id);
    }

    private void cancelPaymentByReservation(Long id, Reservation reservationForDelete) {
        Payment paymentForCancel = paymentRepository.findByReservationId(id)
                .orElseThrow(() -> new NoSuchRecordException("해당하는 결제 정보가 존재하지 않습니다 ID: " + id));
        PaymentCancelRequest request = new PaymentCancelRequest(paymentForCancel.getPaymentKey());
        PaymentCancelResponse response = tossPaymentClient.cancelPayments(request, DEFAULT_CANCEL_REASON);
        if (response.isCancelNotFinished()) {
            throw new PaymentFailException("결제 취소가 완료되지 않았습니다.");
        }
        updateWaitingReservationStatus(reservationForDelete);
    }

    private void updateWaitingReservationStatus(Reservation reservationForDelete) {
        reservationRepository.findFirstByDateValueAndTimeIdAndThemeIdAndStatus(
                reservationForDelete.getDate(),
                reservationForDelete.getTime().getId(),
                reservationForDelete.getTheme().getId(),
                Status.WAITING
        ).ifPresent(value -> value.updateStatus(Status.PENDING));
    }

    @Transactional
    public MemberReservationResponse payForPendingReservation(Long memberId, ReservationPaymentRequest request) {
        Reservation reservationForPay = reservationRepository.findByIdAndMemberId(request.reservationId(), memberId)
                .orElseThrow(
                        () -> new NoSuchRecordException("해당하는 예약이 존재하지 않습니다 ID: " + request.reservationId())
                );

        PaymentConfirmRequest paymentConfirmRequest = request.extractPaymentConfirmRequest();
        PaymentConfirmResponse paymentConfirmResponse = tossPaymentClient.confirmPayments(paymentConfirmRequest);
        Payment payment = new Payment(
                request.reservationId(),
                memberId,
                paymentConfirmResponse.paymentKey(),
                paymentConfirmResponse.orderId(),
                paymentConfirmResponse.totalAmount()
        );

        paymentRepository.save(payment);
        reservationForPay.updateStatus(Status.RESERVED);
        return new MemberReservationResponse(
                reservationForPay.getId(),
                reservationForPay.getTheme().getName(),
                reservationForPay.getDate(),
                reservationForPay.getTime().getStartAt(),
                reservationForPay.getStatus().getValue(),
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getAmount()
        );
    }
}
