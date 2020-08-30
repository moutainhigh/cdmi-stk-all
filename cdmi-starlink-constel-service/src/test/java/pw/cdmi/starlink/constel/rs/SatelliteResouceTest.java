package pw.cdmi.starlink.constel.rs;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SatelliteResouceTest {

	@Autowired
	private MockMvc mockMvc;// 定义一个 MockMvc

	@Test
	public void listSatelByConstel() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/cfg/v1/satel").param("constelId", "112212")
						.accept(MediaType.APPLICATION_JSON_VALUE)) // perform 结束
//				.andExpect(MockMvcResultMatchers.status().isOk()) // 添加断言
//				.andExpect(MockMvcResultMatchers.content().string("Hello fishpro"))// 添加断言
//				.andDo(MockMvcResultHandlers.print()) // 添加执行
				.andReturn();// 添加返回

		// 下面部分等等与 addExcept 部分
		int status = mvcResult.getResponse().getStatus(); // 得到返回代码
		String content = mvcResult.getResponse().getContentAsString(); // 得到返回结果
		Assert.assertEquals(200, status); // 等于 andExpect(MockMvcResultMatchers.status().isOk()) //添加断言
		Assert.assertEquals("Hello World", content); // andExpect(MockMvcResultMatchers.content().string("Hello
														// World"))//添加断言
	}

	@Test
	public void deleteConstellation() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.delete("/cfg/v1/constel/12223")
						.accept(MediaType.APPLICATION_JSON_VALUE)) // perform 结束
				.andExpect(MockMvcResultMatchers.status().isOk()) // 添加断言
//				.andExpect(MockMvcResultMatchers.content().string("Hello fishpro"))// 添加断言
				.andDo(MockMvcResultHandlers.print()) // 添加执行
				.andReturn();// 添加返回

	}
}
