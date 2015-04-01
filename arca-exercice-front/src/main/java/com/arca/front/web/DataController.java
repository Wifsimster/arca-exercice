package com.arca.front.web;

import com.arca.front.bean.Data;
import com.arca.front.domain.DataList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DataController {

    // Slf4j logger
    private final static Logger LOGGER = LoggerFactory.getLogger(DataController.class);

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job importDataJob;

    private static JobExecution jobExecution;

    /**
     * Run data import job within a Web Container
     *
     * @throws Exception
     */
    @RequestMapping(value = "/extract", method = RequestMethod.GET)
    public void handle() throws Exception {
        jobExecution = jobLauncher.run(importDataJob, new JobParameters());
        LOGGER.info("Job start !");
        LOGGER.info("{}", jobExecution);
    }


    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    public void stop() throws Exception {
        jobExecution.stop();
        LOGGER.info("Job stop !");
    }

    @RequestMapping(value = "/data?page={page}&count={count}", method = RequestMethod.GET)
    public DataList getData(@PathVariable int page, @PathVariable int count) {

        DataList dataList = new DataList();
        List<Data> dataPaginate = new ArrayList<>();

//        try {
        // Get paginate data
//            dataPaginate = DataManager.getAllData(count * page, count);
//            long allDataSize = DataManager.getCount();

        // Build response object
        dataList.setCount(count);
        dataList.setPage(page);
//            dataList.setPages((int) (DataManager.getCount() / count));
//            dataList.setSize((int) allDataSize);
//        dataList.setDataEntities(dataPaginate);
        dataList.setSortBy("timestamp");
        dataList.setSortOrder("asc");
//        } catch (UnknownHostException e) {
//            LOGGER.error("Error : {}", e);
//        }

        return dataList;
    }

}
