package roomescape.core.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.core.exception.PaymentException;

public class PaymentErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    // TODO: 토스에서 제공한 메시지를 그대로 넘겨주지 말고, 클라이언트에 필요한 메시지로 변환하여 제공하기
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
            throw new RuntimeException(e.getMessage());
        }
        return jsonObject;
    }
}
