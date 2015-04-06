package com.arca.front.web;

import com.arca.front.bean.Data;
import com.arca.front.bean.DataList;
import com.arca.front.bean.Executions;
import com.arca.front.bean.Response;
import com.arca.front.repository.DataRepository;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandFailureException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import javax.batch.operations.NoSuchJobException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class DataController {

    // Slf4j logger
    private final static Logger LOGGER = LoggerFactory.getLogger(DataController.class);

    @Value("${file.path}")
    private String DATA_FILE;

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

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    @Qualifier("asyncJobLauncher")
    private JobLauncher asyncJobLauncher;

    private JobExecution execution;

    private static float totalLineNumber;

    // Handle all dispatcher exception
    @ExceptionHandler(Exception.class)
    public void handleExceptions(Exception e) {
        LOGGER.error("Handle exceptions : {}", e.getClass());
    }

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

    /**
     * Return the total number of lines in the data file
     *
     * @return
     */
    public int getNoOfLines() {
        int lineNumber = -1;

        try {
            LineNumberReader reader = new LineNumberReader(new FileReader(DATA_FILE));

            // Skips those many chars, if you feel your file size may exceed you can use Long.MAX_VALUE
            reader.skip(Integer.MAX_VALUE);

            return reader.getLineNumber();
        } catch (IOException e) {
            LOGGER.error("IOException : {}", e);
        }

        LOGGER.info("Total line number : {}", totalLineNumber);
        return lineNumber;
    }

    /**
     * Run data import job within a Web Container
     *
     * @throws Exception
     */
    @RequestMapping(value = "/job/start", method = RequestMethod.GET)
    public Response startJob() throws Exception {

        totalLineNumber = getNoOfLines();
        LOGGER.info("Total line number : {}", totalLineNumber);

        Response response = new Response();

        try {
            // Register the job from registry
            jobRegistry.register(jobFactory);

            // Start async the job with timestamp parameter
            execution = asyncJobLauncher.run(importDataJob, createInitialJobParameterMap());

            final ExitStatus status = execution.getExitStatus();

            LOGGER.info("Status : {}", status.getExitCode());

            response.setStatusCode(200);
            response.setMessage("Job started !");
            response.setData(execution);
        } catch (NoSuchJobException | DuplicateJobException | IllegalStateException e) {
            LOGGER.error("Catching error : {}", e);
            LOGGER.error("Return error response to client !");
            response.setStatusCode(400);
            response.setMessage("Something went wrong with job !");
            response.setData(e);
        }

        return response;
    }

    @MessageMapping("/percentage")
    @SendTo("/info/percentage")
    public String getPercentage() throws Exception {

        if (totalLineNumber > 0) {
            Response response = getJobStatus();
            Executions executions = (Executions) response.getData();
            float current = Float.valueOf(executions.getWriteCount());

            if (current > 0) {
                float percentage = ((current * 100) / totalLineNumber);
                LOGGER.debug("Percentage : {}%", percentage);
                DecimalFormat df = new DecimalFormat("#.##");
                return String.valueOf(df.format(percentage));
            } else {
                LOGGER.warn("Cannot get current process line !");
                return "-1";
            }
        } else {
            LOGGER.warn("totalLineNumber is not initialized yet !");
            return "-1";
        }
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
            LOGGER.error("NoSuchJobException : {}", e);
            LOGGER.error("Return error response to client !");
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
            if (jobOperator != null && importDataJob != null) {
                Set<Long> runningExecutions = jobOperator.getRunningExecutions(importDataJob.getName());

                if (runningExecutions != null && runningExecutions.size() > 0) {
                    Map<Long, String> jobInfo = jobOperator.getStepExecutionSummaries(execution.getJobId());

                    String result = null;

                    for (String value : jobInfo.values()) {
                        result = value;
                    }

                    // Parse executions info for HTTP response
                    if (result != null) {
                        Pattern p = Pattern.compile("(\\w+)=\"*((?<=\")[^\"]+(?=\")|([^\\s]+))\"*");
                        Matcher m = p.matcher(result);

                        while (m.find()) {
                            //LOGGER.info("{} : {}", m.group(1), m.group(2));

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

                        float percentage = ((Float.valueOf(executions.getWriteCount()) * 100) / totalLineNumber);
                        NumberFormat numberFormat = NumberFormat.getNumberInstance();
                        numberFormat.setMinimumFractionDigits(2);

                        response.setStatusCode(200);
                        response.setMessage(numberFormat.format(percentage) + "% done");
                        response.setData(executions);
                    }
                } else {
                    String message = "No executions found !";
                    LOGGER.error(message);
                    LOGGER.error("Return error response to client !");
                    response.setStatusCode(404);
                    response.setMessage(message);
                }
            } else {
                String message = "No job operator found !";
                LOGGER.error(message);
                LOGGER.error("Return error response to client !");
                response.setStatusCode(404);
                response.setMessage(message);
            }
        } catch (NoSuchJobException | IndexOutOfBoundsException e) {
            LOGGER.error("Catching error : {}", e);
            LOGGER.error("Return error response to client !");
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

        if (repository != null) {
            Page<Data> dataPaginate = repository.findAll(new PageRequest(page, count));

            // Build response object
            dataList.setCount(dataPaginate.getSize());
            dataList.setPage(page);
            dataList.setPages((int) (dataPaginate.getTotalElements() / dataPaginate.getSize()));
            dataList.setSize((int) dataPaginate.getTotalElements());
            dataList.setDataEntities(dataPaginate.getContent());
            dataList.setSortBy("timestamp");
            dataList.setSortOrder("asc");
        } else {
            LOGGER.warn("No repository found !");
        }
        return dataList;
    }

    /**
     * Return data count
     *
     * @return
     */
    @RequestMapping(value = "/data/count", method = RequestMethod.GET)
    public Response getDataCount() {
        Response response = new Response();

        if (repository != null) {
            response.setStatusCode(200);
            response.setData(repository.count());
        } else {
            LOGGER.warn("No repository found !");
        }

        return response;
    }

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

        if (mongoTemplate != null) {
            List<Data> data = mongoTemplate.getCollection("data").distinct("country");

            // Build response object
            dataList.setCount(count);
            dataList.setPage(page);
            dataList.setPages(count / data.size());
            dataList.setSize(data.size());
            dataList.setDataEntities(data);
            dataList.setSortBy("timestamp");
            dataList.setSortOrder("asc");
        } else {
            LOGGER.warn("No mongoTemplate found !");
        }

        return dataList;
    }

    /**
     * Get countries list
     *
     * @return
     */
    @RequestMapping(value = "/sum/by/country", method = RequestMethod.GET)
    public Map<String, String> getValueSumByDistinctCountry() {

        Map<String, String> resultMap = new HashMap<>();

        if (mongoTemplate != null) {
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

            for (DBObject result : output.results()) {
                resultMap.put(String.valueOf(result.get("_id")), String.valueOf(result.get("sum")));
            }
        } else {
            LOGGER.warn("No mongoTemplate found !");
        }

        return resultMap;
    }

    /**
     * Get sum of values by day
     *
     * @return
     */
    @RequestMapping(value = "/sum/by/day", method = RequestMethod.GET)
    public Response getSumByDay() {

        Response response = new Response();

        if (mongoTemplate != null) {

            try {
                // Build the $projection operation
                DBObject fields = new BasicDBObject("date", 1);
                fields.put("value", 1);
                fields.put("_id", 0);
                DBObject project = new BasicDBObject("$project", fields);

                // Sum the value of each date
                DBObject groupFields = new BasicDBObject("_id", "$date");
                groupFields.put("sum", new BasicDBObject("$sum", "$value"));
                DBObject group = new BasicDBObject("$group", groupFields);

                // Skip and limit
                DBObject skip = new BasicDBObject("$skip", 0);
                DBObject limit = new BasicDBObject("$limit", 250000);

                // Sort by country
                DBObject sort = new BasicDBObject("$sort", new BasicDBObject("_id", -1));

                // Run aggregation
                List<DBObject> pipeline = Arrays.asList(project, group, skip, limit, sort);
                AggregationOutput output = mongoTemplate.getCollection("data").aggregate(pipeline);

                Map<String, String> resultMap = new TreeMap<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                for (DBObject result : output.results()) {
                    //resultMap.put(sdf.format(result.get("_id")), String.valueOf(result.get("sum")));
                    //date = (Date)formatter.parse(str_date);
                    try {
                        Date date = sdf.parse(sdf.format(result.get("_id")));
                        Timestamp timeStampDate = new Timestamp(date.getTime());
                        long req = timeStampDate.getTime();
                        resultMap.put(String.valueOf(req), String.valueOf(result.get("sum")));
                    } catch (ParseException e) {
                        LOGGER.error("ParseException : {}", e);
                    }
                }

                LOGGER.debug("Result map size : {}", resultMap.size());
                response.setStatusCode(200);
                response.setData(resultMap);

            } catch (CommandFailureException e) {
                LOGGER.error("CommandFailureException : {}", e);
                response.setStatusCode(400);
                response.setMessage("CommandFailureException throw !");
                response.setData(e);
            }
        } else {
            String message = "No mongoTemplate found !";
            LOGGER.warn(message);
            response.setStatusCode(400);
            response.setMessage(message);
        }

        return response;
    }

}
