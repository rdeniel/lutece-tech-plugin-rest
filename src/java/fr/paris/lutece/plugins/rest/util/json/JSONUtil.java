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
package fr.paris.lutece.plugins.rest.util.json;

import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import java.util.Map;


/**
 * JSON Utils
 */
public final class JSONUtil
{
    private static final int INDENT = 4;

    /** Private constructor */
    private JSONUtil(  )
    {
    }

    /**
     * Convert a model into a JSON formatted string
     * @param model The model
     * @return The JSON string
     */
    public static String model2Json( Map model )
    {
        JSONObject json = json( model );

        return json.toString( INDENT );
    }

    /**
     * Convert a model into an XML generated string
     * @param model The model
     * @return The XML string
     */
    public static String model2Xml( Map model )
    {
        JSONObject json = json( model );
        XMLSerializer serializer = new XMLSerializer(  );

        return serializer.write( json );
    }

    /**
     * Convert a model into a JSON object
     * @param model The model
     * @return The JSON Object
     */
    public static JSONObject json( Map model )
    {
        JSONObject json = new JSONObject(  );
        json.accumulateAll( model );

        return json;
    }

    /**
     * Format error as JSON
     * @param strMessage the error message
     * @param nCode The error code
     * @return The formatted string
     */
    public static String formatError( String strMessage, int nCode )
    {
        JSONObject json = new JSONObject(  );
        JSONObject detail = new JSONObject(  );
        detail.accumulate( "message", strMessage );
        detail.accumulate( "code", nCode );
        json.accumulate( "error", detail );

        return json.toString( INDENT );
    }
}
