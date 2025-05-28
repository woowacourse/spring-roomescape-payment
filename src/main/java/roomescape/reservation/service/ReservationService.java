package roomescape.reservation.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.PaymentRestClient;
import roomescape.payment.RestClientConfig;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.MyPageReservationResponse;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.ReservationTheme;
import roomescape.theme.repository.ReservationThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.ReservationTimeRepository;
import roomescape.waiting.domain.ReservationWaiting;
import roomescape.waiting.repository.ReservationWaitingRepository;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationThemeRepository reservationThemeRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final EntityManager entityManager;
    private final RestClientConfig restClientConfig;

    @Transactional
    public ReservationResponse addReservation(final long memberId, final ReservationPaymentRequest request) {
        long timeId = request.timeId();
        final long themeId = request.themeId();
        final LocalDate date = request.date();
        validateDuplicateReservation(date, timeId, themeId);
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 사용자 입니다."));
        final ReservationTime time = reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 예약 시간 입니다."));
        final ReservationTheme theme = reservationThemeRepository.findById(themeId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 테마 입니다."));
        final Reservation reservation = new Reservation(member, date, time, theme);
        TossPaymentResponse paymentResponse = approvePayment(request);
        Reservation saved = reservationRepository.save(reservation);
        paymentRepository.save(new Payment(saved, paymentResponse.orderId(), paymentResponse.paymentKey(),
                paymentResponse.totalAmount(), paymentResponse.type()));
        return ReservationResponse.fromV2(saved);
    }

    @Transactional
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::fromV2)
                .toList();
    }

    @Transactional
    public List<ReservationResponse> getFilteredReservations(final Long memberId, final Long themeId,
                                                             final LocalDate dateFrom, final LocalDate dateTo) {
        final List<Reservation> reservations = reservationRepository.findByMemberIdAndThemeIdAndDateFromAndDateTo(
                memberId, themeId, dateFrom, dateTo);
        return reservations.stream()
                .map(ReservationResponse::fromV2)
                .toList();
    }

    @Transactional
    public List<MyPageReservationResponse> getReservationsByMemberId(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 사용자 입니다."));
        final List<Reservation> myReservations = reservationRepository.findByMemberId(member.getId());
        final List<ReservationWaiting> myReservationWaitings = reservationWaitingRepository.findByMemberId(
                member.getId());
        final List<MyPageReservationResponse> myPageReservationResponses = myReservations.stream()
                .map(MyPageReservationResponse::from)
                .collect(Collectors.toList());
        List<MyPageReservationResponse> myPageReservationWaitingResponses = myReservationWaitings.stream()
                .map(myReservationWaiting -> MyPageReservationResponse.of(myReservationWaiting,
                        getWaitingOrderByMember(myReservationWaiting.getMember())))
                .toList();
        myPageReservationResponses.addAll(myPageReservationWaitingResponses);
        return myPageReservationResponses;
    }

    @Transactional
    public void removeReservation(final long id) {
        validateExistsById(id);
        reservationRepository.findById(id).ifPresent(
                reservation -> {
                    reservationRepository.deleteById(id);
                    entityManager.flush();
                    convertWaitingToReservation(reservation);
                }
        );
    }

    private TossPaymentResponse approvePayment(final ReservationPaymentRequest request) {
        PaymentRestClient restClient = restClientConfig.getPaymentRestClient();
        return restClient.requestPaymentApprove(
                new TossPaymentRequest(request.orderId(), request.paymentKey(), request.amount()));
    }

    private void convertWaitingToReservation(final Reservation reservation) {
        reservationWaitingRepository.findFirstByThemeIdAndTimeIdAndDateOrderByCreatedAtAsc(
                        reservation.getTheme().getId(), reservation.getTime().getId(), reservation.getDate())
                .ifPresent(reservationWaiting -> {
                    reservationRepository.save(
                            new Reservation(reservationWaiting.getMember(), reservationWaiting.getDate(),
                                    reservationWaiting.getTime(), reservationWaiting.getTheme()));
                    reservationWaitingRepository.deleteById(reservationWaiting.getId());
                });
    }

    private void validateDuplicateReservation(final LocalDate localDate, final long timeId, final long themeId) {
        if (reservationRepository.existByDateAndTimeIdAndThemeId(localDate, timeId, themeId)) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 예약 입니다.");
        }
    }

    private void validateExistsById(final long id) {
        if (!reservationRepository.existById(id)) {
            throw new NoSuchElementException("[ERROR] 존재하지 않는 예약 입니다.");
        }
    }

    private long getWaitingOrderByMember(final Member member) {
        return reservationWaitingRepository.findWaitingOrderById(member.getId());
    }
}
