package roomescape.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.BookedMember;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;
import roomescape.domain.reservation.WaitingMember;
import roomescape.domain.reservation.dto.BookedReservationReadOnly;
import roomescape.domain.reservation.dto.WaitingRank;
import roomescape.domain.reservation.repository.BookedMemberRepository;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.ReservationTimeRepository;
import roomescape.domain.reservation.repository.ThemeRepository;
import roomescape.domain.reservation.repository.WaitingMemberRepository;
import roomescape.exception.AuthorizationException;
import roomescape.exception.RoomEscapeBusinessException;
import roomescape.service.dto.LoginMember;
import roomescape.service.dto.PaymentResponse;
import roomescape.service.dto.ReservationBookedResponse;
import roomescape.service.dto.ReservationConditionRequest;
import roomescape.service.dto.ReservationPaymentRequest;
import roomescape.service.dto.ReservationRequest;
import roomescape.service.dto.ReservationResponse;
import roomescape.service.dto.ReservationStatus;
import roomescape.service.dto.UserReservationResponse;
import roomescape.service.dto.WaitingResponse;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final BookedMemberRepository bookedMemberRepository;
    private final WaitingMemberRepository waitingMemberRepository;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            BookedMemberRepository bookedMemberRepository,
            WaitingMemberRepository waitingMemberRepository,
            MemberRepository memberRepository, PaymentService paymentService
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.bookedMemberRepository = bookedMemberRepository;
        this.waitingMemberRepository = waitingMemberRepository;
        this.memberRepository = memberRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public ReservationResponse saveReservation(ReservationRequest reservationRequest) {
        Member member = findMemberById(reservationRequest.memberId());
        Reservation reservation = findReservation(reservationRequest.date(), reservationRequest.timeId(), reservationRequest.themeId());

        if (reservation.isBooked()) {
            WaitingMember waitingMember = reservation.addWaiting(member);

            waitingMemberRepository.save(waitingMember);
            return ReservationResponse.createByWaiting(waitingMember);
        }

        BookedMember bookedMember = reservation.book(member);
        bookedMemberRepository.save(bookedMember);
        return ReservationResponse.createByBooked(bookedMember);
    }

    public ReservationResponse saveReservationWithPayment(ReservationPaymentRequest reservationPaymentRequest) {
        ReservationResponse reservationResponse = saveReservation(reservationPaymentRequest.toReservationRequest());

        if (reservationResponse.status() == ReservationStatus.BOOKED) {
            paymentService.requestApproval(reservationPaymentRequest.toPaymentApproveRequest(reservationResponse.reservationId()));
        }

        return reservationResponse;
    }

    @Transactional(readOnly = true)
    public List<ReservationBookedResponse> findBookedReservationsByCondition(
            ReservationConditionRequest reservationConditionRequest) {
        List<BookedReservationReadOnly> reservations = bookedMemberRepository.findByConditions(
                reservationConditionRequest.dateFrom(),
                reservationConditionRequest.dateTo(),
                reservationConditionRequest.themeId(),
                reservationConditionRequest.memberId()
        );

        return reservations.stream()
                .map(ReservationBookedResponse::from)
                .sorted(Comparator.comparing(ReservationBookedResponse::dateTime))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WaitingResponse> findAllWaiting() {
        return waitingMemberRepository.findAllReadOnly().stream()
                .map(WaitingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserReservationResponse> findMyAllReservationAndWaiting(Long memberId, LocalDate date) {
        Member member = findMemberById(memberId);

        List<BookedMember> bookedMembers = bookedMemberRepository.findByMemberAndReservation_DateGreaterThanEqual(member, date);
        List<PaymentResponse> paymentResponses = paymentService.findPaidByMemberId(member.getId());

        List<WaitingRank> waitingRanks = waitingMemberRepository.findRankByMemberAndDateGreaterThanEqual(member, date);

        return Stream.concat(
                        UserReservationResponse.reservationsToResponseStream(bookedMembers, paymentResponses),
                        UserReservationResponse.waitingsToResponseStream(waitingRanks)
                )
                .sorted(Comparator.comparing(UserReservationResponse::dateTime))
                .toList();
    }

    @Transactional
    public void cancelBooked(Long bookedMemberId) {
        Reservation reservation = reservationRepository.findByBookedMember_Id(bookedMemberId)
                .orElseThrow(() -> new RoomEscapeBusinessException("존재하지 않는 예약입니다."));

        reservation.cancelBooked();
    }

    @Transactional
    public void cancelWaiting(Long waitingMemberId, LoginMember loginMember) {
        WaitingMember foundWaitingMember = waitingMemberRepository.findById(waitingMemberId)
                .orElseThrow(() -> new RoomEscapeBusinessException("존재하지 않는 예약 대기입니다."));

        if (loginMember.isUser() && foundWaitingMember.isNotMemberId(loginMember.id())) {
            throw new AuthorizationException();
        }

        waitingMemberRepository.delete(foundWaitingMember);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RoomEscapeBusinessException("회원이 존재하지 않습니다."));
    }

    private Reservation findReservation(LocalDate date, Long timeId, Long themeId) {
        ReservationTime time = findTimeById(timeId);
        Theme theme = findThemeById(themeId);

        return reservationRepository.findByDateAndTimeAndTheme(date, time, theme)
                .orElseGet(() -> reservationRepository.save(new Reservation(date, time, theme)));
    }

    private Theme findThemeById(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new RoomEscapeBusinessException("존재하지 않는 테마입니다."));
    }

    private ReservationTime findTimeById(Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new RoomEscapeBusinessException("존재하지 않는 예약 시간입니다."));
    }
}

