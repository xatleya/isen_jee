package yncrea.pw01.filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import yncrea.pw01.model.Pharmacist;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class SessionFilterBehaviourTestCase {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

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
        when(context.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void shouldByPassIfUrlIsLogin() throws IOException, ServletException {
        //GIVEN
        when(request.getServletPath()).thenReturn("/login");
        Filter filter = new SessionFilter();
        //WHEN
        filter.doFilter(request, response, chain);
        //THEN
        verify(response, never()).sendRedirect(anyString());
        verify(chain, times(1)).doFilter(eq(request), eq(response));
    }

    @Test
    public void shouldByPassIfUrlIsIndexJsp() throws IOException, ServletException {
        //GIVEN
        when(request.getServletPath()).thenReturn("/index.jsp");
        Filter filter = new SessionFilter();
        //WHEN
        filter.doFilter(request,response,chain);
        //THEN
        verify(response,never()).sendRedirect(anyString());
        verify(chain,times(1)).doFilter(eq(request),eq(response));
    }

    @Test
    public void shouldByPassIfAlreadyLoggedIn() throws IOException, ServletException {
        //GIVEN
        when(session.getAttribute(eq("loggedPharmacist"))).thenReturn(new Pharmacist("pharm","pwd"));
        when(request.getServletPath()).thenReturn("/someUrl");
        Filter filter = new SessionFilter();
        //WHEN
        filter.doFilter(request,response,chain);
        //THEN
        verify(response,never()).sendRedirect(anyString());
        verify(chain,times(1)).doFilter(eq(request),eq(response));
    }

    @Test
    public void shouldRedirectToLogIn() throws IOException, ServletException {
        //GIVEN
        when(session.getAttribute(eq("loggedPharmacist"))).thenReturn(null);
        when(request.getServletPath()).thenReturn("/someUrl");
        Filter filter = new SessionFilter();
        //WHEN
        filter.doFilter(request,response,chain);
        //THEN
        verify(response,times(1)).sendRedirect("contextPath/");
        verify(chain,never()).doFilter(eq(request),eq(response));
    }



}
