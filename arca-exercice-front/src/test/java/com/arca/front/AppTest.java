package com.arca.front;

import com.arca.front.repository.DataRepository;
import com.arca.front.web.DataController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class AppTest {

    // Slf4j logger
    private final static Logger LOGGER = LoggerFactory.getLogger(AppTest.class);

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private DataRepository dataRepository;

    // Spring MVC mock
    private MockMvc mockMvc;

    @Value("${file.path}")
    private String DATA_FILE;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(this.context).build();
    }

    /**
     * Test reandom rest service
     *
     * @throws Exception
     */
    @Test
    public void testBadRequest() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(status().is4xxClientError());
    }

    /**
     * Test total number of lines in a file
     */
    @Test
    public void testNoLines() {
        DataController dataController = new DataController();
        long noLines = dataController.getNoOfLines(System.getProperty("user.dir") + DATA_FILE);
        assertTrue(noLines > 0);
    }

    @Test
    public void testNoLinesFalse() {
        DataController dataController = new DataController();
        long noLines = dataController.getNoOfLines("");
        assertTrue(noLines == -1);
    }

    /**
     * Test get percentage
     *
     * @throws Exception
     */
    @Test
    public void testGetPercentage() throws Exception {
        MvcResult result = mockMvc.perform(get("/percentage"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andReturn();
        assertTrue("Welcome to SockJS!".equals(result.getResponse().getContentAsString()));
    }

    /**
     * Test MongoDB communication
     */
    @Test
    public void testDatabase() {
        long count = dataRepository.count();
        assertNotNull(count);
    }

    /**
     * Test sum by country
     *
     * @throws Exception
     */
    @Test
    public void testSumByCountry() throws Exception {
        MvcResult result = mockMvc.perform(get("/sum/by/country"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn();

        LOGGER.debug("Result : {}", result.getResponse().getContentAsString());

        assertTrue("UTF-8".equals(result.getResponse().getCharacterEncoding()));
    }

    /**
     * Test sum by day
     *
     * @throws Exception
     */
    @Test
    public void testSumByDay() throws Exception {
        MvcResult result = mockMvc.perform(get("/sum/by/day"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn();

        LOGGER.debug("Result : {}", result.getResponse().getContentAsString());

        assertTrue("UTF-8".equals(result.getResponse().getCharacterEncoding()));
    }

    @After
    public void downUp() {
        this.mockMvc = null;
    }

    // fyi : http://docs.spring.io/spring-batch/trunk/reference/html/testing.html

    // Test start job

    // Test stop job

    // Test getting job info

    // Test job resume with data coherence

}
