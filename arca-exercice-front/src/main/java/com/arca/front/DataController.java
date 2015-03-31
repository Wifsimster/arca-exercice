package com.arca.front;

import com.arca.batch.Extractor;
import com.arca.core.entity.DataEntity;
import com.arca.core.manager.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DataController {

    // Slf4j logger
    private final static Logger LOGGER = LoggerFactory.getLogger(DataController.class);

    @RequestMapping(value = "/extract", method = RequestMethod.POST)
    public void extractFile() {
        Extractor extractor = new Extractor();
        extractor.extractFile();
    }

    @RequestMapping(value = "/data?page={page}&count={count}", method = RequestMethod.GET)
    public DataList getData(@PathVariable int page, @PathVariable int count) {

        DataList dataList = new DataList();
        List<DataEntity> dataPaginate = new ArrayList<>();

        try {
            // Get paginate data
            dataPaginate = DataManager.getAllData(count * page, count);
            long allDataSize = DataManager.getCount();

            // Build response object
            dataList.setCount(count);
            dataList.setPage(page);
            dataList.setPages((int) (DataManager.getCount() / count));
            dataList.setSize((int) allDataSize);
            dataList.setDataEntities(dataPaginate);
            dataList.setSortBy("timestamp");
            dataList.setSortOrder("asc");
        } catch (UnknownHostException e) {
            LOGGER.error("Error : {}", e);
        }

        return dataList;
    }

}
