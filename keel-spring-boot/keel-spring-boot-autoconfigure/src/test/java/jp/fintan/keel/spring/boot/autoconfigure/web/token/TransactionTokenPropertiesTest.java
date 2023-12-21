package jp.fintan.keel.spring.boot.autoconfigure.web.token;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.util.Lists;

import org.junit.jupiter.api.Test;

class TransactionTokenPropertiesTest {

    private final TransactionTokenProperties properties = new TransactionTokenProperties();

    @Test
    void defaultEnabled() {
        assertThat(properties.isEnabled()).isTrue();
    }

    @Test
    void customEnabled() {
        properties.setEnabled(false);
        assertThat(properties.isEnabled()).isFalse();
    }

    @Test
    void defaultPathPatterns() {
        assertThat(properties.getPathPatterns()).isEmpty();
    }

    @Test
    void customPathPatterns() {
        properties.setPathPatterns(Lists.newArrayList("/user/**", "/item/**"));
        assertThat(properties.getPathPatterns()).containsExactly("/user/**", "/item/**");
    }

    @Test
    void defaultExcludePathPatterns() {
        assertThat(properties.getExcludePathPatterns()).isEmpty();
    }

    @Test
    void customExcludePathPatterns() {
        properties.setExcludePathPatterns(Lists.newArrayList("/admin/**", "/secure/**"));
        assertThat(properties.getExcludePathPatterns()).containsExactly("/admin/**", "/secure/**");
    }
}
