package br.com.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;

@WebFilter(urlPatterns = {"/*"})
public class FilterAutenticacao implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialize resources if needed
        JPAUtil.getEntityManager();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        System.out.println("Invocando o Filter");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(false); // Get session, don't create one if it doesn't exist
        Pessoa usuarioLogado = (session != null) ? (Pessoa) session.getAttribute("usuarioLogado") : null;

        String url = req.getServletPath();

        try {
            // Check if the user is logged in and trying to access a protected page
            if (!url.equalsIgnoreCase("/index.jsf") && usuarioLogado == null) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsf");
                dispatcher.forward(request, response);
                return;
            } 
            // Proceed with the request
            chain.doFilter(request, response);
        } catch (Exception e) {
            // Handle exceptions and redirect to an error page if necessary
            e.printStackTrace(); // Log the exception for debugging
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsf"); // Custom error page
            dispatcher.forward(request, response);
        }
    }

    @Override
    public void destroy() {
        // Clean up resources if needed
    }
}
