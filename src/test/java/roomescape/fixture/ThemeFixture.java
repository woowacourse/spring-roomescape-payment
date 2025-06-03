package roomescape.fixture;

import org.springframework.test.util.ReflectionTestUtils;
import roomescape.domain.theme.Theme;

public class ThemeFixture {

    public static Theme CREATE_THEME_1(){
        Theme register = Theme.register("theme_1", "theme_test_description_1", "theme_test_image_1");
        ReflectionTestUtils.setField(register, "id", 1L);
        return register;
    }

    public static Theme CREATE_THEME_2(){
        Theme register = Theme.register("theme_2", "theme_test_description_2", "theme_test_image_2");
        ReflectionTestUtils.setField(register, "id", 2L);
        return register;
    }

    public static Theme CREATE_THEME_3(){
        Theme register = Theme.register("theme_3", "theme_test_description_3", "theme_test_image_3");
        ReflectionTestUtils.setField(register, "id", 3L);
        return register;
    }

    public static Theme CREATE_THEME_4(){
        Theme register = Theme.register("theme_4", "theme_test_description_4", "theme_test_image_4");
        ReflectionTestUtils.setField(register, "id", 4L);
        return register;
    }
}
