package roomescape.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("방탈출 예약 서비스 API 문서")
                .description("""       
                        ## 에러처리 형식
                        
                        에러 응답에 대한 정보입니다.
                              
                        ### 기본 JSON 에러 형식
                                                
                        ```
                        {
                          "status": "BAD_REQUEST",
                          "message": "잘못된 요청입니다."
                        }
                        ```
                        
                        ### 상태코드
                        
                        기본적으로 반환하는 상태코드와 메시지는 다음과 같습니다. 메시지는 고정되어 있는 것이 아니라, 에러 상황에 맞춰 다른 메시지를 반환할 수 있습니다.
                        
                        |상태코드|메시지|
                        |------|---|
                        |[400] BAD_REQUEST|잘못된 요청입니다.|
                        |[401] UNAUTHORIZED|인증되지 않았습니다.|
                        |[404] NOT_FOUND|리소스를 찾을 수 없습니다.|
                        |[403] UNAUTHORIZED|인증되지 않았습니다.|
                        |[500] INTERNAL_SERVER_ERROR|서버 오류입니다.|
                        
                        ### 결제 에러 JSON 형식
                        
                        결제사에서 제공해주는 형식에 맞춰 응답을 반환합니다.
                        
                        ```
                        {
                           "code": "NOT_FOUND_PAYMENT",
                           "message": "존재하지 않는 결제 입니다."
                        }
                        ```
                        """)
                .version("1.0.0");
    }
}
