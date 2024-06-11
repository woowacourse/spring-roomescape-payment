package roomescape.domain.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.model.Member;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.domain.payment.model.PaymentHistory;
import roomescape.domain.payment.model.PaymentStatus;
import roomescape.domain.payment.repository.PaymentHistoryRepository;
import roomescape.domain.payment.service.PaymentService;
import roomescape.domain.reservation.dto.ReservationDto;
import roomescape.domain.reservation.dto.ReservationWithPaymentHistoryDto;
import roomescape.domain.reservation.dto.SaveAdminReservationRequest;
import roomescape.domain.reservation.dto.SavePaymentHistoryRequest;
import roomescape.domain.reservation.dto.SaveReservationRequest;
import roomescape.domain.reservation.dto.SearchReservationsParams;
import roomescape.domain.reservation.dto.SearchReservationsRequest;
import roomescape.domain.reservation.exception.InvalidReserveInputException;
import roomescape.domain.reservation.model.Reservation;
import roomescape.domain.reservation.model.ReservationDate;
import roomescape.domain.reservation.model.ReservationStatus;
import roomescape.domain.reservation.model.ReservationTime;
import roomescape.domain.reservation.model.Theme;
import roomescape.domain.reservation.repository.CustomReservationRepository;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.ReservationTimeRepository;
import roomescape.domain.reservation.repository.ThemeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final CustomReservationRepository customReservationRepository;
    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentService paymentService;

    public ReservationService(
            final CustomReservationRepository customReservationRepository,
            final ReservationRepository reservationRepository,
            final ReservationTimeRepository reservationTimeRepository,
            final MemberRepository memberRepository,
            final ThemeRepository themeRepository,
            final PaymentHistoryRepository paymentHistoryRepository,
            final PaymentService paymentService
    ) {
        this.customReservationRepository = customReservationRepository;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.paymentService = paymentService;
    }

    public List<ReservationDto> getReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(ReservationDto::from)
                .toList();
    }

    public List<ReservationDto> searchReservations(final SearchReservationsRequest request) {
        final SearchReservationsParams searchReservationsParams = new SearchReservationsParams(
                request.memberId(),
                request.themeId(),
                request.from(),
                request.to()
        );

        return customReservationRepository.searchReservations(searchReservationsParams)
                .stream()
                .map(ReservationDto::from)
                .toList();
    }

    @Transactional
    public ReservationDto saveReservation(final SaveAdminReservationRequest request) {
        final Reservation reservation = generateReservationModel(request.timeId(), request.themeId(), request.memberId(), request.date());

        validateReservationDateAndTime(reservation.getDate(), reservation.getTime());
        validateReservationDuplicated(reservation);

        final SavePaymentHistoryRequest savePaymentHistoryRequest = convertPaymentHistoryRequest(request, reservation);
        paymentService.savePaymentHistory(savePaymentHistoryRequest);

        return ReservationDto.from(reservationRepository.save(reservation));
    }

    private Reservation generateReservationModel(
            final Long timeId,
            final Long themeId,
            final Long memberId,
            final LocalDate date
    ) {
        final ReservationTime reservationTime = getReservationTime(timeId);
        final Theme theme = getTheme(themeId);
        final Member member = getMember(memberId);

        return new Reservation(
                ReservationStatus.RESERVATION,
                date,
                reservationTime,
                theme,
                member
        );
    }

    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 id의 회원이 존재하지 않습니다."));
    }

    private Theme getTheme(final Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new NoSuchElementException("해당 id의 테마가 존재하지 않습니다."));
    }

    private ReservationTime getReservationTime(final Long reservationId) {
        return reservationTimeRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("해당 id의 예약 시간이 존재하지 않습니다."));
    }

    private SavePaymentHistoryRequest convertPaymentHistoryRequest(final SaveAdminReservationRequest request, final Reservation reservation) {
        return new SavePaymentHistoryRequest(
                request.orderId(),
                PaymentStatus.DONE,
                request.orderName(),
                request.amount(),
                LocalDateTime.now(),
                request.paymentKey(),
                "관리자 직접 결제",
                reservation
        );
    }

    @Transactional
    public ReservationDto saveReservationWithPaymentConfirm(final SaveReservationRequest request) {
        final Reservation reservation = generateReservationModel(request.timeId(), request.themeId(), request.memberId(), request.date());

        validateReservationDateAndTime(reservation.getDate(), reservation.getTime());
        validateReservationDuplicated(reservation);

        final Reservation savedReservation = reservationRepository.save(reservation);
        paymentService.submitPayment(request.orderId(), request.amount(), request.paymentKey(), savedReservation);

        return ReservationDto.from(savedReservation);
    }

    private static void validateReservationDateAndTime(final ReservationDate date, final ReservationTime time) {
        final LocalDateTime reservationLocalDateTime = LocalDateTime.of(date.getValue(), time.getStartAt());
        if (reservationLocalDateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidReserveInputException("현재 날짜보다 이전 날짜를 예약할 수 없습니다.");
        }
    }

    private void validateReservationDuplicated(final Reservation reservation) {
        final boolean existReservation = reservationRepository.existsByDateAndTime_IdAndTheme_IdAndStatus(
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId(),
                ReservationStatus.RESERVATION
        );

        if (existReservation) {
            throw new InvalidReserveInputException("이미 해당 날짜/시간의 테마 예약이 있습니다.");
        }
    }

    @Transactional
    public void deleteReservation(final Long reservationId) {
        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 예약 정보입니다."));
        reservation.cancel();
        paymentService.cancelPayment(reservation);
    }

    public List<ReservationWithPaymentHistoryDto> getMyReservationsWithPaymentHistory(final Long memberId) {
        return reservationRepository.findAllByMember_Id(memberId).stream()
                .map(this::convertReservationToReservationWithPaymentHistory)
                .toList();
    }

    private ReservationWithPaymentHistoryDto convertReservationToReservationWithPaymentHistory(final Reservation reservation) {
        final PaymentHistory paymentHistory = paymentHistoryRepository.findByReservation(reservation)
                .orElseThrow(() -> new NoSuchElementException("해당 예약의 결제 정보가 존재하지 않습니다."));

        return ReservationWithPaymentHistoryDto.of(reservation, paymentHistory.getPaymentKey(), paymentHistory.getTotalAmount());
    }
}
