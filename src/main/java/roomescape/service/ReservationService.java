package roomescape.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.payment.Payment;
import roomescape.domain.repository.*;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationDate;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.theme.Theme;
import roomescape.service.exception.PastReservationException;
import roomescape.service.request.AdminSearchedReservationDto;
import roomescape.service.request.PaymentCancelDto;
import roomescape.service.request.ReservationSaveDto;
import roomescape.service.response.PaymentDto;
import roomescape.service.response.ReservationDto;
import roomescape.service.specification.ReservationSpecification;

import java.util.List;
import java.util.NoSuchElementException;
import roomescape.infrastructure.payment.PaymentManager;
import roomescape.service.request.PaymentApproveDto;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final PaymentManager paymentManager;
    private final PaymentRepository paymentRepository;

    public ReservationService(ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            MemberRepository memberRepository,
            PaymentManager paymentManager, PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.paymentManager = paymentManager;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public ReservationDto save(ReservationSaveDto reservationSaveDto) {
        Reservation savedReservation = saveReservation(reservationSaveDto);

        return new ReservationDto(savedReservation);
    }

    @Transactional
    public ReservationDto save(ReservationSaveDto reservationSaveDto, PaymentApproveDto paymentApproveDto) {
        Reservation reservation = saveReservation(reservationSaveDto);
        validatePaymentAmount(reservation, paymentApproveDto.amount());
        PaymentDto paymentDto = paymentManager.approve(paymentApproveDto);
        savePayment(reservation, paymentDto);

        return new ReservationDto(reservation);
    }

    private void validatePaymentAmount(Reservation reservation, Long amount) {
        boolean isValidAmount = reservation.isPriceEqual(amount);
        if (!isValidAmount) {
            throw new IllegalArgumentException("테마 가격과 결제 금액이 일치하지 않습니다.");
        }
    }

    private Reservation saveReservation(ReservationSaveDto reservationSaveDto) {
        Member member = findMember(reservationSaveDto.memberId());
        ReservationDate date = new ReservationDate(reservationSaveDto.date());
        ReservationTime time = findTime(reservationSaveDto.timeId());
        Theme theme = findTheme(reservationSaveDto.themeId());
        Reservation reservation = new Reservation(member, date, time, theme);
        validatePastReservation(reservation);
        validateDuplication(date, reservationSaveDto.timeId(), reservationSaveDto.themeId());

        return reservationRepository.save(reservation);
    }

    private void savePayment(Reservation reservation, PaymentDto paymentDto) {
        Payment payment = new Payment(reservation, paymentDto.paymentKey(), paymentDto.orderId());
        Payment savedPayment = savePayment(payment);
        reservation.setPayment(savedPayment);
    }

    private Payment savePayment(Payment payment) {
        try {
            return paymentRepository.save(payment);
        } catch (Exception e) {
            paymentManager.cancel(payment.getPaymentKey(), new PaymentCancelDto("결제 정보 저장 중 오류가 발생했습니다."));
            throw e;
        }
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
        return reservationRepository.findAllByMemberId(id)
                .stream()
                .map(ReservationDto::new)
                .toList();
    }
}
