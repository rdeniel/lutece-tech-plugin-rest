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
package fr.paris.lutece.plugins.rest.service.resourceinfo;

import fr.paris.lutece.plugins.rest.business.resourceinfo.IResourceInfo;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 * ResourceInfoManager
 *
 */
public final class ResourceInfoManager
{
    /**
     * Private constructor
     */
    private ResourceInfoManager(  )
    {
    }

    /**
     * Get the list of resource info
     * @return the list of resource info
     */
    public static List<IResourceInfoProvider> getProviders(  )
    {
        return SpringContextService.getBeansOfType( IResourceInfoProvider.class );
    }

    /**
     * Get the resource info
     * @param mapParams the map parameters
     * @return the resource info in format ( key - value )
     */
    public static List<IResourceInfo> getResourceInfo( Map<String, String> mapParams )
    {
        List<IResourceInfo> listResourceInfos = new ArrayList<IResourceInfo>(  );

        for ( IResourceInfoProvider provider : getProviders(  ) )
        {
            if ( provider.isInvoked( mapParams ) )
            {
                listResourceInfos.add( provider.getResourceInfo( mapParams ) );
            }
        }

        return listResourceInfos;
    }
}
