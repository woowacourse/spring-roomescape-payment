package roomescape.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.common.exception.custom.AlreadyInUseException;
import roomescape.common.exception.custom.EntityNotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberId;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationId;
import roomescape.reservation.domain.ReservationWithPayment;
import roomescape.reservation.dto.request.FilteringReservationRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.response.BookedReservationTimeResponse;
import roomescape.reservation.dto.response.MyReservationsResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.ReservationTimeResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.waiting.domain.Waiting;
import roomescape.reservation.waiting.domain.WaitingWithRank;
import roomescape.reservation.waiting.repository.WaitingRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeId;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeId;
import roomescape.time.repository.ReservationTimeRepository;

@Slf4j
@Service
public class ReservationService {

    private final PaymentService paymentService;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;

    public ReservationService(
            final PaymentService paymentService,
            final ReservationRepository reservationRepository,
            final ReservationTimeRepository reservationTimeRepository,
            final ThemeRepository themeRepository,
            final MemberRepository memberRepository,
            final WaitingRepository waitingRepository) {
        this.paymentService = paymentService;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.waitingRepository = waitingRepository;
    }

    @Transactional
    public ReservationResponse create(final ReservationCreateRequest request) {
        validateReservation(request);

        Reservation reservation = createReservation(request, request.loginMember());
        validateDateTime(LocalDateTime.now(), reservation.getDate(), reservation.getTime().getStartAt());
        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("예약 저장 완료: reservationId={}", savedReservation.getId());

        return ReservationResponse.from(savedReservation);
    }

    @Transactional
    public ReservationResponse createWithPayment(final ReservationCreateRequest request,
                                                 final PaymentRequest paymentRequest) {
        validateReservation(request);

        paymentService.confirm(paymentRequest);

        Reservation reservation = createReservation(request, request.loginMember());
        validateDateTime(LocalDateTime.now(), reservation.getDate(), reservation.getTime().getStartAt());
        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("예약 저장 완료: reservationId={}", savedReservation.getId());

        paymentService.create(savedReservation.getId(), paymentRequest);

        return ReservationResponse.from(savedReservation);
    }

    private Reservation createReservation(final ReservationCreateRequest request, final LoginMember loginMember) {
        ReservationTime reservationTime = getReservationTime(request);
        Theme theme = getTheme(request);
        Member member = getMember(loginMember);
        return new Reservation(member, request.date(), reservationTime, theme);
    }

    public List<ReservationResponse> getFilteredReservations(final FilteringReservationRequest request) {
        ThemeId themeId = new ThemeId(request.themeId());
        MemberId memberId = new MemberId(request.memberId());
        LocalDate dateFrom = request.dateFrom();
        LocalDate dateTo = request.dateTo();

        return reservationRepository.findByThemeIdAndMemberIdAndDateBetween(themeId, memberId, dateFrom, dateTo)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> getAll() {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<MyReservationsResponse> getAllMyReservations(final LoginMember loginMember) {
        List<WaitingWithRank> waitingWithRanks = waitingRepository.findAllWaitingWithRankByMemberId(
                new MemberId(loginMember.id()));
        List<ReservationWithPayment> reservationsWithPayment = reservationRepository.findReservationsWithPayment(
                new MemberId(loginMember.id()));
        return toMyReservationResponses(reservationsWithPayment, waitingWithRanks);
    }

    public List<BookedReservationTimeResponse> getSortedAvailableTimes(final LocalDate date, final Long themeId) {
        Map<ReservationTime, Boolean> allTimes = processAlreadyBookedTimesMap(date, themeId);

        return allTimes.keySet()
                .stream()
                .map(key -> bookedReservationTimeResponseOf(key, allTimes.get(key)))
                .sorted(Comparator.comparing(bookedReservationTimeResponse
                        -> bookedReservationTimeResponse.timeResponse().startAt()
                )).toList();
    }

    @Transactional
    public void delete(final Long id) {
        ReservationId reservationId = new ReservationId(id);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 예약입니다."));

        paymentService.delete(reservationId);
        reservationRepository.deleteById(reservationId);
        log.info("예약 삭제 완료: reservationId={}", reservationId);
        approveFirstWaiting(reservation);
    }

    private void validateReservation(ReservationCreateRequest request) {
        if (isAlreadyBooked(request)) {
            throw new AlreadyInUseException("이미 예약이 존재합니다.");
        }
        if (hasAlreadyWaiting(request)) {
            throw new AlreadyInUseException("예약 대기가 존재해 예약을 생성할 수 없습니다.");
        }
    }

    private void validateDateTime(final LocalDateTime now, final LocalDate date, final LocalTime time) {
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        if (now.isAfter(dateTime)) {
            log.warn("지난 날짜, 시간에 예약 시도: now={}, requestDateTime={}", now, dateTime);
            throw new IllegalArgumentException("이미 지난 예약 시간입니다.");
        }
    }

    private void approveFirstWaiting(final Reservation reservation) {
        Optional<Waiting> waiting = waitingRepository.findFirstByDateAndTimeAndThemeOrderByCreatedAtAsc(
                reservation.getDate(),
                reservation.getTime(),
                reservation.getTheme()
        );
        waiting.ifPresent(value -> {
            reservationRepository.save(new Reservation(
                    value.getMember(), value.getDate(), value.getTime(), value.getTheme()
            ));
            log.info("대기 1순위 승인 완료: memberId={}, date={}, timeId={}, themeId={}",
                    value.getMember().getId(), value.getDate(), value.getTime().getId(), value.getTheme().getId());
            waitingRepository.delete(value);
            log.info("대기 1순위 삭제 완료: memberId={}, date={}, timeId={}, themeId={}",
                    value.getMember().getId(), value.getDate(), value.getTime().getId(), value.getTheme().getId());
        });
    }

    private List<MyReservationsResponse> toMyReservationResponses(
            final List<ReservationWithPayment> reservations,
            final List<WaitingWithRank> waitingWithRanks
    ) {
        List<MyReservationsResponse> responses = new ArrayList<>();
        for (ReservationWithPayment reservation : reservations) {
            responses.add(MyReservationsResponse.from(reservation));
        }
        for (WaitingWithRank waitingWithRank : waitingWithRanks) {
            responses.add(MyReservationsResponse.from(waitingWithRank));
        }
        return responses;
    }

    private BookedReservationTimeResponse bookedReservationTimeResponseOf(
            final ReservationTime reservationTime,
            final boolean isAlreadyBooked
    ) {
        return new BookedReservationTimeResponse(ReservationTimeResponse.from(reservationTime), isAlreadyBooked);
    }

    private Map<ReservationTime, Boolean> processAlreadyBookedTimesMap(final LocalDate date, final Long themeId) {
        Set<ReservationTime> alreadyBookedTimes = getAlreadyBookedTimes(date, themeId);

        return reservationTimeRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Function.identity(), alreadyBookedTimes::contains));
    }

    private boolean hasAlreadyWaiting(final ReservationCreateRequest request) {
        return waitingRepository.existsByDateAndTimeIdAndThemeId(
                request.date(),
                new ReservationTimeId(request.timeId()),
                new ThemeId(request.themeId())
        );
    }

    private boolean isAlreadyBooked(final ReservationCreateRequest request) {
        return reservationRepository.existsByDateAndTimeIdAndThemeId(
                request.date(),
                new ReservationTimeId(request.timeId()),
                new ThemeId(request.themeId())
        );
    }

    private Theme getTheme(final ReservationCreateRequest request) {
        Long themeId = request.themeId();
        return themeRepository.findById(themeId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 테마 조회 시도: themeId={}", themeId);
                    return new EntityNotFoundException("존재하지 않는 테마입니다");
                });
    }

    private ReservationTime getReservationTime(final ReservationCreateRequest request) {
        Long timeId = request.timeId();
        return reservationTimeRepository.findById(new ReservationTimeId(timeId))
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 시간 조회 시도: timeId={}", timeId);
                    return new EntityNotFoundException("존재하지 않는 시간입니다");
                });
    }

    private Member getMember(LoginMember loginMember) {
        MemberId memberId = new MemberId(loginMember.id());
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 회원 조회 시도: memberId={}", memberId);
                    return new EntityNotFoundException("등록되지 않은 회원입니다.");
                });
    }

    private Set<ReservationTime> getAlreadyBookedTimes(final LocalDate date, final Long themeId) {
        return reservationRepository.findByDateAndThemeId(date, new ThemeId(themeId))
                .stream()
                .map(Reservation::getTime)
                .collect(Collectors.toSet());
    }
}
