package roomescape.global.converter;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomRequestMapperTest {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void 객체를_restClient에_필요한_바디로_변환한다() {
        //given
        record Request(Long orderId, String name) {

        }
        CustomRequestMapper customRequestMapper = new CustomRequestMapper(objectMapper);
        Request request = new Request(1L, "mimi");

        //when
        Map<String, Object> result = customRequestMapper.convertMap(request);

        //then
        assertThat(result).containsExactlyEntriesOf(Map.of(
                "orderId", 1L,
                "name", "mimi"
        ));
    }
}
