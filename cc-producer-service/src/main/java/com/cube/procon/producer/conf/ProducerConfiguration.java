package com.cube.procon.producer.conf;

import com.cube.procon.property.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(factory = YamlPropertySourceFactory.class, value = "file:${producer.config.file}")
public class ProducerConfiguration {

}
