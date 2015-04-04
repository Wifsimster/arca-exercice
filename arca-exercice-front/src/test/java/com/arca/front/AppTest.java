package com.arca.front;

import com.arca.front.repository.DataRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class AppTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private DataRepository dataRepository;

    // Spring MVC mock
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(this.context).build();
    }

    /**
     * Test Rest service
     *
     * @throws Exception
     */
    @Test
    public void testRequest() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
    }

    @Test
    public void testBadRequest() throws Exception {
        mockMvc.perform(get("/test2"))
                .andExpect(status().is4xxClientError());
    }

    /**
     * Test MongoDB communication
     */
    @Test
    public void testDatabase() {
        long count = dataRepository.count();
        assertNotNull(count);
    }

    @Test
    public void testSumByCountry() throws Exception {
        mockMvc.perform(get("/sum/by/country"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
    }


    @Test
    public void testSumByDay() throws Exception {
        mockMvc.perform(get("/sym/by/day"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
    }

    @After
    public void downUp() {
        this.mockMvc = null;
    }

    // Test start job

    // Test stop job

    // Test getting job info

    // Test job resume with data coherence

}
