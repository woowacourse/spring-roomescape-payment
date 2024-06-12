package roomescape.reservation.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.client.PaymentClient;
import roomescape.member.domain.Member;
import roomescape.member.dto.LoginMemberInToken;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.repository.PaymentRepository;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@Service
public class PaymentService {
    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final ReservationTimeRepository reservationTimeRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository,
                          ReservationRepository reservationRepository, ThemeRepository themeRepository,
                          MemberRepository memberRepository, ReservationTimeRepository reservationTimeRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.reservationTimeRepository = reservationTimeRepository;
    }

    @Transactional
    public ReservationResponse purchase(ReservationCreateRequest request, LoginMemberInToken loginMemberInToken) {
        Reservation reservation = getValidatedReservation(request.date(), request.themeId(), request.timeId(),
                loginMemberInToken);

        reservationRepository.save(reservation);
        if (reservation.isSuccess()) {
            paymentClient.confirm(getAuthorizations(), request.toPaymentRequest());
            Payment payment = new Payment(request.amount(), request.paymentKey(), reservation);
            paymentRepository.save(payment);
        }

        return ReservationResponse.toResponse(reservation);
    }

    private String getAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((WIDGET_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

    private Reservation getValidatedReservation(LocalDate date,
                                                long themeId,
                                                long timeId,
                                                LoginMemberInToken loginMemberInToken) {
        ReservationTime reservationTime = reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약 시간입니다."));

        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 테마입니다."));

        Member member = memberRepository.findById(loginMemberInToken.id())
                .orElseThrow(() -> new IllegalArgumentException("회원 인증에 실패했습니다."));

        boolean reserved = reservationRepository.existsByDateAndReservationTimeIdAndThemeId(date, timeId, themeId);
        if (reserved) {
            return new Reservation(member, date, theme, reservationTime, Status.WAITING);
        }

        return new Reservation(member, date, theme, reservationTime, Status.SUCCESS);
    }
}
