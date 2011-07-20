/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.rest.service.security;

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.security.SecurityUtil;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * SimpleHashAuthenticator
 */
public class SimpleHashAuthenticator implements RequestAuthenticator
{
    private static final String HEADER_SIGNATURE = "Lutece REST Signature";
    private static final String PROPERTY_SIMPLE_HASH_SECRET = "rest.simpleHash.secret";
    
    List<String> _listSignatureElements;
    
    public void setSignatureElements( List<String> list )
    {
        _listSignatureElements = list;
    }
    
    /**
     * {@inheritDoc }
     */
    public boolean isRequestAuthenticated( HttpServletRequest request )
    {
        String strHash1 = request.getHeader( HEADER_SIGNATURE );

        // no signature
        if( strHash1 == null )
        {
            return false;
        }
        
        List<String> listElements = new ArrayList<String>();
        for( String strParameter : _listSignatureElements )
        {
            String strValue = request.getParameter( strParameter );
            if( strValue != null )
            {
                listElements.add( strValue );
            }
        }
        
        String strHash2 = buildSignature( listElements );
        return strHash1.equals( strHash2 );
       
    }
    
    /**
     * {@inheritDoc }
     */
    public void authenticateRequest( GetMethod method , List<String> elements )
    {
        String signature = buildSignature( elements );
        Header header = new Header( HEADER_SIGNATURE , signature );
        method.setRequestHeader( header );
    }
    
    /**
     * Create a signature  
     * @param listElements The list of elements that part of the hash 
     * @return A signature as an Hexadecimal Hash
     */
    private String buildSignature( List<String> listElements )
    {
        StringBuilder sb = new StringBuilder();
        for( String strElement : listElements )
        {
            sb.append( strElement );
        }
        
        sb.append( AppPropertiesService.getProperty( PROPERTY_SIMPLE_HASH_SECRET) );
        return SecurityUtil.sha1( sb.toString() );
    }
    
}
