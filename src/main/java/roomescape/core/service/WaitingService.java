package roomescape.core.service;

import static roomescape.core.exception.ExceptionMessage.ALLOWED_TO_ADMIN_ONLY_EXCEPTION;
import static roomescape.core.exception.ExceptionMessage.BOOKED_TIME_WAITING_EXCEPTION;
import static roomescape.core.exception.ExceptionMessage.MEMBER_NOT_FOUND_EXCEPTION;
import static roomescape.core.exception.ExceptionMessage.THEME_NOT_FOUND_EXCEPTION;
import static roomescape.core.exception.ExceptionMessage.TIME_NOT_FOUND_EXCEPTION;
import static roomescape.core.exception.ExceptionMessage.WAITED_TIME_WAITING_EXCEPTION;
import static roomescape.core.exception.ExceptionMessage.WAITING_IS_NOT_YOURS_EXCEPTION;
import static roomescape.core.exception.ExceptionMessage.WAITING_NOT_FOUND_EXCEPTION;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.core.domain.Member;
import roomescape.core.domain.ReservationTime;
import roomescape.core.domain.Theme;
import roomescape.core.domain.Waiting;
import roomescape.core.dto.member.LoginMember;
import roomescape.core.dto.waiting.WaitingRequest;
import roomescape.core.dto.waiting.WaitingResponse;
import roomescape.core.repository.MemberRepository;
import roomescape.core.repository.ReservationRepository;
import roomescape.core.repository.ReservationTimeRepository;
import roomescape.core.repository.ThemeRepository;
import roomescape.core.repository.WaitingRepository;

@Service
public class WaitingService {
    private final WaitingRepository waitingRepository;
    private final MemberRepository memberRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public WaitingService(final WaitingRepository waitingRepository, final MemberRepository memberRepository,
                          final ReservationTimeRepository reservationTimeRepository,
                          final ThemeRepository themeRepository,
                          final ReservationRepository reservationRepository) {
        this.waitingRepository = waitingRepository;
        this.memberRepository = memberRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public WaitingResponse create(final WaitingRequest request) {
        final Waiting waiting = buildWaiting(request);

        validateDuplicatedReservation(waiting);
        validateDuplicateWaiting(waiting);

        return new WaitingResponse(waitingRepository.save(waiting));
    }

    private void validateDuplicatedReservation(final Waiting waiting) {
        final Member member = waiting.getMember();
        final LocalDate date = waiting.getDate();
        final ReservationTime time = waiting.getTime();
        final Theme theme = waiting.getTheme();
        final Integer reservationCount = reservationRepository.countByMemberAndDateAndTimeAndTheme(member, date, time,
                theme);

        if (reservationCount > 0) {
            throw new IllegalArgumentException(BOOKED_TIME_WAITING_EXCEPTION.getMessage());
        }
    }

    private void validateDuplicateWaiting(final Waiting waiting) {
        final Member member = waiting.getMember();
        final LocalDate date = waiting.getDate();
        final ReservationTime time = waiting.getTime();
        final Theme theme = waiting.getTheme();
        final boolean isWaitingExist = waitingRepository.existsByMemberAndDateAndTimeAndTheme(member, date, time,
                theme);

        if (isWaitingExist) {
            throw new IllegalArgumentException(WAITED_TIME_WAITING_EXCEPTION.getMessage());
        }
    }

    private Waiting buildWaiting(final WaitingRequest request) {
        final Member member = getMemberById(request.getMemberId());
        final ReservationTime reservationTime = getReservationTimeById(request.getTimeId());
        final Theme theme = getThemeById(request.getThemeId());

        return new Waiting(member, request.getDate(), reservationTime, theme);
    }

    private Member getMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND_EXCEPTION.getMessage()));
    }

    private ReservationTime getReservationTimeById(final Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TIME_NOT_FOUND_EXCEPTION.getMessage()));
    }

    private Theme getThemeById(final Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(THEME_NOT_FOUND_EXCEPTION.getMessage()));
    }

    @Transactional(readOnly = true)
    public List<WaitingResponse> findAll() {
        return waitingRepository.findAll()
                .stream()
                .map(WaitingResponse::new)
                .toList();
    }

    @Transactional
    public void delete(final long id, final LoginMember loginMember) {
        final Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND_EXCEPTION.getMessage()));

        final Waiting waiting = waitingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(WAITING_NOT_FOUND_EXCEPTION.getMessage()));

        if (waiting.isNotOwner(member)) {
            throw new IllegalArgumentException(WAITING_IS_NOT_YOURS_EXCEPTION.getMessage());
        }

        waitingRepository.delete(waiting);
    }

    @Transactional
    public void deleteByAdmin(final long id, final LoginMember loginMember) {
        final Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND_EXCEPTION.getMessage()));

        if (member.isNotAdmin()) {
            throw new IllegalArgumentException(ALLOWED_TO_ADMIN_ONLY_EXCEPTION.getMessage());
        }
        waitingRepository.deleteById(id);
    }
}
