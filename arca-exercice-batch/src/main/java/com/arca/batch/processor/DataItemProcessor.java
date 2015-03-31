package com.arca.batch.processor;

import com.arca.batch.bean.DataTxt;
import com.arca.core.entity.DataEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.Date;

/**
 * Intermediate processor
 */
public class DataItemProcessor implements ItemProcessor<DataTxt, DataEntity> {

    // Slf4j logger
    private final static Logger LOGGER = LoggerFactory.getLogger(DataItemProcessor.class);

    @Override
    public DataEntity process(final DataTxt dataTxt) throws Exception {

        DataEntity dataEntity = new DataEntity();
        dataEntity.setDate(new Date(Long.valueOf(dataTxt.getTimestamp())));
        dataEntity.setCountry(dataTxt.getCountry());
        dataEntity.setValue(dataTxt.getValue());

        LOGGER.debug("Process() : {}", dataEntity.toString());

        return dataEntity;
    }

}