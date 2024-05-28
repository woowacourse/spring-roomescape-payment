package roomescape.service.reservation;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.reservationdetail.ReservationDetailRepository;
import roomescape.domain.schedule.ReservationDate;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.ReservationTimeRepository;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.exception.InvalidMemberException;
import roomescape.exception.InvalidReservationException;
import roomescape.service.reservation.dto.AdminReservationRequest;
import roomescape.service.reservation.dto.PaymentRequest;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.reservation.dto.ReservationResponse;

import java.time.LocalDate;

@Service
@Transactional
public class ReservationCreateService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final ReservationDetailRepository reservationDetailRepository;
    private final RestClient restClient;

    public ReservationCreateService(ReservationRepository reservationRepository,
        ReservationTimeRepository reservationTimeRepository, ThemeRepository themeRepository,
        MemberRepository memberRepository, ReservationDetailRepository reservationDetailRepository, RestClient restClient) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.reservationDetailRepository = reservationDetailRepository;
        this.restClient = restClient;
    }

    public ReservationResponse createAdminReservation(AdminReservationRequest adminReservationRequest) {
        return createReservation(adminReservationRequest.timeId(), adminReservationRequest.themeId(),
            adminReservationRequest.memberId(), adminReservationRequest.date());
    }

    public ReservationResponse createMemberReservation(ReservationRequest reservationRequest, long memberId) {
        PaymentRequest request = new PaymentRequest(reservationRequest.paymentKey(), reservationRequest.orderId(), reservationRequest.amount());
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);
        Payment payment = restClient.post()
            .uri("/v1/payments/confirm")
            .contentType(APPLICATION_JSON)
            .header("Authorization", authorizations)
            .body(request)
            .retrieve()
            .body(Payment.class);

        return createReservation(reservationRequest.timeId(), reservationRequest.themeId(), memberId,
            reservationRequest.date());
    }

    private ReservationResponse createReservation(long timeId, long themeId, long memberId, LocalDate date) {
        ReservationDate reservationDate = ReservationDate.of(date);
        ReservationTime reservationTime = findTimeById(timeId);
        Theme theme = findThemeById(themeId);
        Member member = findMemberById(memberId);
        ReservationDetail reservationDetail = getReservationDetail(reservationDate, reservationTime, theme);
        ReservationStatus reservationStatus = determineStatus(reservationDetail, member);
        Reservation reservation = reservationRepository.save(new Reservation(member, reservationDetail, reservationStatus));

        return new ReservationResponse(reservation);
    }

    private ReservationTime findTimeById(long timeId) {
        return reservationTimeRepository.findById(timeId)
            .orElseThrow(() -> new InvalidReservationException("더이상 존재하지 않는 시간입니다."));
    }

    private Theme findThemeById(long themeId) {
        return themeRepository.findById(themeId)
            .orElseThrow(() -> new InvalidReservationException("더이상 존재하지 않는 테마입니다."));
    }

    private Member findMemberById(long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new InvalidMemberException("존재하지 않는 회원입니다."));
    }

    private ReservationDetail getReservationDetail(ReservationDate reservationDate, ReservationTime reservationTime, Theme theme) {
        Schedule schedule = new Schedule(reservationDate, reservationTime);
        return reservationDetailRepository.findByScheduleAndTheme(schedule, theme)
            .orElseGet(() -> reservationDetailRepository.save(new ReservationDetail(schedule, theme)));
    }

    private ReservationStatus determineStatus(ReservationDetail reservationDetail, Member member) {
        if (reservationRepository.existsByDetailIdAndMemberId(reservationDetail.getId(), member.getId())) {
            throw new InvalidReservationException("이미 예약(대기) 상태입니다.");
        }
        if (reservationRepository.existsByDetailIdAndStatus(reservationDetail.getId(), ReservationStatus.RESERVED)) {
            return ReservationStatus.WAITING;
        }
        return ReservationStatus.RESERVED;
    }
}


