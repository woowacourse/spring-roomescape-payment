package roomescape.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.*;
import roomescape.dto.request.AdminReservationRequest;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.dto.request.ReservationWithPaymentRequest;
import roomescape.dto.response.ReservationDetailResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.exception.PaymentException;
import roomescape.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static roomescape.exception.ExceptionType.*;

@Service
public class ReservationService {
    private static final int NOT_WAITING_INDEX = 1;
    private static final long ADMIN_ID = 1;

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService;
    private final Logger logger = LogManager.getLogger(ReservationService.class);

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository,
                              MemberRepository memberRepository, PaymentService paymentService) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public ReservationResponse saveByUser(LoginMemberRequest loginMemberRequest,
                                          ReservationWithPaymentRequest reservationWithPaymentRequest) {

        Reservation requestedReservation = makeReservationByRequest(
                reservationWithPaymentRequest.date(),
                reservationWithPaymentRequest.timeId(),
                reservationWithPaymentRequest.themeId(),
                loginMemberRequest.id()
        );

        Payment payment = paymentService.pay(reservationWithPaymentRequest.toPayment());

        ReservationResponse response;
        try {
            Reservation reservation = new Reservation(
                    requestedReservation.getDate(),
                    requestedReservation.getReservationTime(),
                    requestedReservation.getTheme(),
                    requestedReservation.getMember(),
                    payment
            );

            Reservation savedReservation = reservationRepository.save(reservation);
            response = ReservationResponse.from(savedReservation);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            paymentService.cancel(new CancelPayment(payment, new CancelReason("서버 오류")));
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "예약에 실패했습니다. 다시 시도해주세요.");
        }
        return response;
    }

    public ReservationResponse saveByAdmin(AdminReservationRequest reservationRequest) {
        Reservation requestedReservation = makeReservationByRequest(
                reservationRequest.date(),
                reservationRequest.timeId(),
                reservationRequest.themeId(),
                reservationRequest.memberId()
        );
        Reservation savedReservation = reservationRepository.save(requestedReservation);

        return ReservationResponse.from(
                savedReservation
        );
    }

    private Reservation makeReservationByRequest(LocalDate date, long timeId, long themeId, long memberId) {
        ReservationTime requestedTime = reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_RESERVATION_TIME));
        Theme requestedTheme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_THEME));
        Member requestedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_MEMBER));

        Reservation beforeSaveReservation = new Reservation(
                date,
                requestedTime,
                requestedTheme,
                requestedMember);
        if (beforeSaveReservation.isBefore(LocalDateTime.now())) {
            throw new RoomescapeException(PAST_TIME_RESERVATION);
        }
        return beforeSaveReservation;
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationDetailResponse> findAllByMemberId(long userId) {
        List<Reservation> reservationsByMember = reservationRepository.findAllByMemberId(userId);
        return reservationsByMember.stream()
                .map(reservation -> ReservationDetailResponse.from(
                        reservation,
                        reservationRepository.calculateIndexOf(reservation)))
                .toList();
    }

    public List<ReservationResponse> searchReservation(Long themeId, Long memberId, LocalDate dateFrom,
                                                       LocalDate dateTo) {
        return findReservationsBy(themeId, memberId, dateFrom, dateTo).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    private List<Reservation> findReservationsBy(Long themeId, Long memberId, LocalDate dateFrom,
                                                 LocalDate dateTo) {
        if (themeId != null && memberId != null) {
            return reservationRepository.findByThemeIdAndMemberIdAndDateBetween(themeId, memberId, dateFrom, dateTo);
        }
        if (themeId != null) {
            return reservationRepository.findByThemeIdAndDateBetween(themeId, dateFrom, dateTo);
        }
        if (memberId != null) {
            return reservationRepository.findByMemberIdAndDateBetween(memberId, dateFrom, dateTo);
        }
        return reservationRepository.findByDateBetween(dateFrom, dateTo);
    }

    @Transactional
    public void delete(LoginMemberRequest loginMemberRequest, long reservationId) {
        Optional<Reservation> findResult = reservationRepository.findById(reservationId);
        if (findResult.isEmpty()) {
            return;
        }

        Reservation requestedReservation = findResult.get();
        if (isNotDeletableMember(loginMemberRequest, requestedReservation.getMember())) {
            throw new RoomescapeException(FORBIDDEN_DELETE);
        }
        reservationRepository.delete(requestedReservation);

        if (isPaidReservation(requestedReservation)) {
            paymentService.cancel(new CancelPayment(requestedReservation.getPayment(), new CancelReason("예약 취소")));
        }
    }

    private static boolean isNotDeletableMember(LoginMemberRequest loginMember, Member requestReservationMember) {
        return loginMember.id() != requestReservationMember.getId() && loginMember.id() != ADMIN_ID;
    }

    @Transactional
    public void deleteWaitingByAdmin(long id) {
        Optional<Reservation> findResult = reservationRepository.findById(id);
        if (findResult.isEmpty()) {
            return;
        }

        Reservation requestedReservation = findResult.get();
        if (isNotWaiting(requestedReservation)) {
            throw new RoomescapeException(FORBIDDEN_DELETE);
        }
        reservationRepository.deleteById(id);

        if (isPaidReservation(requestedReservation)) {
            paymentService.cancel(new CancelPayment(requestedReservation.getPayment(), new CancelReason("관리자의 대기 취소")));
        }
    }

    private static boolean isPaidReservation(Reservation requestedReservation) {
        return requestedReservation.getPayment() != null;
    }

    private boolean isNotWaiting(Reservation requestedReservation) {
        return reservationRepository.calculateIndexOf(requestedReservation) == NOT_WAITING_INDEX;
    }

    public List<ReservationResponse> findAllRemainedWaiting() {
        return reservationRepository.findAllRemainedWaiting(LocalDateTime.now()).stream()
                .map(ReservationResponse::from)
                .toList();
    }
}
