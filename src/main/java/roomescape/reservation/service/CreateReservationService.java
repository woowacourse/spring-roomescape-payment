package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.service.dto.LoginMember;
import roomescape.common.exception.DuplicatedException;
import roomescape.common.exception.EntityNotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.service.TossPaymentService;
import roomescape.payment.service.dto.ConfirmPaymentRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.service.dto.request.ReservationCreateRequest;
import roomescape.reservation.service.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.service.dto.response.ReservationResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

import java.time.LocalDateTime;

@Service
public class CreateReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final TossPaymentService tossPaymentService;

    public CreateReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            MemberRepository memberRepository,
            TossPaymentService tossPaymentService
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.tossPaymentService = tossPaymentService;
    }

    public ReservationResponse create(final ReservationCreateRequest request) {
        Reservation reservation = convertRequestToReservation(request);

        validateDuplicated(reservation);
        validateReservationDateTime(reservation);

        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponse.from(savedReservation);
    }

    private void validateDuplicated(Reservation reservation) {
        if (isAlreadyBooked(reservation)) {
            throw new DuplicatedException("중복되는 예약이 존재합니다.");
        }
    }

    private boolean isAlreadyBooked(Reservation reservation) {
        return reservationRepository.existsByReservationInformation(reservation.getReservationInformation());
    }

    private Reservation convertRequestToReservation(final ReservationCreateRequest request) {
        ReservationTime reservationTime = getReservationTime(request);
        Theme theme = getTheme(request);
        LoginMember loginMember = request.loginMember();
        Member member = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 회원입니다."));
        return new Reservation(member, request.date(), reservationTime, theme);
    }

    private Theme getTheme(final ReservationCreateRequest request) {
        Long themeId = request.themeId();
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 테마입니다."));
    }

    private ReservationTime getReservationTime(final ReservationCreateRequest request) {
        Long timeId = request.timeId();
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 예약 가능 시간입니다."));
    }

    private void validateReservationDateTime(Reservation reservation) {
        LocalDateTime now = LocalDateTime.now();
        if (reservation.isBefore(now)) {
            throw new IllegalArgumentException("과거 날짜의 예약은 생성할 수 없습니다.");
        }
    }

    @Transactional
    public ReservationResponse createWithPayment(ReservationWithPaymentRequest request, LoginMember loginMember) {
        ReservationCreateRequest reservationCreateRequest = ReservationCreateRequest.from(request, loginMember);

        ReservationResponse reservationResponse = create(reservationCreateRequest);
        tossPaymentService.postConfirmPayment(ConfirmPaymentRequest.from(request));

        return reservationResponse;
    }
}
