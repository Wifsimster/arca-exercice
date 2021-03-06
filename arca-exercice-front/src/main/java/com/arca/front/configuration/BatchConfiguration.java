package com.arca.front.configuration;

import com.arca.front.bean.Data;
import com.arca.front.bean.DataTxt;
import com.arca.front.processor.DataItemProcessor;
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
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
@EnableBatchProcessing
@Import(AdditionalBatchConfiguration.class)
public class BatchConfiguration {

    // Slf4j logger
    private final static Logger LOGGER = LoggerFactory.getLogger(BatchConfiguration.class);

    @Value("${db.address}")
    private String ADDRESS;

    @Value("${db.port}")
    private Integer PORT;

    @Value("${db.name}")
    private String DB_NAME;

    @Value("${file.path}")
    private String DATA_FILE;

    // tag::readerwriterprocessor[]
    @Bean
    public ItemReader<DataTxt> reader() {

        // TODO : Create a custom ItemReader to keep trace of current index
        // fyi : http://docs.spring.io/spring-batch/trunk/reference/html/readersAndWriters.html

        FlatFileItemReader<DataTxt> reader = new FlatFileItemReader<DataTxt>();

        try {
            reader.setEncoding("UTF-8");
            //final ClassPathResource resource = new ClassPathResource("data.txt");
            LOGGER.info("Use data file : {]", DATA_FILE);
            final FileSystemResource resource = new FileSystemResource(DATA_FILE);
            reader.setResource(resource);
            reader.setLineMapper(new DefaultLineMapper<DataTxt>() {{
                setLineTokenizer(new DelimitedLineTokenizer() {{
                    setNames(new String[]{"timestamp", "value", "country"});
                }});
                setFieldSetMapper(new BeanWrapperFieldSetMapper<DataTxt>() {{
                    setTargetType(DataTxt.class);
                }});
            }});
        } catch (FlatFileParseException e) {
            LOGGER.error("Error : {}", e);
        }

        return reader;
    }

    @Bean
    public ItemProcessor<DataTxt, Data> processor() {
        return new DataItemProcessor();
    }

    @Bean
    public ItemWriter<Data> writer(MongoOperations template) {
        final MongoItemWriter<Data> mongoItemWriter = new MongoItemWriter<Data>();
        mongoItemWriter.setTemplate(template);
        mongoItemWriter.setCollection("data");
        return mongoItemWriter;
    }
    // end::readerwriterprocessor[]

    // tag::jobstep[]
    /**
     * Initialize MongoClient instance with conf
     *
     * @return
     * @throws Exception
     */
    @Bean
    public MongoDbFactory mongoDbFactory() throws Exception {
        LOGGER.info("MongoDB({}:{})", ADDRESS, PORT);
        MongoClient client = new MongoClient(ADDRESS, PORT);
        return new SimpleMongoDbFactory(client, DB_NAME);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }

    /**
     * Defines the job
     *
     * @param jobs
     * @param s1
     * @return
     */
    @Bean
    public Job importDataJob(JobBuilderFactory jobs, @Qualifier("step1") Step s1) {
        return jobs.get("importDataJob")
                .incrementer(new RunIdIncrementer())
                .flow(s1)
                .end()
                .build();
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<DataTxt> reader,
                      ItemWriter<Data> writer, ItemProcessor<DataTxt, Data> processor) {

        ItemReadListener<DataTxt> itemReadListener = new ItemReadListener<DataTxt>() {
            @Override
            public void beforeRead() {
            }

            @Override
            public void afterRead(DataTxt o) {
            }

            @Override
            public void onReadError(Exception e) {
                LOGGER.error("Error reading line : {}", e.getMessage());
            }
        };

        return stepBuilderFactory.get("step1")
                .<DataTxt, Data>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(itemReadListener)
                .build();
    }
    // end::jobstep[]
}