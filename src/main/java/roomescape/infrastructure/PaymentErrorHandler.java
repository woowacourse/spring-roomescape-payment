package roomescape.infrastructure;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.core.exception.PaymentException;

public class PaymentErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is5xxServerError()) {
            throw new PaymentException(response.getStatusCode(), response.getStatusText());
        }
        if (response.getStatusCode().is4xxClientError()) {
            Reader reader = new InputStreamReader(response.getBody(), StandardCharsets.UTF_8);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = getJsonObject(parser, reader);
            response.getBody().close();
            throw new PaymentException(response.getStatusCode(), jsonObject.get("message").toString());
        }
    }

    private JSONObject getJsonObject(final JSONParser parser, final Reader reader) throws IOException {
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) parser.parse(reader);
        } catch (ParseException e) {
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 API 호출이 실패했습니다.");
        }
        return jsonObject;
    }
}
