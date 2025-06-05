package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.global.auth.dto.UserInfo;
import roomescape.member.domain.Member;
import roomescape.member.exception.MemberNotFoundException;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.infrastructure.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationInfo;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.exception.ReservationNotFoundException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.dto.ReservationWithPayment;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.exception.ReservationTimeNotFoundException;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.exception.ThemeNotFoundException;
import roomescape.theme.repository.ThemeRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;

    public ReservationService(final ReservationRepository reservationRepository,
                              final ReservationTimeRepository reservationTimeRepository,
                              final ThemeRepository themeRepository,
                              final MemberRepository memberRepository,
                              final PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<Reservation> getReservations(final Long themeId, final Long memberId,
                                             final LocalDate startDate, final LocalDate endDate) {
        if ((themeId == null) || (memberId == null) || (startDate == null) || (endDate == null)) {
            return reservationRepository.findAll();
        }
        return reservationRepository.findByInfoThemeIdAndMemberIdAndInfoDateBetween(themeId, memberId, startDate,
                endDate);
    }

    public void delete(Long id) {
        paymentRepository.deleteById(id);
        reservationRepository.deleteById(id);
    }

    public boolean isReservationExists(ReservationRequest request) {
        return reservationRepository.existsByInfoDateAndInfoTimeIdAndInfoThemeId(request.date(), request.timeId(),
                request.themeId());
    }

    public List<ReservationWithPayment> findMyReservations(final UserInfo userInfo) {
        return reservationRepository.findReservationWithPaymentByMemberId(userInfo.id());
    }

    public Reservation save(final Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Reservation findById(final Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("요청한 id와 일치하는 예약 정보가 없습니다."));
    }

    public Reservation createReservation(final ReservationRequest request, final Long memberId) {
        ReservationTime time = reservationTimeRepository.findById(request.timeId())
                .orElseThrow(() -> new ReservationTimeNotFoundException("요청한 id와 일치하는 예약 시간 정보가 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("요청한 id와 일치하는 맴버 정보가 없습니다."));
        Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> new ThemeNotFoundException("요청한 id와 일치하는 테마 정보가 없습니다."));
        ReservationInfo reservationInfo = new ReservationInfo(request.date(), time, theme);
        return reservationRepository.save(
                Reservation.createUpcomingReservationWithUnassignedId(member, reservationInfo)
        );
    }
}

