package com.arca.front.web;

import com.arca.front.bean.Data;
import com.arca.front.bean.Executions;
import com.arca.front.domain.DataList;
import com.arca.front.repository.DataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.batch.operations.NoSuchJobException;
import java.util.*;

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
     * Run data import job within a Web Container
     *
     * @throws Exception
     */
    @RequestMapping(value = "/job/start", method = RequestMethod.GET)
    public void startJob() throws Exception {
        try {
            jobRegistry.register(jobFactory);
            jobOperator.start(importDataJob.getName(), null);
        } catch (NoSuchJobException | JobInstanceAlreadyExistsException e) {
            LOGGER.error("{}", e);
        } catch (DuplicateJobException e) {
            LOGGER.error("{}", e.getMessage());
        }
    }


    @RequestMapping(value = "/job/stop", method = RequestMethod.GET)
    public void stopJob() throws Exception {
        try {
            Set<Long> executions = jobOperator.getRunningExecutions(importDataJob.getName());

            if (executions != null && executions.size() > 0) {
                jobOperator.stop(executions.iterator().next());
            } else {
                LOGGER.info("No executions found !");
            }
        } catch (NoSuchJobException e) {
            LOGGER.error("{}", e.getMessage());
        }
    }

    /**
     * Get job status
     * status=STARTED, exitStatus=EXECUTING, readCount=43570
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/job/status", method = RequestMethod.GET)
    public Executions getJobStatus() throws Exception {

        Executions executions = new Executions();

        try {
            Set<Long> runningExecutions = jobOperator.getRunningExecutions(importDataJob.getName());

            if (runningExecutions != null && runningExecutions.size() > 0) {
                Map<Long, String> jobInfo = jobOperator.getStepExecutionSummaries(runningExecutions.iterator().next());
                String infos = jobInfo.get(0L);
                LOGGER.info("{}", infos);

                for (String item : infos.split(":")[1].split(",")) {
                    //LOGGER.info(item);
                    String[] value = item.split("=");

                    if ("status".equals(value[0])) {
                        LOGGER.info("{} : {}", value[0], value[1]);
                        executions.setStatus(value[1]);
                    }
                    if ("exitStatus".equals(value[0])) {
                        LOGGER.info("{} : {}", value[0], value[1]);
                        executions.setExitStatus(value[1]);
                    }
                    if ("readCount".equals(value[0])) {
                        LOGGER.info("{} : {}", value[0], value[1]);
                        executions.setReadCount(value[1]);
                    }
                    if ("writeCount".equals(value[0])) {
                        LOGGER.info("{} : {}", value[0], value[1]);
                        executions.setWriteCount(value[1]);
                    }
                }
            } else {
                LOGGER.info("No executions found !");
            }
        } catch (NoSuchJobException | IndexOutOfBoundsException e) {
            LOGGER.error("{}", e.getMessage());
        }

        return executions;
    }

    @Autowired
    private DataRepository repository;

    @RequestMapping(value = "/data?page={page}&count={count}", method = RequestMethod.GET)
    public DataList getData(@PathVariable int page, @PathVariable int count) {


        DataList dataList = new DataList();
        //List<Data> dataPaginate = new ArrayList<>();

//        try {
        // Get paginate data
//            dataPaginate = DataManager.getAllData(count * page, count);
//            long allDataSize = DataManager.getCount();

        Page<Data> dataPaginate = repository.findAll(new PageRequest(page, count));
        long allDataSize = repository.count();

        // Build response object
        dataList.setCount(count);
        dataList.setPage(page);
        dataList.setPages((int) (allDataSize / count));
        dataList.setSize((int) allDataSize);
        dataList.setDataEntities(dataPaginate);
        dataList.setSortBy("timestamp");
        dataList.setSortOrder("asc");
//        } catch (UnknownHostException e) {
//            LOGGER.error("Error : {}", e);
//        }

        return dataList;
    }

}
