package roomescape.reservation.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.client.PaymentClient;
import roomescape.member.domain.Member;
import roomescape.member.dto.LoginMemberInToken;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.Waitings;
import roomescape.reservation.dto.request.PaymentRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationSearchRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.WaitingResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@Service
@Transactional
public class ReservationService {
    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final PaymentClient paymentClient;


    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            MemberRepository memberRepository,
            PaymentClient paymentClient
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.paymentClient = paymentClient;
    }

    public Long save(ReservationCreateRequest reservationCreateRequest, LoginMemberInToken loginMemberInToken) {
        paymentClient.payForReservation(getAuthorizations(), PaymentRequest.toRequest(reservationCreateRequest));

        if (reservationRepository.existsByDateAndReservationTimeIdAndThemeId(reservationCreateRequest.date(),
                reservationCreateRequest.timeId(), reservationCreateRequest.themeId())) {
            Reservation reservation = getValidatedReservation(reservationCreateRequest, loginMemberInToken,
                    Status.WAITING);
            return reservationRepository.save(reservation).getId();
        }
        Reservation reservation = getValidatedReservation(reservationCreateRequest, loginMemberInToken, Status.SUCCESS);
        return reservationRepository.save(reservation).getId();
    }


    private String getAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((WIDGET_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }


    private Reservation getValidatedReservation(ReservationCreateRequest reservationCreateRequest,
                                                LoginMemberInToken loginMemberInToken,
                                                Status status) {
        ReservationTime reservationTime = reservationTimeRepository.findById(reservationCreateRequest.timeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약 시간입니다."));

        Theme theme = themeRepository.findById(reservationCreateRequest.themeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 테마입니다."));

        Member member = getValidatedMemberByRole(loginMemberInToken);

        return reservationCreateRequest.toReservation(member, theme, reservationTime, status);
    }

    private Member getValidatedMemberByRole(LoginMemberInToken loginMemberInToken) {
        return memberRepository.findById(loginMemberInToken.id())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    public ReservationResponse findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        return ReservationResponse.toResponse(reservation);
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::toResponse)
                .toList();
    }

    public List<ReservationResponse> findAllBySearch(ReservationSearchRequest reservationSearchRequest) {
        Theme theme = themeRepository.findById(reservationSearchRequest.themeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 테마 정보 입니다."));
        Member member = memberRepository.findById(reservationSearchRequest.memberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 정보입니다."));
        return reservationRepository.findAllByMemberAndThemeAndDateBetween(member, theme,
                        reservationSearchRequest.dateFrom(), reservationSearchRequest.dateTo()).stream()
                .map(ReservationResponse::toResponse)
                .toList();
    }

    public List<MyReservationResponse> findAllByMemberId(Long memberId) {
        List<Reservation> waitingReservation = reservationRepository.findAllByStatus(Status.WAITING);
        Waitings waitings = new Waitings(waitingReservation);

        return reservationRepository.findAllByMemberId(memberId).stream()
                .map(reservation -> MyReservationResponse.toResponse(reservation,
                        waitings.findMemberRank(reservation, memberId)))
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약 정보입니다."));
        reservationRepository.deleteById(id);

        Waitings waitings = new Waitings(reservationRepository.findAllByDateAndReservationTimeIdAndThemeIdAndStatus(
                reservation.getDate(), reservation.getTime().getId(), reservation.getTheme().getId(), Status.WAITING));
        isAvailableChangeToReservation(waitings);
    }

    private void isAvailableChangeToReservation(Waitings waitings) {
        if (waitings.haveWaiting()) {
            Reservation firstWaiting = waitings.getFirstWaiting();
            firstWaiting.changeSuccess();
        }
    }

    public List<WaitingResponse> findWaiting() {
        return reservationRepository.findAllByStatus(Status.WAITING).stream()
                .map(reservation -> WaitingResponse.toResponse(reservation.getMember(), reservation.getTheme(),
                        reservation, reservation.getTime()))
                .toList();
    }
}
