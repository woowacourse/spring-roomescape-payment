package roomescape.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.ReservationWithRank;
import roomescape.dto.request.AddReservationRequest;
import roomescape.dto.request.AdminCreateReservationRequest;
import roomescape.dto.request.CreateWaitReservationRequest;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.dto.response.MyReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationWaitResponse;
import roomescape.entity.Member;
import roomescape.entity.Reservation;
import roomescape.entity.ReservationTime;
import roomescape.entity.Theme;
import roomescape.exception.custom.InvalidMemberException;
import roomescape.exception.custom.InvalidReservationException;
import roomescape.exception.custom.InvalidReservationTimeException;
import roomescape.exception.custom.InvalidThemeException;
import roomescape.global.ReservationStatus;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@Service
@Transactional
public class ReservationService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;

    public ReservationService(MemberRepository memberRepository,
                              ReservationRepository reservationRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository) {
        this.memberRepository = memberRepository;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
    }

    public Reservation addReservation(AddReservationRequest request, LoginMemberRequest loginMemberRequest) {
        return createReservation(loginMemberRequest.id(), request.themeId(), request.date(),
                request.timeId(), ReservationStatus.PENDING);
    }

    public ReservationResponse addReservationByAdmin(AdminCreateReservationRequest request) {
        Reservation reservation = createReservation(request.memberId(), request.themeId(), request.date(),
                request.timeId(), ReservationStatus.RESERVED);
        return ReservationResponse.from(reservation);
    }

    public ReservationWaitResponse addWaitReservation(CreateWaitReservationRequest request,
                                                      LoginMemberRequest loginMemberRequest) {
        Reservation reservation = createReservation(loginMemberRequest.id(), request.themeId(), request.date(),
                request.timeId(),
                ReservationStatus.WAIT);
        return ReservationWaitResponse.from(reservation);
    }

    public void approveWaitReservationByAdmin(long waitReservationId) {
        Reservation waitReservation = reservationRepository.findById(waitReservationId)
                .orElseThrow(() -> new InvalidReservationException("존재하지 않는 예약 대기입니다."));

        checkStatus(waitReservation);

        Optional<Reservation> cancelTargetOptional = reservationRepository.findByDateAndReservationTimeAndThemeAndStatus(
                waitReservation.getDate(),
                waitReservation.getReservationTime(),
                waitReservation.getTheme(),
                ReservationStatus.RESERVED);
        cancelTargetOptional.ifPresent(Reservation::cancel);

        waitReservation.waitToPending();
    }

    public void rejectWaitReservationByAdmin(long waitReservationId) {
        Reservation waitReservation = reservationRepository.findById(waitReservationId)
                .orElseThrow(() -> new InvalidReservationException("존재하지 않는 예약 대기입니다."));

        checkStatus(waitReservation);

        waitReservation.cancel();
    }

    private void checkStatus(final Reservation waitReservation) {
        if (waitReservation.getStatus() != ReservationStatus.WAIT) {
            throw new InvalidReservationException("대기 중인 예약이 아닙니다.");
        }
    }

    private Reservation createReservation(long memberId,
                                          long themeId,
                                          LocalDate date,
                                          long timeId,
                                          ReservationStatus status) {
        Member member = memberRepository.findFetchById(memberId)
                .orElseThrow(() -> new InvalidMemberException("존재하지 않는 멤버 ID입니다."));
        ReservationTime reservationTime = reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new InvalidReservationTimeException("존재하지 않는 예약 시간입니다."));
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new InvalidThemeException("존재하지 않는 테마입니다."));

        if (status == ReservationStatus.RESERVED || status == ReservationStatus.PENDING) {
            checkExistedReservation(date, timeId, themeId);
        }

        Reservation reservation = member.reserve(date, reservationTime, theme, status);
        reservationRepository.flush();
        return reservation;
    }

    private void checkExistedReservation(LocalDate date, long timeId, long themeId) {
        boolean exists = reservationRepository.existsAlreadyReservedReservation(date, timeId, themeId,
                ReservationStatus.RESERVED, ReservationStatus.PENDING);
        if (exists) {
            throw new InvalidReservationException("이미 예약이 존재합니다.");
        }
    }

    public List<ReservationResponse> findAllReserved() {
        List<Reservation> reservations = reservationRepository.findAllFetchByStatus(ReservationStatus.RESERVED);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InvalidReservationException("존재하지 않는 예약 입니다."));
        reservation.cancel();
        confirmNextWaitingReservation(reservation);
    }

    private void confirmNextWaitingReservation(Reservation reservation) {
        List<Member> targetMembers = memberRepository.findNextReserveMember(reservation.getDate(),
                reservation.getReservationTime().getId(),
                reservation.getTheme().getId(),
                ReservationStatus.WAIT,
                PageRequest.of(0, 1));
        if (targetMembers.isEmpty()) {
            return;
        }
        Member member = targetMembers.getFirst();
        member.waitToPending(reservation.getDate(), reservation.getReservationTime(), reservation.getTheme());
    }

    public List<MyReservationResponse> findAllReservationOfMember(Long memberId) {
        Member member = memberRepository.findFetchById(memberId)
                .orElseThrow(() -> new InvalidMemberException("존재하지 않는 멤버 ID입니다."));
        List<Reservation> reservations = reservationRepository.findAll();
        List<ReservationWithRank> reservationWithRanks = member.calculateReservationRanks(reservations);

        return reservationWithRanks.stream()
                .map(MyReservationResponse::from)
                .toList();
    }

    public List<Reservation> findAllByFilter(
            Long memberId,
            Long themeId,
            LocalDate dateFrom,
            LocalDate dateTo
    ) {
        return reservationRepository.findAllByFilter(memberId, themeId, dateFrom, dateTo);
    }

    public List<ReservationWaitResponse> findAllByStatus(ReservationStatus status) {
        List<Reservation> waitReservations = reservationRepository.findAllFetchByStatus(status);
        return waitReservations.stream()
                .map(ReservationWaitResponse::from)
                .toList();
    }

    public Reservation pendingToReserve(Long reservationId,
                                        LoginMemberRequest loginMemberRequest) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InvalidReservationException("존재하지 않는 예약입니다."));

        if (!loginMemberRequest.id().equals(reservation.getMember().getId())) {
            throw new InvalidMemberException("예약자 본인만 예약 확정 가능합니다.");
        }

        reservation.pendingToReserve();
        return reservation;
    }
}
