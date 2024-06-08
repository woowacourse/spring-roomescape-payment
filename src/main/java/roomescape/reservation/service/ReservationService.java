package roomescape.reservation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.member.domain.Member;
import roomescape.member.dto.LoginMemberInToken;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.Waitings;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationSearchRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.WaitingResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@Service
@Transactional(readOnly = true)
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;


    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            MemberRepository memberRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public long save(ReservationCreateRequest request, LoginMemberInToken loginMemberInToken) {
        Reservation reservation = getValidatedReservation(request, loginMemberInToken);

        return reservationRepository.save(reservation).getId();
    }

    private Reservation getValidatedReservation(ReservationCreateRequest request,
                                                LoginMemberInToken loginMemberInToken) {
        ReservationTime reservationTime = reservationTimeRepository.findById(request.timeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약 시간입니다."));

        Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 테마입니다."));

        Member member = getValidatedMemberByRole(loginMemberInToken);

        boolean reserved = reservationRepository.existsByDateAndReservationTimeIdAndThemeId(request.date(),
                request.timeId(), request.themeId());
        if (reserved) {
            return new Reservation(member, request.date(), theme, reservationTime, Status.WAITING);
        }

        return new Reservation(member, request.date(), theme, reservationTime, Status.SUCCESS);
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
