package com.arca.front;

import com.arca.front.repository.DataRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    // Spring MBV mock
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(this.context).build();
    }

    /**
     * Test Web application
     * @throws Exception
     */
    @Test
    public void testSampleController() throws Exception {
        this.mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    /**
     * Test MongoDB communication
     */
    @Test
    public void testDatabase() {
        long count = dataRepository.count();
        Assert.assertNotNull(count);
    }

    @After
    public void downUp() {
        this.mockMvc = null;
    }

}
