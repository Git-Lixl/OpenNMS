package org.opennms.web;

import javax.servlet.*;
import java.io.IOException;

public class BlackListFilter implements Filter
{
    private String blacklistedip;

    public void init(final FilterConfig filterConfig) throws ServletException
    {
        this.blacklistedip = filterConfig.getInitParameter("blacklistedip");
    }

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException
    {
        if (!request.getRemoteAddr().equals(this.blacklistedip))
        {
            filterChain.doFilter(request, response);
        }
    }

    public void destroy()
    {
        // nothing
    }
}
