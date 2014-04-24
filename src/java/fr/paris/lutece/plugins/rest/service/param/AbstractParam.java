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
package fr.paris.lutece.plugins.rest.service.param;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


/**
 * Provides simple param parsing. Implementations have to override {@link #parse(String)} to transform the String to a concrete V object.
 * @param <V> the <i>real</i> object class
 */
public abstract class AbstractParam<V>
{
    private final String _strOriginalParam;
    private final V _value;

    /**
     * Builds this param and calls {@link #parse(String)}
     * @param strParam the param to parse
     * @throws WebApplicationException if an exception occured, with preconfigured message {@link #getErrorMessage(String, Throwable)}
     */
    public AbstractParam( String strParam ) throws WebApplicationException
    {
        this._strOriginalParam = strParam;

        try
        {
            this._value = parse( strParam );
        }
        catch ( Throwable e )
        {
            throw new WebApplicationException( onError( strParam, e ) );
        }
    }

    /**
     * The value of the string {@link #getOriginalParam()}
     * @return the object value
     */
    public V getValue(  )
    {
        return _value;
    }

    /**
     * Gets the original string param
     * @return the string
     */
    public String getOriginalParam(  )
    {
        return _strOriginalParam;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String toString(  )
    {
        return _value.toString(  );
    }

    /**
     * Builds a reponse when an error occurs with {@link Status#BAD_REQUEST} as http status.
     * @param strParam the string causing the error
     * @param e the exception
     * @return th reponse
     */
    protected Response onError( String strParam, Throwable e )
    {
        return Response.status( Status.BAD_REQUEST ).entity( getErrorMessage( strParam, e ) ).build(  );
    }

    /**
     * Gets an error message
     * @param strParam string to parse
     * @param e error to handle
     * @return error message
     */
    protected String getErrorMessage( String strParam, Throwable e )
    {
        return "Parameter : " + strParam + " cannot be parsed : " + e.getMessage(  );
    }

    /**
     * Parse the string to its real value
     * @param strParam the string to parse
     * @return real object value
     * @throws Throwable if occurs
     */
    protected abstract V parse( String strParam ) throws Throwable;
}
