package roomescape.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class BasicHeaderGeneratorImpl implements HeaderGenerator {

    @Override
    public HttpHeaders generate() {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return headers;
    }
}
