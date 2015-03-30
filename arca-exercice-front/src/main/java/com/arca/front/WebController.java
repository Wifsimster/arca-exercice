package com.arca.front;

import com.arca.core.entity.DataEntity;
import com.arca.core.manager.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.net.UnknownHostException;
import java.util.List;


@Controller
public class WebController extends WebMvcConfigurerAdapter {

    // Slf4j logger
    private final static Logger LOGGER = LoggerFactory.getLogger(WebController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {

        try {
            List<DataEntity> allData = DataManager.getAllData(0, 150);
            LOGGER.info("All data size : {}", allData.size());
            model.addAttribute("allData", allData);
        } catch (UnknownHostException e) {
            LOGGER.error("Error : {}", e);
        }

        return "home";
    }

}
