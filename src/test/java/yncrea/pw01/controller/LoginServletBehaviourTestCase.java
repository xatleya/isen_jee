package yncrea.pw01.controller;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import yncrea.pw01.model.Pharmacist;

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

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.assertj.core.api.StrictAssertions.tuple;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class LoginServletBehaviourTestCase {

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
        when(request.getParameter(eq("login"))).thenReturn("loginTest");
        when(request.getParameter(eq("password"))).thenReturn("passwordTest");
        when(context.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void shouldNotHavePharmacistsBeforeInit() throws NoSuchFieldException, IllegalAccessException, ServletException {
        //GIVEN
        Class<LoginServlet> clazz = LoginServlet.class;
        Field pharmacistsField = clazz.getDeclaredField("pharmacists");
        pharmacistsField.setAccessible(true);
        ParameterizedType listType = (ParameterizedType) pharmacistsField.getGenericType();
        LoginServlet servlet = new LoginServlet();
        //WHEN
        Object pharmacists = pharmacistsField.get(servlet);
        //THEN
        assertThat(pharmacists).isNull();
        assertThat(pharmacistsField.getType()).isEqualTo(List.class);
        assertThat(listType.getActualTypeArguments()[0]).isEqualTo(Pharmacist.class);
        
    }

    @Test
    public void shouldHavePharmacistsAfterInit() throws NoSuchFieldException, IllegalAccessException, ServletException {
        //GIVEN
        Class<LoginServlet> clazz = LoginServlet.class;
        Field pharmacistsField = clazz.getDeclaredField("pharmacists");
        pharmacistsField.setAccessible(true);
        LoginServlet servlet = new LoginServlet();
        ((HttpServlet)servlet).init();
        //WHEN
        Object pharmacists = pharmacistsField.get(servlet);
        //THEN
        assertThat(pharmacists).isInstanceOf(List.class);
        List<?> list = (List<?>) pharmacists;
        Assertions.assertThat(list).hasSize(2);
        assertThat(list.get(0)).isInstanceOf(Pharmacist.class);
        assertThat(list.get(1)).isInstanceOf(Pharmacist.class);
        List<Pharmacist> pharmacistsList = (List<Pharmacist>) pharmacists;
        Assertions.assertThat(pharmacistsList).extracting("login", "password").containsExactly(tuple("pharm1", "password1"), tuple("pharm2", "password2"));
    }

    @Test
    public void shouldLogoutIfUrlIsGood() throws ServletException, IOException {
        //GIVEN
        when(request.getQueryString()).thenReturn("logout");
        LoginServlet servlet = new LoginServlet();
        ((HttpServlet)servlet).init();
        //WHEN
        servlet.doGet(request, response);
        //THEN
        verify(session, times(1)).removeAttribute(eq("loggedPharmacist"));
        verify(response, times(1)).sendRedirect(eq("contextPath/"));
    }

    @Test
    public void shouldNotLogoutIfUrlIsBad() throws ServletException, IOException {
        //GIVEN
        when(request.getQueryString()).thenReturn("someUrl");
        LoginServlet servlet = new LoginServlet();
        ((HttpServlet)servlet).init();
        //WHEN
        servlet.doGet(request,response);
        //THEN
        verify(session,never()).removeAttribute(eq("loggedPharmacist"));
        verify(response,never()).sendRedirect(eq("contextPath/"));
    }

    @Test
    public void shouldNotLogInIfCredentialsAreWrong() throws ServletException, IOException {
        //GIVEN
        LoginServlet servlet = new LoginServlet();
        ((HttpServlet)servlet).init();
        //WHEN
        servlet.doPost(request, response);
        //THEN
        verify(request,times(1)).setAttribute(eq("loginError"), eq("Invalid credentials!"));
        verify(context,times(1)).getRequestDispatcher(eq("/index.jsp"));
        verify(dispatcher,times(1)).forward(eq(request), eq(response));
    }

    @Test
    public void shouldLogInIfCredentialsAreGood() throws ServletException, IOException {
        //GIVEN
        when(request.getParameter(eq("login"))).thenReturn("pharm1");
        when(request.getParameter(eq("password"))).thenReturn("password1");
        LoginServlet servlet = new LoginServlet();
        ((HttpServlet)servlet).init();
        //WHEN
        servlet.doPost(request, response);
        //THEN
        verify(request,times(1)).removeAttribute(eq("loginError"));
        verify(session,times(1)).setAttribute(eq("loggedPharmacist"),eq (new Pharmacist("pharm1", "password1")));
        verify(response,times(1)).sendRedirect(eq("contextPath/drugs"));

    }

}
