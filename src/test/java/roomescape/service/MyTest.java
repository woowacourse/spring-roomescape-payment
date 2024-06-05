package roomescape.service;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

public class MyTest {

    @Test
    void test() {
        RestClient restClient = RestClient.create();

        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restClient.mutate()).build();
        mockServer.expect(requestTo("/greeting")).andRespond(withSuccess());

        // Test code that uses the above RestTemplate ...

        mockServer.verify();

    }
}
