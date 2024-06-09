package roomescape.reservation.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.model.Member;
import roomescape.member.service.MemberService;
import roomescape.payment.dto.PaymentHistoryDto;
import roomescape.payment.model.PaymentHistory;
import roomescape.payment.repository.PaymentHistoryRepository;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.ReservationDto;
import roomescape.reservation.dto.SaveAdminReservationRequest;
import roomescape.reservation.dto.SaveReservationRequest;
import roomescape.reservation.dto.SearchReservationsParams;
import roomescape.reservation.dto.SearchReservationsRequest;
import roomescape.reservation.model.Reservation;
import roomescape.reservation.model.ReservationDate;
import roomescape.reservation.model.ReservationTime;
import roomescape.reservation.model.ReservationWaiting;
import roomescape.reservation.model.Theme;
import roomescape.reservation.repository.CustomReservationRepository;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationWaitingRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final CustomReservationRepository customReservationRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final MemberService memberService;
    private final ThemeService themeService;
    private final ReservationTimeService reservationTimeService;
    private final PaymentService paymentService;

    public ReservationService(
            final CustomReservationRepository customReservationRepository,
            final ReservationRepository reservationRepository,
            final ReservationWaitingRepository reservationWaitingRepository,
            PaymentHistoryRepository paymentHistoryRepository,
            final MemberService memberService,
            final ThemeService themeService,
            final ReservationTimeService reservationTimeService,
            final PaymentService paymentService
    ) {
        this.customReservationRepository = customReservationRepository;
        this.reservationRepository = reservationRepository;
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.memberService = memberService;
        this.themeService = themeService;
        this.reservationTimeService = reservationTimeService;
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

    public ReservationDto saveReservation(final SaveAdminReservationRequest request) {
        final ReservationTime reservationTime = reservationTimeService.getReservationTime(request.timeId());
        final Theme theme = themeService.getTheme(request.themeId());
        final Member member = memberService.getMember(request.memberId());
        final Reservation reservation = request.toReservation(reservationTime, theme, member);

        validateReservationDateAndTime(reservation.getDate(), reservationTime);
        validateReservationDuplicated(reservation);

        return ReservationDto.from(reservationRepository.save(reservation));
    }

    public ReservationDto saveReservation(final SaveReservationRequest request,
                                          final Long memberId) {
        final ReservationTime reservationTime = reservationTimeService.getReservationTime(request.timeId());
        final Theme theme = themeService.getTheme(request.themeId());
        final Member member = memberService.getMember(memberId);
        final Reservation reservation = request.toReservation(reservationTime, theme, member);

        validateReservationDateAndTime(reservation.getDate(), reservationTime);
        validateReservationDuplicated(reservation);

        final Reservation savedReservation = reservationRepository.save(reservation);
        paymentService.submitPayment(request.orderId(), request.amount(), request.paymentKey(), reservation, member);
        return ReservationDto.from(savedReservation);
    }

    private static void validateReservationDateAndTime(final ReservationDate date, final ReservationTime time) {
        final LocalDateTime reservationLocalDateTime = LocalDateTime.of(date.getValue(), time.getStartAt());
        if (reservationLocalDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("현재 날짜보다 이전 날짜를 예약할 수 없습니다.");
        }
    }

    private void validateReservationDuplicated(final Reservation reservation) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId())
        ) {
            throw new IllegalArgumentException("이미 해당 날짜/시간의 테마 예약이 있습니다.");
        }
    }

    @Transactional
    public void deleteReservation(final Long reservationId) {
        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 예약 정보입니다."));
        if (reservation.isPaid()) {
            final PaymentHistory paymentHistory = paymentHistoryRepository.findByReservation(reservation)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 결제 정보입니다."));
            paymentHistory.deleteReservation();
        }
        reservationRepository.deleteById(reservation.getId());
        reservationWaitingRepository.findTopByDateAndTimeAndThemeOrderByCreatedAtAsc(
                        reservation.getDate(),
                        reservation.getTime(),
                        reservation.getTheme())
                .ifPresent(this::saveReservationWithWaiting);
    }

    private void saveReservationWithWaiting(final ReservationWaiting reservationWaiting) {
        final Reservation reservation = reservationWaiting.makeReservation();
        reservationRepository.save(reservation);
        reservationWaitingRepository.delete(reservationWaiting);
    }

    public List<MyReservationResponse> getMyReservations(final Long memberId) {
        List<Reservation> allByMemberId = reservationRepository.findAllByMember_Id(memberId);
        List<PaymentHistory> paymentHistories = paymentHistoryRepository.findAllByMember_Id(memberId);
        return allByMemberId.stream()
                .map(reservation -> MyReservationResponse.from(
                        ReservationDto.from(reservation),
                        getPaymentHistoryDto(reservation, paymentHistories)
                ))
                .toList();
    }

    private PaymentHistoryDto getPaymentHistoryDto(Reservation reservation, List<PaymentHistory> paymentHistories) {
        Optional<PaymentHistory> optionalHistory = paymentHistories.stream()
                .filter(paymentHistory -> paymentHistory.hasSameReservation(reservation))
                .findFirst();
        return optionalHistory.map(PaymentHistoryDto::from)
                .orElseGet(PaymentHistoryDto::getDefault);
    }
}
