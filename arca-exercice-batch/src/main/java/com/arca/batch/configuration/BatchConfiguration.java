package com.arca.batch.configuration;

import com.arca.batch.bean.DataTxt;
import com.arca.batch.processor.DataItemProcessor;
import com.arca.core.entity.DataEntity;
import com.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.ResourceBundle;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    // Slf4j logger
    private final static Logger LOGGER = LoggerFactory.getLogger(BatchConfiguration.class);
    private static ResourceBundle bundleConfigApplication = ResourceBundle.getBundle("application");
    private static final String ADDRESS = bundleConfigApplication.getString("db.address");
    private static final int PORT = Integer.valueOf(bundleConfigApplication.getString("db.port"));
    private static final String DB_NAME = bundleConfigApplication.getString("db.name");

    // tag::readerwriterprocessor[]
    @Bean
    public ItemReader<DataTxt> reader() {
        FlatFileItemReader<DataTxt> reader = new FlatFileItemReader<DataTxt>();
        reader.setEncoding("UTF-8");
        //final ClassPathResource resource = new ClassPathResource("data.txt");
        final FileSystemResource resource = new FileSystemResource("C:\\Users\\Wifsimster\\mercurial\\arca-exercice\\data.txt");
        reader.setResource(resource);
        reader.setLineMapper(new DefaultLineMapper<DataTxt>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"timestamp", "value", "country"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<DataTxt>() {{
                setTargetType(DataTxt.class);
            }});
        }});

        return reader;
    }

    @Bean
    public ItemProcessor<DataTxt, DataEntity> processor() {
        return new DataItemProcessor();
    }

    @Bean
    public ItemWriter<DataEntity> writer(MongoOperations template) {
        final MongoItemWriter<DataEntity> mongoItemWriter = new MongoItemWriter<DataEntity>();
        mongoItemWriter.setTemplate(template);
        mongoItemWriter.setCollection("data");
        return mongoItemWriter;
    }
    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean
    public Job importDataJob(JobBuilderFactory jobs, @Qualifier("step1") Step s1) {
        return jobs.get("importDataJob")
                .incrementer(new RunIdIncrementer())
                .flow(s1)
                .end()
                .build();
    }

    @Bean
    public MongoDbFactory mongoDbFactory() throws Exception {
        LOGGER.info("MongoDB({}:{})",ADDRESS, PORT);
        MongoClient client = new MongoClient(ADDRESS, PORT);
        return new SimpleMongoDbFactory(client, DB_NAME);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<DataTxt> reader,
                      ItemWriter<DataEntity> writer, ItemProcessor<DataTxt, DataEntity> processor) {

        ItemReadListener<DataTxt> itemReadListener = new ItemReadListener<DataTxt>() {

            @Override
            public void beforeRead() {

            }

            @Override
            public void afterRead(DataTxt o) {

            }

            @Override
            public void onReadError(Exception e) {
                LOGGER.warn("Error reading line : {}", e);
            }
        };

        return stepBuilderFactory.get("step1")
                .<DataTxt, DataEntity>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(itemReadListener)
                .faultTolerant().skip(Exception.class).skipLimit(200000)
                .build();
    }
    // end::jobstep[]
}