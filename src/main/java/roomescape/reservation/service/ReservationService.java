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
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.payment.TossPaymentClient;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.ReservationWaiting;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.WaitingRankCalculator;
import roomescape.reservation.dto.MemberReservationAddRequest;
import roomescape.reservation.dto.MemberReservationResponse;
import roomescape.reservation.dto.MemberReservationWithPaymentAddRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeRepository;

@Service
public class ReservationService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final TossPaymentClient tossPaymentClient;

    public ReservationService(MemberRepository memberRepository,
                              ReservationRepository reservationRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository,
                              TossPaymentClient tossPaymentClient) {
        this.memberRepository = memberRepository;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
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
        List<MemberReservationResponse> memberReservationRespons = new ArrayList<>();

        findAllMembersReservedReservation(memberReservationRespons, memberId);
        findAllMembersWaitingReservation(memberReservationRespons, memberId);

        return memberReservationRespons;
    }

    private void findAllMembersReservedReservation(List<MemberReservationResponse> responses, Long memberId) {
        reservationRepository.findAllReservedByMemberId(memberId)
                .stream()
                .map(MemberReservationResponse::new)
                .forEach(responses::add);
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

        tossPaymentClient.confirmPayments(paymentConfirmRequest);
        return saveMemberReservation(memberId, memberReservationAddRequest, Status.RESERVED);
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
        reservationRepository.deleteById(id);
    }

    private void updateWaitingReservationStatus(Reservation reservationForDelete) {
        reservationRepository.findFirstByDateValueAndTimeIdAndThemeIdAndStatus(
                reservationForDelete.getDate(),
                reservationForDelete.getTime().getId(),
                reservationForDelete.getTheme().getId(),
                Status.WAITING
        ).ifPresent(value -> value.updateStatus(Status.RESERVED));
    }
}
