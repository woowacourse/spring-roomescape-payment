package roomescape.reservation.domain.service;

import org.springframework.stereotype.Service;
import roomescape.exception.ResourceNotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.entity.MemberReservation;
import roomescape.reservation.domain.entity.Reservation;
import roomescape.reservation.dto.ReservationCreateRequest;
import roomescape.reservation.repository.MemberReservationRepository;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.ReservationTimeRepository;

import java.time.LocalDate;

@Service
public class ReservationCreateService {

    private final MemberReservationRepository memberReservationRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;

    public ReservationCreateService(MemberReservationRepository memberReservationRepository,
                                    ReservationRepository reservationRepository,
                                    MemberRepository memberRepository,
                                    ReservationTimeRepository reservationTimeRepository,
                                    ThemeRepository themeRepository) {
        this.memberReservationRepository = memberReservationRepository;
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
    }

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다."));
    }

    private ReservationTime getReservationTimeById(Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 예약 시간입니다."));
    }

    private Theme getThemeById(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 테마입니다."));
    }

    public MemberReservation createReservation(ReservationCreateRequest request) {
        Member member = getMemberById(request.memberId());
        Reservation reservation = getReservationOrSave(request);

        MemberReservation memberReservation = request.toMemberReservation(member, reservation);
        reservation.validateIsBeforeNow();
        validateDuplicated(memberReservation);

        return memberReservationRepository.save(memberReservation);
    }

    private Reservation getReservationOrSave(ReservationCreateRequest request) {
        Long timeId = request.timeId();
        Long themeId = request.themeId();
        LocalDate date = request.date();

        return reservationRepository.findByDateAndTimeIdAndThemeId(date, timeId, themeId)
                .orElseGet(() -> createReservation(timeId, themeId, date));
    }

    private Reservation createReservation(Long timeId, Long themeId, LocalDate date) {
        ReservationTime time = getReservationTimeById(timeId);
        Theme theme = getThemeById(themeId);

        Reservation reservation = new Reservation(date, time, theme);
        return reservationRepository.save(reservation);
    }

    private void validateDuplicated(MemberReservation memberReservation) {
        if (memberReservation.isNotWaitingStatus()) {
            validateNotWaitingReservation(memberReservation);
            return;
        }

        validateWaitingReservation(memberReservation);
    }

    private void validateNotWaitingReservation(MemberReservation memberReservation) {
        memberReservationRepository.findByReservationAndStatusIsConfirmationAndPending(
                        memberReservation.getReservation())
                .ifPresent(memberReservation::validateDuplicated);
    }

    private void validateWaitingReservation(MemberReservation memberReservation) {
        memberReservationRepository.findByReservationAndMember(
                        memberReservation.getReservation(),
                        memberReservation.getMember())
                .ifPresent(memberReservation::validateDuplicated);
    }
}
