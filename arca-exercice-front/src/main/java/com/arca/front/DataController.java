package com.arca.front;

import com.arca.core.entity.DataEntity;
import com.arca.core.manager.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DataController {

    // Slf4j logger
    private final static Logger LOGGER = LoggerFactory.getLogger(DataController.class);

    @RequestMapping(value = "/data?page={page}&count={count}", method = RequestMethod.GET)
    public List<DataEntity> getData(@PathVariable int page, @PathVariable int count) {

        LOGGER.info("Page {}, Count {}", page, count);
        
        List<DataEntity> allData = new ArrayList<>();

        try {
            allData = DataManager.getAllData(count * page, count);
            LOGGER.info("Display data size : {}", allData.size());
        } catch (UnknownHostException e) {
            LOGGER.error("Error : {}", e);
        }

        return allData;
    }

}
