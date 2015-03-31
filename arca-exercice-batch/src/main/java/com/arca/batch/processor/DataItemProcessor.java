package com.arca.batch.processor;

import com.arca.batch.bean.DataTxt;
import com.arca.batch.bean.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.Date;

/**
 * Intermediate processor
 */
public class DataItemProcessor implements ItemProcessor<DataTxt, Data> {

    // Slf4j logger
    private final static Logger LOGGER = LoggerFactory.getLogger(DataItemProcessor.class);

    @Override
    public Data process(final DataTxt dataTxt) throws Exception {

        Data data = new Data();
        data.setDate(new Date(Long.valueOf(dataTxt.getTimestamp())));
        data.setCountry(dataTxt.getCountry());
        data.setValue(dataTxt.getValue());

        LOGGER.debug("Process() : {}", data.toString());

        return data;
    }

}