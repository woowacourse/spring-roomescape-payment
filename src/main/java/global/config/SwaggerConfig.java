package global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("커머스 프로젝트 API")
                        .description("상품을 등록하고, 상품을 장바구니에 담는 기능을 제공합니다.")
                        .version("1.0.0"));
    }
}
