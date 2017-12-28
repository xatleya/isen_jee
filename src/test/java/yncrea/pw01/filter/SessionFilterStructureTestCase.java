package yncrea.pw01.filter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import yncrea.pw01.utils.TestUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import static org.assertj.core.api.StrictAssertions.assertThat;

@RunWith(JUnit4.class)
public class SessionFilterStructureTestCase {

    @Test
    public void shouldImplementFilter(){
        //GIVEN
        SessionFilter filter = new SessionFilter();
        //WHEN
        //THEN
        assertThat(filter).isInstanceOf(Filter.class);
    }

    @Test
    public void shouldHaveWebFilterAnnotation() {
        //GIVEN
        Class<SessionFilter> clazz = SessionFilter.class;
        WebFilter[] annotations = clazz.getAnnotationsByType(WebFilter.class);
        //WHEN
        //THEN
        assertThat(annotations).hasSize(1);
        assertThat(annotations[0].urlPatterns()).containsOnly("/*");
    }



    @Test
    public void shouldHaveInitMethod() throws NoSuchMethodException {
        TestUtils.shouldHaveMethod(SessionFilter.class,"init", FilterConfig.class);
    }

    @Test
    public void shouldHaveDoFilterMethod() throws NoSuchMethodException {
        TestUtils.shouldHaveMethod(SessionFilter.class,"doFilter", ServletRequest.class, ServletResponse.class,FilterChain.class);
    }

    @Test
    public void shouldHaveDestroyMethod() throws NoSuchMethodException {
        TestUtils.shouldHaveMethod(SessionFilter.class,"destroy");
    }

}
