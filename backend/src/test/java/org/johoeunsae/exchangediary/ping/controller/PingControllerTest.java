package org.johoeunsae.exchangediary.ping.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import utils.test.E2EMvcTest;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PingControllerTest extends E2EMvcTest {
	private final String URL_PREFIX = "/ping";
	private MockMvc mockMvc;

	@BeforeEach
	protected void setup(WebApplicationContext webApplicationContext) {
		this.mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilters(new CharacterEncodingFilter("UTF-8", true))
				.apply(springSecurity())
				.build();
	}

	@Nested
	@DisplayName("ping")
	class Ping {
		private final String url = URL_PREFIX;

		@DisplayName("fine")
		@Test
		void fine() throws Exception {
			mockMvc.perform(get(url))
					.andExpect(status().isOk())
					.andExpect(content().string("pong"));
		}
	}
}
