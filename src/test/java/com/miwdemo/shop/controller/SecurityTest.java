package com.miwdemo.shop.controller;

import com.miwdemo.shop.MIWDemoApplication;
import com.miwdemo.shop.model.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MIWDemoApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ItemRepository mockRepository;


    @WithMockUser("USER")
    @Test
    public void getLoginOk() throws Exception {

        mockMvc.perform(get("/items"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser("USER")
    @Test
    public void getLoginOkItem() throws Exception {

        mockMvc.perform(get("/items/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.price", is(12)));
    }

    @Test
    public void getNologin401() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}