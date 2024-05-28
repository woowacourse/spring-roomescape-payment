package roomescape.controller.api;

import jakarta.validation.Valid;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.CreateReservationResponse;
import roomescape.controller.dto.CreateUserReservationRequest;
import roomescape.controller.dto.CreateUserReservationStandbyRequest;
import roomescape.controller.dto.FindMyReservationResponse;
import roomescape.domain.member.Member;
import roomescape.global.argumentresolver.AuthenticationPrincipal;
import roomescape.global.exception.RoomescapeException;
import roomescape.service.UserReservationService;

@RestController
@RequestMapping("/reservations")
public class UserReservationController {

    @Value("${toss-payment.test-secret-key}")
    private String secretKey;

    private final UserReservationService userReservationService;

    public UserReservationController(UserReservationService userReservationService) {
        this.userReservationService = userReservationService;
    }

    @PostMapping
    public ResponseEntity<CreateReservationResponse> save(
        @Valid @RequestBody CreateUserReservationRequest request,
        @AuthenticationPrincipal Member member) throws Exception {

        JSONObject obj = new JSONObject(Map.of(
            "orderId", request.orderId(),
            "amount", request.amount(),
            "paymentKey", request.paymentKey()
        ));

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        if (connection.getResponseCode() == 200) {
            CreateReservationResponse response = userReservationService.reserve(
                member.getId(),
                request.date(),
                request.timeId(),
                request.themeId()
            );

            return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
        } else {
            InputStream responseStream = connection.getErrorStream();
            // 결제 성공 및 실패 비즈니스 로직을 구현하세요.
            Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            String responseMessage = (String) jsonObject.get("message");

            throw new RoomescapeException(responseMessage);
        }
    }

    @PostMapping("/standby")
    public ResponseEntity<CreateReservationResponse> standby(
        @Valid @RequestBody CreateUserReservationStandbyRequest request,
        @AuthenticationPrincipal Member member) {

        CreateReservationResponse response = userReservationService.standby(
            member.getId(),
            request.date(),
            request.timeId(),
            request.themeId()
        );

        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
            .body(response);
    }

    @DeleteMapping("/standby/{id}")
    public ResponseEntity<Void> deleteStandby(@PathVariable Long id, @AuthenticationPrincipal Member member) {
        userReservationService.deleteStandby(id, member);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mine")
    public ResponseEntity<List<FindMyReservationResponse>> findMyReservations(@AuthenticationPrincipal Member member) {
        List<FindMyReservationResponse> response = userReservationService.findMyReservationsWithRank(member.getId());
        return ResponseEntity.ok(response);
    }
}
