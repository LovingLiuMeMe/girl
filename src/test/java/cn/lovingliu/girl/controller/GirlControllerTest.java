package cn.lovingliu.girl.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @Author：LovingLiu
 * @Description: 对API测试 感觉还不如使用postman呢
 * @Date：Created in 2019-09-17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GirlControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void girlListByAge() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/girls/age/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}