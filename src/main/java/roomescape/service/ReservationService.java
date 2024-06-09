package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationtime.ReservationTimeRepository;
import roomescape.domain.reservationwaiting.ReservationWaitingRepository;
import roomescape.domain.reservationwaiting.WaitingWithRank;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.service.dto.request.CreateReservationRequest;
import roomescape.service.dto.response.PersonalReservationResponse;
import roomescape.service.dto.response.ReservationResponse;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class ReservationService { // todo cqrs

    private final ReservationRepository reservationRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final Clock clock;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationWaitingRepository reservationWaitingRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            MemberRepository memberRepository,
            PaymentRepository paymentRepository,
            Clock clock
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
        this.clock = clock;
    }

    public List<ReservationResponse> getReservationsByConditions(
            Long memberId,
            Long themeId,
            LocalDate dateFrom,
            LocalDate dateTo
    ) {
        List<Reservation> reservations = reservationRepository
                .findAllByConditions(memberId, themeId, dateFrom, dateTo);

        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public ReservationResponse addReservationByAdmin(CreateReservationRequest createReservationRequest) {
        Reservation reservation = createValidatedReservation(createReservationRequest);
        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationResponse.from(savedReservation);
    }

    @Transactional
    public Reservation addReservation(CreateReservationRequest createReservationRequest) {
        Reservation reservation = createValidatedReservation(createReservationRequest);
        return reservationRepository.save(reservation);
    }

    private Reservation createValidatedReservation(CreateReservationRequest createReservationRequest) {
        Reservation reservation = getReservation(createReservationRequest);
        reservation.validateFutureReservation(LocalDateTime.now(clock));
        validateDuplicatedReservation(reservation);
        return reservation;
    }

    private Reservation getReservation(CreateReservationRequest request) {
        Member member = getMember(request.memberId());
        ReservationTime reservationTime = getTime(request.timeId());
        Theme theme = getTheme(request.themeId());
        return request.toReservation(reservationTime, theme, member);
    }

    private Member getMember(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
    }

    private ReservationTime getTime(long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 예약 시간입니다."));
    }

    private Theme getTheme(long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 테마입니다."));
    }

    private void validateDuplicatedReservation(Reservation reservation) {
        Optional<Reservation> optionalReservation = reservationRepository.findByDateAndTimeAndThemeAndStatusIs(
                reservation.getDate(), reservation.getTime(), reservation.getTheme(), ReservationStatus.ACCEPTED);
        optionalReservation.ifPresent(r -> {
            throw new IllegalArgumentException("해당 날짜/시간에 이미 예약이 존재합니다.");
        });
    }

    @Transactional
    public void delete(Reservation reservation) {
        reservationRepository.delete(reservation);
    }

    @Transactional
    public Reservation deleteReservationById(Long id) { // todo 이름 변경
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 예약입니다."));
        reservation.cancel();
        return reservation;
    }

    @Transactional
    public void rollbackDelete(Reservation reservation) {
        reservationRepository.findById(reservation.getId())
                .ifPresent(Reservation::accept);
    }

    public List<PersonalReservationResponse> getReservationsByMemberId(long memberId) { // todo refactor
        Member member = getMember(memberId);
        List<Reservation> reservations = reservationRepository.findAllByMemberAndStatusIs(member, ReservationStatus.ACCEPTED);
        Map<Long, Payment> reservationPayments = paymentRepository.findAllByReservationIn(reservations)
                .stream()
                .collect(Collectors.toMap(Payment::getReservationId, Function.identity()));
        List<WaitingWithRank> waitingWithRanks = reservationWaitingRepository.findAllWithRankByMember(member);

        return Stream.concat(
                        reservations.stream().map(r -> PersonalReservationResponse.from(r, reservationPayments.get(r.getId()))),
                        waitingWithRanks.stream().map(PersonalReservationResponse::from))
                .sorted(Comparator.comparing(PersonalReservationResponse::date)
                        .thenComparing(PersonalReservationResponse::time))
                .toList();
    }
}
