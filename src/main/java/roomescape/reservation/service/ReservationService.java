package roomescape.reservation.service;

import static roomescape.exception.type.RoomescapeExceptionType.DUPLICATE_RESERVATION;
import static roomescape.exception.type.RoomescapeExceptionType.DUPLICATE_WAITING_RESERVATION;
import static roomescape.exception.type.RoomescapeExceptionType.NOT_FOUND_MEMBER_BY_ID;
import static roomescape.exception.type.RoomescapeExceptionType.NOT_FOUND_RESERVATION_TIME;
import static roomescape.exception.type.RoomescapeExceptionType.NOT_FOUND_THEME;
import static roomescape.exception.type.RoomescapeExceptionType.PAST_TIME_RESERVATION;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.admin.dto.AdminReservationDetailResponse;
import roomescape.admin.dto.AdminReservationRequest;
import roomescape.exception.RoomescapeException;
import roomescape.member.domain.LoginMember;
import roomescape.member.entity.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.PaymentInfo;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.Reservations;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.dto.ReservationDetailResponse;
import roomescape.reservation.dto.ReservationPaymentDetail;
import roomescape.reservation.dto.ReservationPaymentResponse;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.entity.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.entity.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.entity.ReservationTime;
import roomescape.time.repository.ReservationTimeRepository;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository,
                              MemberRepository memberRepository,
                              PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public ReservationPaymentResponse saveReservationPayment(
            LoginMember loginMember,
            ReservationRequest reservationRequest,
            PaymentInfo paymentInfo
    ) {
        Reservation reservation = getReservation(loginMember.getId(), reservationRequest, ReservationStatus.BOOKED);

        Reservations reservations = new Reservations(reservationRepository.findAll());
        if (reservations.hasSameReservation(reservation)) {
            throw new RoomescapeException(
                    DUPLICATE_RESERVATION,
                    reservationRequest.date(),
                    reservationRequest.themeId(),
                    reservationRequest.timeId());
        }
        Reservation savedReservation = reservationRepository.save(reservation);
        paymentRepository.save(new Payment(savedReservation, paymentInfo));

        return new ReservationPaymentResponse(
                ReservationResponse.from(reservation),
                PaymentResponse.from(paymentInfo)
        );
    }

    @Transactional
    public ReservationResponse saveWaiting(LoginMember loginMember, ReservationRequest reservationRequest) {

        Reservation reservation = getReservation(loginMember.getId(), reservationRequest, ReservationStatus.WAITING);
        Reservations reservations = new Reservations(reservationRepository.findAllByMemberId(loginMember.getId()));
        if (reservations.hasSameReservation(reservation)) {
            throw new RoomescapeException(
                    DUPLICATE_WAITING_RESERVATION,
                    loginMember.getId(),
                    reservationRequest.date(),
                    reservationRequest.themeId(),
                    reservationRequest.timeId());
        }
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse saveByAdmin(AdminReservationRequest reservationRequest) {

        Reservation beforeSaveReservation = getReservation(
                reservationRequest.memberId(),
                new ReservationRequest(
                        reservationRequest.date(),
                        reservationRequest.timeId(),
                        reservationRequest.themeId()),
                ReservationStatus.BOOKED);

        Reservations reservations = new Reservations(reservationRepository.findAll());
        if (reservations.hasSameReservation(beforeSaveReservation)) {
            throw new RoomescapeException(
                    DUPLICATE_RESERVATION,
                    reservationRequest.date(),
                    reservationRequest.themeId(),
                    reservationRequest.timeId());
        }

        return ReservationResponse.from(reservationRepository.save(beforeSaveReservation));
    }


    private Reservation getReservation(long memberId, ReservationRequest reservationRequest, ReservationStatus reservationStatus) {
        ReservationTime requestedTime = reservationTimeRepository.findById(reservationRequest.timeId())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_RESERVATION_TIME, reservationRequest.timeId()));
        Theme requestedTheme = themeRepository.findById(reservationRequest.themeId())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_THEME, reservationRequest.themeId()));
        Member requestedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_MEMBER_BY_ID, memberId));

        Reservation reservation = reservationRequest.toReservation(
                requestedMember,
                requestedTime,
                requestedTheme,
                reservationStatus);

        if (reservation.isBefore(LocalDateTime.now())) {
            throw new RoomescapeException(PAST_TIME_RESERVATION, reservation.getReservationTime().getStartAt());
        }
        return reservation;
    }

    public List<ReservationResponse> findAllReservations() {
        Reservations reservations = new Reservations(reservationRepository.findAllByStatus(ReservationStatus.BOOKED));
        return reservations.getReservations().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> searchReservation(Long themeId, Long memberId, LocalDate dateFrom, LocalDate dateTo) {
        Reservations reservations = new Reservations(reservationRepository.findByThemeIdAndMemberIdAndDateBetween(themeId, memberId, dateFrom, dateTo));
        return reservations.booked().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public List<ReservationPaymentDetail> findAllByMemberId(long memberId) {
        Reservations reservations = new Reservations(reservationRepository.findAllByMemberId(memberId));
        List<Waiting> waitings = reservations.waiting().stream()
                .map(this::createWaiting)
                .filter(waiting -> !waiting.isOver())
                .toList();
        return getReservationPaymentDetailsBy(ReservationDetailResponse.of(reservations, waitings));
    }

    private List<ReservationPaymentDetail> getReservationPaymentDetailsBy(List<ReservationDetailResponse> reservationDetails) {
        List<ReservationPaymentDetail> paymentDetails = new ArrayList<>();
        for (ReservationDetailResponse response : reservationDetails) {
            paymentRepository.findByReservationId(response.reservationId())
                    .ifPresentOrElse(payment -> paymentDetails.add(new ReservationPaymentDetail(response, PaymentResponse.from(payment))),
                            () -> paymentDetails.add(new ReservationPaymentDetail(response, PaymentResponse.nothing())));
        }
        return paymentDetails;
    }

    private Waiting createWaiting(Reservation reservation) {
        return new Waiting(
                reservation,
                reservationRepository.findAndCountWaitingNumber(
                        reservation.getDate(),
                        reservation.getReservationTime(),
                        reservation.getTheme(),
                        reservation.getCreatedAt()));
    }

    @Transactional
    public List<AdminReservationDetailResponse> findAllWaitingReservations() {
        List<Reservation> reservations = reservationRepository.findAllByStatus(ReservationStatus.WAITING);
        return reservations.stream()
                .map(this::createWaiting)
                .filter(waiting -> !waiting.isOver())
                .map(AdminReservationDetailResponse::from)
                .toList();
    }

    @Transactional
    public void cancelReservationPayment(long reservationId, long paymentId) {
        paymentRepository.deleteById(paymentId);
        reservationRepository.deleteById(reservationId);
    }

    @Transactional
    public void deleteById(long reservationId) {
        reservationRepository.deleteById(reservationId);
    }

    @Transactional
    public void deleteByMemberIdAndId(LoginMember loginMember, long id) {
        reservationRepository.deleteByMemberIdAndId(loginMember.getId(), id);
    }
}
