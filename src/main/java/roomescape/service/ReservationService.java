package roomescape.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import roomescape.domain.*;
import roomescape.domain.repository.MemberRepository;
import roomescape.domain.repository.ReservationRepository;
import roomescape.domain.repository.ReservationTimeRepository;
import roomescape.domain.repository.ThemeRepository;
import roomescape.service.exception.PastReservationException;
import roomescape.service.request.AdminSearchedReservationDto;
import roomescape.service.request.ReservationSaveDto;
import roomescape.service.response.ReservationDto;
import roomescape.service.specification.ReservationSpecification;

import java.util.List;
import java.util.NoSuchElementException;
import roomescape.infrastructure.payment.PaymentManager;
import roomescape.service.request.PaymentApproveDto;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final PaymentManager paymentManager;

    public ReservationService(ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            MemberRepository memberRepository,
            PaymentManager paymentManager) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.paymentManager = paymentManager;
    }

    public ReservationDto save(ReservationSaveDto reservationSaveDto) {
        Member member = findMember(reservationSaveDto.memberId());
        ReservationDate date = new ReservationDate(reservationSaveDto.date());
        ReservationTime time = findTime(reservationSaveDto.timeId());
        Theme theme = findTheme(reservationSaveDto.themeId());
        Reservation reservation = new Reservation(member, date, time, theme);
        validatePastReservation(reservation);
        validateDuplication(date, reservationSaveDto.timeId(), reservationSaveDto.themeId());

        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationDto(savedReservation);
    }

    public ReservationDto save(ReservationSaveDto reservationSaveDto, PaymentApproveDto paymentApproveDto) {
        paymentManager.approve(paymentApproveDto);
        return save(reservationSaveDto);
    }

    private ReservationTime findTime(Long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new NoSuchElementException("예약에 대한 예약시간이 존재하지 않습니다."));
    }

    private Theme findTheme(Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new NoSuchElementException("예약에 대한 테마가 존재하지 않습니다."));
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(memberId + "|예약에 대한 사용자가 존재하지 않습니다."));
    }

    private void validatePastReservation(Reservation reservation) {
        if (reservation.isPast()) {
            throw new PastReservationException();
        }
    }

    private void validateDuplication(ReservationDate date, Long timeId, Long themeId) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(date, timeId, themeId)) {
            throw new IllegalArgumentException("이미 존재하는 예약 정보 입니다.");
        }
    }

    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<ReservationDto> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationDto::new)
                .toList();
    }

    public List<ReservationDto> findAllSearched(AdminSearchedReservationDto request) {
        Specification<Reservation> reservationSpecification = new ReservationSpecification().generate(request);
        List<Reservation> searchedReservations = reservationRepository.findAll(reservationSpecification);

        return searchedReservations.stream()
                .map(ReservationDto::new)
                .toList();
    }

    public List<ReservationDto> findByMemberId(Long id) {
        List<Reservation> reservations = reservationRepository.findAllByMemberId(id);

        return reservations.stream()
                .map(ReservationDto::new)
                .toList();
    }
}
