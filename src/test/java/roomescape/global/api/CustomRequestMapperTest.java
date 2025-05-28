package roomescape.global.api;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomRequestMapperTest {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("""
            Map<String, Object>로 맵핑한다.
            - 객체의 필드 이름을 key로 맵핑
            - 객체의 값을 value로 맵핑
            """)
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
