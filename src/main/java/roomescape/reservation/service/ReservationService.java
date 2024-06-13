package roomescape.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.DuplicateSaveException;
import roomescape.global.exception.IllegalReservationDateException;
import roomescape.global.exception.NoSuchRecordException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.Status;
import roomescape.reservation.dto.MemberMyReservationResponse;
import roomescape.reservation.dto.MemberReservationAddRequest;
import roomescape.reservation.dto.MemberReservationStatusResponse;
import roomescape.reservation.dto.MemberReservationWithPaymentAddRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeRepository;

@Service
public class ReservationService {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;

    public ReservationService(
            MemberRepository memberRepository,
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            PaymentService paymentService,
            PaymentRepository paymentRepository
    ) {
        this.memberRepository = memberRepository;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    public List<ReservationResponse> findAllReservation() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public MemberReservationStatusResponse findById(Long id) {
        Reservation foundReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NoSuchRecordException("해당하는 예약이 존재하지 않습니다 ID: " + id));
        return new MemberReservationStatusResponse(foundReservation);
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

    public List<MemberReservationStatusResponse> findAllByMemberId(Long memberId) {
        Stream<MemberReservationStatusResponse> reservedReservationResponses = reservationRepository.findAllReservedByMemberId(memberId).stream()
                .map(MemberReservationStatusResponse::new);

        Stream<MemberReservationStatusResponse> waitingReservationResponses = reservationRepository.findAllReservationWaitingByMemberId(memberId)
                .stream()
                .map(MemberReservationStatusResponse::new);

        return Stream.concat(reservedReservationResponses, waitingReservationResponses)
                .sorted(Comparator.comparing(MemberReservationStatusResponse::date))
                .toList();
    }

    public List<MemberMyReservationResponse> findMyReservation(Long memberId) {
        return reservationRepository.findAllMyReservation(memberId);
    }

    @Transactional
    public ReservationResponse saveMemberReservation(Long memberId, MemberReservationWithPaymentAddRequest request) {
        MemberReservationAddRequest memberReservationAddRequest = new MemberReservationAddRequest(request);
        validateDuplicatedReservation(memberReservationAddRequest);

        Reservation reservation = saveMemberReservation(memberId, memberReservationAddRequest, Status.RESERVED);
        paymentService.confirmPayment(new PaymentConfirmRequest(request), reservation.getId());
        return new ReservationResponse(reservation);
    }

    public ReservationResponse saveAdminReservation(Long memberId, MemberReservationAddRequest request) {
        MemberReservationAddRequest memberReservationAddRequest = new MemberReservationAddRequest(request);
        validateDuplicatedReservation(memberReservationAddRequest);

        Reservation reservation = saveMemberReservation(memberId, memberReservationAddRequest, Status.RESERVED);
        return new ReservationResponse(reservation);
    }

    public ReservationResponse saveMemberWaitingReservation(Long memberId, MemberReservationAddRequest request) {
        validateDuplicatedWaitingReservation(memberId, request);
        Reservation reservation = saveMemberReservation(memberId, request, Status.WAITING);
        return new ReservationResponse(reservation);
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

    private Reservation saveMemberReservation(
            Long memberId,
            MemberReservationAddRequest request,
            Status status
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchRecordException("ID: " + memberId + " 해당하는 회원을 찾을 수 없습니다"));
        ReservationTime reservationTime = getReservationTime(request.timeId());
        validateReservingPastTime(request.date(), reservationTime.getStartAt());
        Theme theme = getTheme(request.themeId());

        return reservationRepository.save(new Reservation(member, request.date(), reservationTime, theme, status, LocalDateTime.now()));
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
            updateWaitingReservationStatus(reservationForDelete);
        }
        paymentRepository.deleteByReservationId(id);
        reservationRepository.deleteById(id);
    }

    private void updateWaitingReservationStatus(Reservation reservationForDelete) {
        reservationRepository.findFirstByDateValueAndTimeIdAndThemeIdAndStatus(
                reservationForDelete.getDate(),
                reservationForDelete.getTime().getId(),
                reservationForDelete.getTheme().getId(),
                Status.WAITING
        ).ifPresent(value -> value.updateStatus(Status.PAYMENT_WAITING));
    }
}
