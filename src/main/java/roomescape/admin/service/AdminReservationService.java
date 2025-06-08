package roomescape.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.InvalidReservationException;
import roomescape.common.util.DateTime;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.reservationTime.domain.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;

@Service
public class AdminReservationService {

    private static final Logger log = LoggerFactory.getLogger(AdminReservationService.class);

    private final DateTime dateTime;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public AdminReservationService(DateTime dateTime, ReservationRepository reservationRepository, ReservationTimeRepository reservationTimeRepository, ThemeRepository themeRepository, MemberRepository memberRepository) {
        this.dateTime = dateTime;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public ReservationResponse createReservation(final ReservationWithPaymentRequest request, final Long memberId) {
        ReservationTime time = reservationTimeRepository.findById(request.timeId())
                .orElseThrow(() -> {
                    log.warn("예약 시간 없음 - timeId: {}", request.timeId());
                    return new InvalidReservationException("존재하지 않는 시간입니다.");
                });
        Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> {
                    log.warn("테마 없음 - themeId: {}", request.themeId());
                    return new InvalidReservationException("존재하지 않는 테마입니다.");
                });
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("회원 없음 - memberId: {}", memberId);
                    return new InvalidReservationException("존재 하지 않는 유저입니다.");
                });

        Reservation reservation = Reservation.createWithoutId(dateTime.now(), member, request.date(), time, theme, null);

        if (reservationRepository.existsByDateAndTimeStartAtAndThemeId(
                reservation.getDate(),
                reservation.getReservationTime(),
                reservation.getThemeId()
        )) {
            log.warn("중복 예약 시도 - date: {}, time: {}, themeId: {}");
            throw new InvalidReservationException("이미 예약이 존재합니다.");
        }

        Reservation save = reservationRepository.save(reservation);
        log.info("예약 생성 성공 - reservationId: {}", save.getId());

        return ReservationResponse.from(save);
    }

}
