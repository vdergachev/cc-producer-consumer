package com.cube.procon.property;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(@Nullable final String name, final EncodedResource resource)
            throws IOException {
        final String sourceName = name != null ? name : resource.getResource().getFilename();
        return new PropertiesPropertySource(sourceName, loadYamlIntoProperties(resource));
    }

    private Properties loadYamlIntoProperties(final EncodedResource resource) throws FileNotFoundException {
        try {
            final var factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();
            return factory.getObject();
        } catch (IllegalStateException ex) {
            // for ignoreResourceNotFound
            final Throwable cause = ex.getCause();
            if (cause instanceof FileNotFoundException) {
                throw (FileNotFoundException) ex.getCause();
            }
            throw ex;
        }
    }
}
