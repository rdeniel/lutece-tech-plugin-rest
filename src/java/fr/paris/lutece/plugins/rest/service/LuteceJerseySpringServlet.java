/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.rest.service;


import fr.paris.lutece.plugins.rest.service.mediatype.MediaTypeMapping;
import fr.paris.lutece.plugins.rest.service.mediatype.RestMediaTypes;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.signrequest.RequestAuthenticator;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * LuteceJerseySpringServlet : using {@link SpringContextService#getContext()} as context.
 * @see ServletContainer
 */
public class LuteceJerseySpringServlet extends ServletContainer
{
    private static final long serialVersionUID = 5686655395749077671L;
    private static final String BEAN_REQUEST_AUTHENTICATOR = "rest.requestAuthenticator";

    static final Logger LOGGER = Logger.getLogger( RestConstants.REST_LOGGER );


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext context = filterConfig.getServletContext();
        if ( WebApplicationContextUtils.getWebApplicationContext( context ) == null ) {
            //If we are not using spring's context loader, register this property needed by the jersey spring integration
            context.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, getContext( ) );
        }

        super.init(filterConfig);
    }

    /**
     * Gets the lutece spring context
     * @return the spring context
     */
    private ConfigurableApplicationContext getContext(  )
    {
        return (ConfigurableApplicationContext) SpringContextService.getContext(  );
    }

    /**
     * Checks if the request is authenticated. Sets {@link HttpServletResponse#SC_UNAUTHORIZED} if not,
     * calls {@link ServletContainer#doFilter(HttpServletRequest, HttpServletResponse, FilterChain)} otherwise.
     * @param request the HTTP request
     * @param response the response
     * @param chain the filter chain
     * @throws IOException exception if I/O error
     * @throws ServletException exception if servlet error
     */
    @Override
    public void doFilter( HttpServletRequest request, HttpServletResponse response, FilterChain chain )
        throws IOException, ServletException
    {
        if ( checkRequestAuthentification( request ) )
        {
            if( LOGGER.isDebugEnabled())
            {
                LOGGER.debug( "LuteceJerseySpringServlet processing request : " + request.getMethod() + " " + request.getContextPath() + request.getServletPath() );
            }
            super.doFilter( request, response, chain );
        }
        else
        {
            response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
        }
    }

    /**
     * Checks if the request is authenticated.
     * @param request the request
     * @return <code>true</code> if the request is authenticated, <code>false</code> otherwise.
     */
    private boolean checkRequestAuthentification( HttpServletRequest request )
    {
        RequestAuthenticator ra = (RequestAuthenticator) SpringContextService.getBean( BEAN_REQUEST_AUTHENTICATOR );

        return ra.isRequestAuthenticated( request );
    }
}
