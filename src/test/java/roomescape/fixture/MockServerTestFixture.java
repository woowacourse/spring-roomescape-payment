package roomescape.fixture;

import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

public class MockServerTestFixture {
    public static final String BASE_URL = "https://api.tosspayments.com/v1/payments";

    public static final RestClient.Builder BUILDER = RestClient.builder().baseUrl(BASE_URL);

    public static final MockRestServiceServer SERVER = MockRestServiceServer
            .bindTo(BUILDER)
            .build();
}
