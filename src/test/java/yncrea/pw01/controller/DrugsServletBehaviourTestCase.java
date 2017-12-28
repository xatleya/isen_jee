package yncrea.pw01.controller;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import yncrea.pw01.model.Drug;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.StrictAssertions.tuple;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class DrugsServletBehaviourTestCase {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private ServletContext context;

    @Mock
    private HttpSession session;

    @Before
    public void beforeTests() {
        when(request.getSession()).thenReturn(session);
        when(request.getServletContext()).thenReturn(context);
        when(context.getContextPath()).thenReturn("contextPath");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void shouldNotHaveDrugsBeforeInit() throws NoSuchFieldException, IllegalAccessException, ServletException {
        //GIVEN
        Class<DrugsServlet> clazz = DrugsServlet.class;
        Field drugsField = clazz.getDeclaredField("drugs");
        drugsField.setAccessible(true);
        ParameterizedType listType = (ParameterizedType) drugsField.getGenericType();
        DrugsServlet servlet = new DrugsServlet();
        //WHEN
        Object drugs = drugsField.get(servlet);
        //THEN
        assertThat(drugs).isNull();
        assertThat(drugsField.getType()).isEqualTo(List.class);
        assertThat(listType.getActualTypeArguments()[0]).isEqualTo(Drug.class);
        
    }

    @Test
    public void shouldHaveDrugsAfterInit() throws NoSuchFieldException, IllegalAccessException, ServletException {
        //GIVEN
        Class<DrugsServlet> clazz = DrugsServlet.class;
        Field drugsField = clazz.getDeclaredField("drugs");
        drugsField.setAccessible(true);
        DrugsServlet servlet = new DrugsServlet();
        ((HttpServlet)servlet).init();
        //WHEN
        Object drugs = drugsField.get(servlet);
        //THEN
        assertThat(drugs).isInstanceOf(List.class);
        List<?> list = (List<?>) drugs;
        assertThat(list).hasSize(2);
        assertThat(list.get(0)).isInstanceOf(Drug.class);
        assertThat(list.get(1)).isInstanceOf(Drug.class);
        List<Drug> drugsList = (List<Drug>) drugs;
        Assertions.assertThat(drugsList).extracting("name", "lab").containsExactly(tuple("Drug1", "Lab1"), tuple("Drug2", "Lab2"));
    }

    @Test
    public void shouldSetDrugsInRequestThenDispatch() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException {
        //GIVEN

        DrugsServlet servlet = new DrugsServlet();
        ((HttpServlet)servlet).init();
        Field drugsField = DrugsServlet.class.getDeclaredField("drugs");
        drugsField.setAccessible(true);
        List<Drug> drugs = (List<Drug>) drugsField.get(servlet);
        //WHEN
        servlet.doGet(request, response);
        //THEN
        verify(request,times(1)).setAttribute(eq("drugs"),eq(drugs));
        verify(request, times(1)).getRequestDispatcher(eq("DrugsList.jsp"));
        verify(dispatcher,times(1)).forward(eq(request),eq(response));
    }

    @Test
    public void shouldSaveDrugThenRedirect() throws NoSuchFieldException, ServletException, IllegalAccessException, IOException {
        //GIVEN
        when(request.getParameter(eq("name"))).thenReturn("Doliprane");
        when(request.getParameter(eq("lab"))).thenReturn("Sanofi");
        DrugsServlet servlet = new DrugsServlet();
        ((HttpServlet)servlet).init();
        Field drugsField = DrugsServlet.class.getDeclaredField("drugs");
        drugsField.setAccessible(true);
        //WHEN
        servlet.doPost(request, response);
        List<Drug> drugs = (List<Drug>) drugsField.get(servlet);
        //THEN
        assertThat(drugs).hasSize(3);
        assertThat(drugs.get(2).getName()).isEqualTo("Doliprane");
        assertThat(drugs.get(2).getLab()).isEqualTo("Sanofi");
        verify(response,times(1)).sendRedirect(eq("contextPath/drugs"));
    }

}
