package com.arca.front.web;

import com.arca.front.bean.Data;
import com.arca.front.bean.DataList;
import com.arca.front.bean.Executions;
import com.arca.front.bean.Response;
import com.arca.front.repository.DataRepository;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.batch.operations.NoSuchJobException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class DataController {

    // Slf4j logger
    private final static Logger LOGGER = LoggerFactory.getLogger(DataController.class);

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job importDataJob;

    @Autowired
    JobOperator jobOperator;

    @Autowired
    JobRegistry jobRegistry;

    @Autowired
    JobFactory jobFactory;

    @Autowired
    private DataRepository repository;

    /**
     * Build job parameters
     *
     * @return
     */
    private JobParameters createInitialJobParameterMap() {
        Map<String, JobParameter> m = new HashMap<>();
        m.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters p = new JobParameters(m);
        return p;
    }

    @Autowired
    @Qualifier("asyncJobLauncher")
    private JobLauncher asyncJobLauncher;

    private JobExecution execution;

    /**
     * Rest service for Junit
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public Response testRest() throws Exception {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("test");
        response.setData(null);
        return response;
    }

    /**
     * Run data import job within a Web Container
     *
     * @throws Exception
     */
    @RequestMapping(value = "/job/start", method = RequestMethod.GET)
    public Response startJob() throws Exception {

        Response response = new Response();

        try {
            // Register the job from registry
            jobRegistry.register(jobFactory);

            // Start async the job with timestamp parameter
            execution = asyncJobLauncher.run(importDataJob, createInitialJobParameterMap());

            final ExitStatus status = execution.getExitStatus();

            LOGGER.info("Status : {}", status.getExitCode());

            if (ExitStatus.COMPLETED.getExitCode().equals(status.getExitCode())) {
                // Response object
                response.setStatusCode(200);
                response.setMessage("Job started !");
                response.setData(execution);
            } else {
                response.setStatusCode(400);
                response.setMessage("Something went wrong with job !");
            }
        } catch (NoSuchJobException | DuplicateJobException | IllegalStateException e) {
            LOGGER.error("Error : {}", e.getMessage());
            response.setStatusCode(400);
            response.setMessage("Something went wrong with job !");
            response.setData(e);
        }

        return response;
    }

    @RequestMapping(value = "/job/stop", method = RequestMethod.GET)
    public Response stopJob() throws Exception {
        Response response = new Response();
        try {
            // Stop the current job
            jobOperator.stop(execution.getJobId());

            // Unregister the job from registry
            jobRegistry.unregister(importDataJob.getName());

            // Response object
            response.setStatusCode(200);
            response.setMessage("Job stopped !");
        } catch (NoSuchJobException e) {
            LOGGER.error("{}", e.getMessage());
            response.setStatusCode(400);
            response.setMessage("Something went wrong with job !");
            response.setData(e);
        }
        return response;
    }

    /**
     * Get job status
     * status=STARTED, exitStatus=EXECUTING, readCount=43570
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/job/status", method = RequestMethod.GET)
    public Response getJobStatus() throws Exception {

        Response response = new Response();
        Executions executions = new Executions();

        try {
            Set<Long> runningExecutions = jobOperator.getRunningExecutions(importDataJob.getName());

            if (runningExecutions != null && runningExecutions.size() > 0) {
                Map<Long, String> jobInfo = jobOperator.getStepExecutionSummaries(execution.getJobId());

                String result = null;

                for (String value : jobInfo.values()) {
                    result = value;
                }

                LOGGER.info("Result : {}", result);

                // Parse executions info for HTTP response
                if (result != null) {
                    Pattern p = Pattern.compile("(\\w+)=\"*((?<=\")[^\"]+(?=\")|([^\\s]+))\"*");
                    Matcher m = p.matcher(result);

                    while (m.find()) {
//                        LOGGER.info("{} : {}", m.group(1), m.group(2));

                        if ("status".equals(m.group(1))) {
                            executions.setStatus(m.group(2));
                        }
                        if ("exitStatus".equals(m.group(1))) {
                            executions.setExitStatus(m.group(2));
                        }
                        if ("readCount".equals(m.group(1))) {
                            executions.setReadCount(m.group(2));
                        }
                        if ("writeCount".equals(m.group(1))) {
                            executions.setWriteCount(m.group(2));
                        }
                    }

                    response.setStatusCode(200);
                    response.setMessage("Info !");
                    response.setData(executions);
                }
            } else {
                LOGGER.info("No executions found !");
            }
        } catch (NoSuchJobException | IndexOutOfBoundsException e) {
            LOGGER.error("{}", e.getMessage());
            response.setStatusCode(400);
            response.setMessage("Something went wrong with job !");
            response.setData(e);
        }

        return response;
    }

    /**
     * Return paginate data
     *
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/data?page={page}&count={count}", method = RequestMethod.GET)
    public DataList getData(@PathVariable int page, @PathVariable int count) {

        DataList dataList = new DataList();
        Page<Data> dataPaginate = repository.findAll(new PageRequest(page, count));

        // Build response object
        dataList.setCount(dataPaginate.getSize());
        dataList.setPage(page);
        dataList.setPages((int) (dataPaginate.getTotalElements() / dataPaginate.getSize()));
        dataList.setSize((int) dataPaginate.getTotalElements());
        dataList.setDataEntities(dataPaginate.getContent());
        dataList.setSortBy("timestamp");
        dataList.setSortOrder("asc");

        return dataList;
    }

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * Get countries list with pagination
     *
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/country?page={page}&count={count}", method = RequestMethod.GET)
    public DataList getDistinctCountry(@PathVariable int page, @PathVariable int count) {

        DataList dataList = new DataList();
        List<Data> data = mongoTemplate.getCollection("data").distinct("country");

        // Build response object
        dataList.setCount(count);
        dataList.setPage(page);
        dataList.setPages(count / data.size());
        dataList.setSize(data.size());
        dataList.setDataEntities(data);
        dataList.setSortBy("timestamp");
        dataList.setSortOrder("asc");

        return dataList;
    }

    /**
     * Get countries list
     *
     * @return
     */
    @RequestMapping(value = "/sum/by/country", method = RequestMethod.GET)
    public Map<String, String> getValueSumByDistinctCountry() {

        // Build the $projection operation
        DBObject fields = new BasicDBObject("country", 1);
        fields.put("value", 1);
        fields.put("_id", 0);
        DBObject project = new BasicDBObject("$project", fields);

        // Sum the value of each country
        DBObject groupFields = new BasicDBObject("_id", "$country");
        groupFields.put("sum", new BasicDBObject("$sum", "$value"));
        DBObject group = new BasicDBObject("$group", groupFields);

        // Sort by country
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));

        // Run aggregation
        List<DBObject> pipeline = Arrays.asList(project, group, sort);
        AggregationOutput output = mongoTemplate.getCollection("data").aggregate(pipeline);

        Map<String, String> resultMap = new HashMap<>();

        for (DBObject result : output.results()) {
            resultMap.put(String.valueOf(result.get("_id")), String.valueOf(result.get("sum")));
        }

        return resultMap;
    }

}
