package roomescape.service.reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationWaiting;
import roomescape.domain.reservation.ReservationWaitingRepository;
import roomescape.domain.schedule.ReservationDate;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.ReservationTimeRepository;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.exception.InvalidMemberException;
import roomescape.exception.InvalidReservationException;
import roomescape.service.member.dto.MemberReservationResponse;
import roomescape.service.payment.PaymentService;
import roomescape.service.reservation.dto.ReservationFilterRequest;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.reservation.dto.ReservationResponse;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final PaymentService paymentService;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            MemberRepository memberRepository,
            ReservationWaitingRepository reservationWaitingRepository, PaymentService paymentService) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public ReservationResponse create(ReservationRequest reservationRequest, long memberId) {
        Reservation reservation = generateValidReservation(reservationRequest, memberId);
        Reservation savedReservation = reservationRepository.save(reservation);

        paymentService.confirm(reservationRequest.toPaymentRequest());
        return new ReservationResponse(savedReservation);
    }

    private Reservation generateValidReservation(ReservationRequest reservationRequest, long memberId) {
        LocalDate date = reservationRequest.date();
        long timeId = reservationRequest.timeId();
        long themeId = reservationRequest.themeId();

        ReservationDate reservationDate = ReservationDate.of(date);
        ReservationTime reservationTime = findTimeById(timeId);
        Schedule schedule = new Schedule(reservationDate, reservationTime);
        schedule.validateFuture();

        Theme theme = findThemeById(themeId);
        validateDuplicated(reservationDate, reservationTime, theme);
        Member member = findMemberById(memberId);

        return new Reservation(member, schedule, theme, ReservationStatus.RESERVED);
    }

    private ReservationTime findTimeById(long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new InvalidReservationException("더이상 존재하지 않는 시간입니다."));
    }

    private Theme findThemeById(long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new InvalidReservationException("더이상 존재하지 않는 테마입니다."));
    }

    private Member findMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidMemberException("존재하지 않는 회원입니다."));
    }

    private void validateDuplicated(ReservationDate date, ReservationTime reservationTime, Theme theme) {
        if (reservationRepository.existsByScheduleDateAndScheduleTimeIdAndThemeId(
                date, reservationTime.getId(), theme.getId()
        )) {
            throw new InvalidReservationException("선택하신 테마와 일정은 이미 예약이 존재합니다.");
        }
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public void deleteById(long reservationId) {
        reservationRepository.findById(reservationId)
                .ifPresent(this::cancelReservation);
    }

    private void cancelReservation(Reservation reservation) {
        Theme theme = reservation.getTheme();
        Schedule schedule = reservation.getSchedule();
        reservationRepository.delete(reservation);
        reservationWaitingRepository.findTopByThemeAndScheduleOrderByCreatedAt(theme, schedule)
                .ifPresent(this::convertFirstPriorityWaitingToReservation);
    }

    private void convertFirstPriorityWaitingToReservation(ReservationWaiting waiting) {
        Reservation reservation = new Reservation(
                waiting.getMember(), waiting.getSchedule(), waiting.getTheme(), ReservationStatus.RESERVED
        );
        reservationRepository.save(reservation);
        reservationWaitingRepository.delete(waiting);
    }

    public void deleteById(long reservationId, long memberId) {
        reservationRepository.findById(reservationId)
                .ifPresent(reservation -> reservation.checkCancelAuthority(memberId));
        deleteById(reservationId);
    }

    public List<ReservationResponse> findByCondition(ReservationFilterRequest reservationFilterRequest) {
        ReservationDate dateFrom = ReservationDate.of(reservationFilterRequest.dateFrom());
        ReservationDate dateTo = ReservationDate.of(reservationFilterRequest.dateTo());
        return reservationRepository.findBy(reservationFilterRequest.memberId(), reservationFilterRequest.themeId(),
                dateFrom, dateTo).stream().map(ReservationResponse::new).toList();
    }

    public List<MemberReservationResponse> findReservationsOf(long memberId) {
        Stream<MemberReservationResponse> reservations = reservationRepository.findByMemberId(memberId).stream()
                .map(MemberReservationResponse::from);
        Stream<MemberReservationResponse> waitings = reservationWaitingRepository.findWithRankByMemberId(memberId)
                .stream()
                .map(MemberReservationResponse::from);

        return Stream.concat(reservations, waitings).toList();
    }
}
