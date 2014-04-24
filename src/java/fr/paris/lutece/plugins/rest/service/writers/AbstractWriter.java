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
package fr.paris.lutece.plugins.rest.service.writers;

import fr.paris.lutece.plugins.rest.service.formatters.IFormatter;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.OutputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyWriter;


/**
 *
 * This class handle the action of formatting an object E to String with the appropriate
 * MediaType.
 * @param <E> the <i>real</i> object class
 *
 */
public abstract class AbstractWriter<E> implements MessageBodyWriter<List<E>>
{
    // ERROR CODE
    private static final String ERROR_CODE = "1";

    // MESSAGES
    private static final String MESSAGE_NO_RESOURCE = "No resource";

    // PROPERTIES
    private static final String PROPERTY_WRITER_ENCODING = "rest.writer.encoding";

    // VARIABLES
    private Map<String, IFormatter<E>> _mapFormatters;

    /**
     * Set the formatters
     * @param mapFormatters the formatters
     */
    public void setFormatters( Map<String, IFormatter<E>> mapFormatters )
    {
        _mapFormatters = mapFormatters;
    }

    /**
     * {@inheritDoc}
     */
    public long getSize( List<E> resource, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType )
    {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public void writeTo( List<E> listResources, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream )
        throws IOException, WebApplicationException
    {
        if ( _mapFormatters != null )
        {
            String strContent = StringUtils.EMPTY;
            IFormatter<E> formatter = _mapFormatters.get( mediaType.toString(  ) );

            if ( formatter == null )
            {
                // Should not happen
                throw new WebApplicationException( Status.UNSUPPORTED_MEDIA_TYPE );
            }

            if ( listResources != null )
            {
                if ( listResources.size(  ) == 1 )
                {
                    E resource = listResources.get( 0 );

                    if ( resource != null )
                    {
                        strContent = formatter.format( resource );
                    }
                    else
                    {
                        strContent = formatter.formatError( ERROR_CODE, MESSAGE_NO_RESOURCE );
                    }
                }
                else if ( listResources.size(  ) > 1 )
                {
                    strContent = formatter.format( listResources );
                }
                else
                {
                    strContent = formatter.formatError( ERROR_CODE, MESSAGE_NO_RESOURCE );
                }
            }
            else
            {
                strContent = formatter.formatError( ERROR_CODE, MESSAGE_NO_RESOURCE );
            }

            if ( StringUtils.isBlank( strContent ) )
            {
                // Should not happen
                throw new WebApplicationException( Status.UNSUPPORTED_MEDIA_TYPE );
            }

            String strEncoding = AppPropertiesService.getProperty( PROPERTY_WRITER_ENCODING );
            entityStream.write( strContent.getBytes( strEncoding ) );
        }
        else
        {
            // Not well configured
            throw new WebApplicationException( Status.INTERNAL_SERVER_ERROR );
        }
    }
}
