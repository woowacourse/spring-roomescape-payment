package roomescape.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class JacksonConfig implements WebMvcConfigurer {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        JavaTimeModule module = new JavaTimeModule();
        ObjectMapper mapper = new ObjectMapper();

        configureDate(module);
        configureTime(module);

        mapper.registerModule(module);
        converters.add(0, new MappingJackson2HttpMessageConverter(mapper));
    }

    private void configureDate(final JavaTimeModule module) {
        LocalDateSerializer localDateSerializer = new LocalDateSerializer(DATE_FORMATTER);
        LocalDateDeserializer localDateDeserializer = new LocalDateDeserializer(DATE_FORMATTER);

        module.addSerializer(LocalDate.class, localDateSerializer);
        module.addDeserializer(LocalDate.class, localDateDeserializer);
    }

    private void configureTime(final JavaTimeModule module) {
        LocalTimeSerializer localTimeSerializer = new LocalTimeSerializer(TIME_FORMATTER);
        LocalTimeDeserializer localTimeDeserializer = new LocalTimeDeserializer(TIME_FORMATTER);

        module.addSerializer(LocalTime.class, localTimeSerializer);
        module.addDeserializer(LocalTime.class, localTimeDeserializer);
    }
}
