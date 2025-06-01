package roomescape.fixture;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

public class ServerClientFixture {

    public static final String BASE_URL = "https://api.tosspayments.com/v1/payments";

    public static final RestClient.Builder TEST_BUILDER = RestClient.builder()
            .baseUrl(BASE_URL);

    public static final MockRestServiceServer SERVER = MockRestServiceServer
            .bindTo(TEST_BUILDER)
            .build();

    public static final ObjectMapper MAPPER = new ObjectMapper();

}
