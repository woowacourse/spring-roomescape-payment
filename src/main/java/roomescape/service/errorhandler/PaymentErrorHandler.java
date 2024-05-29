package roomescape.service.errorhandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.global.exception.RoomescapeException;

public class PaymentErrorHandler implements ErrorHandler {

    private static final String MESSAGE = "message";

    @Override
    public void handle(HttpRequest request, ClientHttpResponse response) {
        try (InputStream responseStream = response.getBody();
            Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);
            throw new RoomescapeException((String) jsonObject.get(MESSAGE));
        } catch (IOException | ParseException e) {
            throw new RoomescapeException("결제 요청에 실패하였습니다.");
        }
    }
}
