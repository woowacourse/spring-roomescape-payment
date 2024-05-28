package roomescape.reservation.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.IllegalRequestException;
import roomescape.global.exception.InternalServerException;
import roomescape.member.service.MemberService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.Reservations;
import roomescape.reservation.dto.MemberReservationAddRequest;
import roomescape.reservation.dto.MemberReservationResponse;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.WaitingResponse;
import roomescape.theme.service.ThemeService;
import roomescape.time.service.ReservationTimeService;

@Service
public class ReservationService {

    private final MemberService memberService;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;
    private final ReservationRepository reservationRepository;
    private final RestClient restClient;

    @Value("${third-party-api.payment.secret-key}")
    private String secretKey;

    public ReservationService(MemberService memberService, ReservationTimeService reservationTimeService,
                              ThemeService themeService, ReservationRepository reservationRepository,
                              RestClient restClient) {
        this.memberService = memberService;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
        this.reservationRepository = reservationRepository;
        this.restClient = restClient;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllReservation() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllByMemberAndThemeAndPeriod(Long memberId, Long themeId, LocalDate dateFrom,
                                                                      LocalDate dateTo) {
        return reservationRepository.findByMemberAndThemeAndPeriod(memberId, themeId, dateFrom, dateTo).stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberReservationResponse> findMemberReservationWithWaitingStatus(Long memberId) {
        return reservationRepository.findByMemberIdWithWaitingStatus(memberId).stream()
                .map(MemberReservationResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WaitingResponse> findReservationsOnWaiting() {
        return reservationRepository.findReservationOnWaiting().stream()
                .map(WaitingResponse::new)
                .toList();
    }

    @Transactional
    public ReservationResponse saveMemberReservation(Long memberId, MemberReservationAddRequest request) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + new String(encodedBytes))
                .body(request.extractPaymentInformation())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new IllegalRequestException("결제 승인 요청 정보가 잘못되었습니다");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new InternalServerException("결제 승인 도중 알 수 없는 예외가 발생했습니다");
                })
                .toBodilessEntity();

        validateMemberReservationNotExistInSlot(memberId, request);

        Reservation newReservation = Reservation.createNewReservation(
                memberService.findById(memberId),
                new ReservationDate(request.date()),
                reservationTimeService.findById(request.timeId()),
                themeService.findById(request.themeId())
        );

        Reservation saved = reservationRepository.save(newReservation);
        return new ReservationResponse(saved);
    }

    private void validateMemberReservationNotExistInSlot(Long memberId, MemberReservationAddRequest request) {
        Reservations sameSlotReservations = new Reservations(reservationRepository.findByDateAndTimeAndTheme(
                request.date(),
                request.timeId(),
                request.themeId()
        ));

        if (sameSlotReservations.hasReservationMadeBy(memberId)) {
            throw new IllegalRequestException("해당 아이디로 진행되고 있는 예약(대기)이 이미 존재합니다");
        }
    }

    @Transactional
    public void removeReservation(long id) {
        reservationRepository.deleteById(id);
    }
}
