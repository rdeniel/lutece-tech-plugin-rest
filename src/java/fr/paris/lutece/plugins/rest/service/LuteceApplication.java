package fr.paris.lutece.plugins.rest.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.ws.rs.Path;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import fr.paris.lutece.plugins.rest.service.mediatype.MediaTypeMapping;
import fr.paris.lutece.plugins.rest.service.mediatype.RestMediaTypes;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import static fr.paris.lutece.plugins.rest.service.LuteceJerseySpringServlet.LOGGER;

import javax.ws.rs.core.MediaType;


public class LuteceApplication extends ResourceConfig {

    public LuteceApplication () {
        //Automatically register all beans with @Path annotation because
        //this is was the previous versions of plugin-rest did
        Map<String, Object> map = SpringContextService.getContext().getBeansWithAnnotation(Path.class);
        for (Object o: map.values()) {
            register(o.getClass());
        }

        try
        {
            try
            {
                Map<String, MediaType> mapExtensionToMediaType = new HashMap<>();

                // map default ".extension" to MediaType
                mapExtensionToMediaType.put( "atom", MediaType.APPLICATION_ATOM_XML_TYPE );
                mapExtensionToMediaType.put( "xml", MediaType.APPLICATION_XML_TYPE );
                mapExtensionToMediaType.put( "json", MediaType.APPLICATION_JSON_TYPE );
                mapExtensionToMediaType.put( "kml", RestMediaTypes.APPLICATION_KML_TYPE );

                // add specific-plugin-provided extensions
                List<MediaTypeMapping> listMappings = SpringContextService.getBeansOfType( MediaTypeMapping.class );

                if ( listMappings != null )
                {
                    for ( MediaTypeMapping mapping : listMappings )
                    {
                        String strExtension = mapping.getExtension(  );
                        MediaType mediaType = mapping.getMediaType(  );

                        if ( StringUtils.isNotBlank( strExtension ) && ( mediaType != null ) )
                        {
                            mapExtensionToMediaType.put( strExtension, mediaType );
                        }
                        else
                        {
                            LOGGER.error( "Can't add media type mapping for extension : " + strExtension +
                                ", mediatype : " + mediaType + ". Please check your context configuration." );
                        }
                    }
                }

                property( ServerProperties.MEDIA_TYPE_MAPPINGS, mapExtensionToMediaType );

            }

            catch ( UnsupportedOperationException uoe )
            {
                // In jersey 1.x, this might have been an immutable map.
                // In jersey 2.x, I don't know if this is useful
                LOGGER.error( uoe.getMessage(  ) + ". Won't support extension mapping (.json, .xml, .atom)", uoe );
            }

            // log services
            if ( LOGGER.isDebugEnabled(  ) )
            {
                LOGGER.debug( "Listing registered services and providers" );

                for ( Class<?> clazz : getClasses(  ) )
                {
                    LOGGER.debug( clazz );
                }

                LOGGER.debug( "End of listing" );
            }
        }
        catch ( RuntimeException e )
        {
        	LOGGER.log( Level.ERROR, "REST services won't be available. Please check your configuration or enable at least on rest module." );
            LOGGER.log( Level.ERROR, "LuteceJerseySpringServlet : Exception occurred when intialization", e );
            // throw e;
        }
    }
}
