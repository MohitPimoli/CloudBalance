package com.cloudbalance.lens.utils;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    /**
     * @param name the name of the property source
     * (can be {@code null} in which case the factory implementation
     * will have to generate a name based on the given resource)
     * @param resource the resource (potentially encoded) to wrap
     * @return
     * @throws IOException
     */

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());


        Properties properties = factory.getObject();
        return new PropertiesPropertySource(
                Objects.requireNonNull(resource.getResource().getFilename()),
                properties);
    }
}

