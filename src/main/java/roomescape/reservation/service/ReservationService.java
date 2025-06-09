package roomescape.reservation.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.common.event.EventPublisher;
import roomescape.exception.ForbiddenException;
import roomescape.exception.NotFoundException;
import roomescape.exception.ReservationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.RegistrationSlot;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationPolicy;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSearchRequest;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.dto.CreateRegistrationCommand;
import roomescape.reservation.service.dto.ReservationDeleteEvent;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final EventPublisher eventPublisher;
    private final ReservationRepository reservationRepository;
    private final ReservationPolicy reservationPolicy;

    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public List<ReservationResponse> searchReservationsByCriteria(final ReservationSearchRequest request) {
        final List<Reservation> reservations = reservationRepository.findByCriteria(
                request.themeId(),
                request.memberId(),
                request.dateFrom(),
                request.dateTo()
        );
        return ReservationResponse.fromReservations(reservations);
    }

    public ReservationResponse getById(Long reservationId, LoginMember loginMember) {
        Reservation reservation = getReservationById(reservationId);
        validateCanReadPermission(reservation, loginMember);
        return new ReservationResponse(reservation);
    }

    private void validateCanReadPermission(Reservation checkReservation, LoginMember loginMember) {
        if(loginMember.role() == MemberRole.ADMIN || checkReservation.isOwnedBy(loginMember.id())) {
            return;
        }
        log.warn("예약 조회 권한 없음 - memberId={}, reservationId={}, role={}", loginMember.id(), checkReservation.getId(), loginMember.role());
        throw new ReservationException("자신의 예약만 조회할 수 있습니다.");
    }

    @Transactional
    public ReservationResponse registerReservation(CreateRegistrationCommand command) {
        final ReservationTime reservationTime = getReservationTimeById(command.timeId());
        final Theme theme = getThemeById(command.themeId());
        final Member member = getMemberById(command.memberId());
        Reservation reservation = Reservation.createNew(
                member, new RegistrationSlot(reservationTime, theme, command.date()));

        validateCanRegistration(reservation);
        final Reservation saved = reservationRepository.save(reservation);
        log.info("예약 등록 완료 - reservationId={}", saved.getId());
        return new ReservationResponse(saved);
    }

    private void validateCanRegistration(Reservation reservation) {
        boolean existsSameSlot = reservationRepository.existsSameSlot(
                reservation.getDate(), reservation.getTime().getId(), reservation.getTheme().getId());
        reservationPolicy.validateReservationAvailable(reservation, existsSameSlot);
    }

    @Transactional
    public void deleteById(final Long id) {
        final Reservation reservation = reservationRepository.findById(id)
                .orElse(null);
        if (reservation == null) {
            log.warn("예약 삭제 불가 - 존재하지 않음, reservationId={}", id);
            return;
        }
        reservation.cancel();

        log.info("예약 삭제 이벤트 발행 - reservationId={}", id);
        eventPublisher.raise(new ReservationDeleteEvent(id));
        log.info("예약 삭제 완료 - reservationId={}", id);
    }

    public void validateOwnership(Long reservationId, Long memberId) {
        Reservation reservation = getReservationById(reservationId);
        Member member = getMemberById(memberId);
        if(member.isAdmin() || reservation.isOwnedBy(memberId)) {
            return;
        }
        log.warn("해당 예약에 접근 권한이 없는 사용자 - reservationId={}, memberId={}, memberRole={}", reservationId, memberId, member.getRole());
        throw new ForbiddenException("해당 예약에 접근할 권한이 없습니다.");
    }

    private Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 예약 - reservationId={}", reservationId);
                    return new NotFoundException("존재하지 않는 예약입니다, id: " + reservationId);
                });
    }

    private Member getMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 회원 - memberId={}", id);
                    return new NotFoundException("존재하지 않는 멤버입니다.");
                });
    }

    private ReservationTime getReservationTimeById(final Long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 예약 시간 - timeId={}", timeId);
                    return new NotFoundException("존재하지 않는 예약 시간입니다.");
                });
    }

    private Theme getThemeById(final Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 테마 - themeId={}", themeId);
                    return new NotFoundException("존재하지 않는 테마입니다.");
                });
    }
}
