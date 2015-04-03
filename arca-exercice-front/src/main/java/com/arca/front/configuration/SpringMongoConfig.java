package com.arca.front.configuration;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.ResourceBundle;

@Configuration
public class SpringMongoConfig {

    private static ResourceBundle bundleConfigApplication = ResourceBundle.getBundle("application");
    private static final String ADDRESS = bundleConfigApplication.getString("db.address");
    private static final int PORT = Integer.valueOf(bundleConfigApplication.getString("db.port"));
    private static final String DB_NAME = bundleConfigApplication.getString("db.name");

    @Bean
    public MongoDbFactory mongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(ADDRESS, PORT), DB_NAME);
    }


    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;
    }
}
