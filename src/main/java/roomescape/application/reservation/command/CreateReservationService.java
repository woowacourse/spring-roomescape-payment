package roomescape.application.reservation.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import roomescape.application.reservation.command.dto.CreateReservationCommand;
import roomescape.application.reservation.command.dto.CreateReservationWithPaymentCommand;
import roomescape.application.reservation.command.dto.PaymentCommand;
import roomescape.domain.member.Member;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.ReservationTimeRepository;
import roomescape.domain.reservation.repository.ThemeRepository;
import roomescape.infrastructure.error.exception.MemberException;
import roomescape.infrastructure.error.exception.PaymentException;
import roomescape.infrastructure.error.exception.ReservationException;
import roomescape.infrastructure.error.exception.ReservationTimeException;
import roomescape.infrastructure.error.exception.ThemeException;

@Service
@Transactional
public class CreateReservationService {

    private static final Logger log = LoggerFactory.getLogger(CreateReservationService.class);
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final Clock clock;
    private final RestClient restClient;

    public CreateReservationService(ReservationRepository reservationRepository,
                                    ReservationTimeRepository reservationTimeRepository,
                                    ThemeRepository themeRepository,
                                    MemberRepository memberRepository,
                                    Clock clock) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.clock = clock;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .build();
    }

    public Long reserve(CreateReservationCommand command) {
        Member member = getMember(command.memberId());
        ReservationTime time = getTime(command.timeId());
        Theme theme = getTheme(command.themeId());
        validateDuplicateReservation(command.date(), time, theme);
        Reservation reservation = new Reservation(member, command.date(), time, theme);
        reservation.validateReservable(LocalDateTime.now(clock));
        Reservation savedReservation = reservationRepository.save(reservation);
        return savedReservation.getId();
    }

    public Long reserve(CreateReservationWithPaymentCommand command) {
        approvePayment(command.getPaymentCommand());
        return reserve(command.toCreateWithoutPaymentCommand());
    }

    private void approvePayment(PaymentCommand command) {
        String secretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6" + ":";
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        restClient.post()
                .uri("/confirm")
                .header("Authorization", "Basic " + encodedKey)
                .body(command)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        JsonNode node = mapper.readTree(response.getBody());
                        if (node.has("message")) {
                            log.warn("code: {} message: {}", node.get("code").asText(), node.get("code").asText());
                            throw new PaymentException(node.get("message").asText());
                        }
                    } catch (JsonProcessingException ignored) {
                    }
                    throw new PaymentException("페이먼츠 예외");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new PaymentException("토스 서버 예외");
                })
                .body(String.class);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
    }

    private ReservationTime getTime(Long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new ReservationTimeException("존재하지 않는 예약 시간입니다."));
    }

    private Theme getTheme(Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new ThemeException("존재하지 않는 테마입니다."));
    }

    private void validateDuplicateReservation(LocalDate date, ReservationTime time, Theme theme) {
        boolean duplicated = reservationRepository.existsByDateAndTimeIdAndThemeId(date, time.getId(), theme.getId());
        if (duplicated) {
            throw new ReservationException("날짜와 시간이 중복된 예약이 존재합니다.");
        }
    }
}
