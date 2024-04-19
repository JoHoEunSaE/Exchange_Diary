package org.johoeunsae.exchangediary.exception.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DiscordWebhookErrorSender {

	@Value("${discord.webhook.url}")
	private String DISCORD_WEBHOOK_URL;

	private String formatDefaultErrorMessage(Exception exception, String requestUrl,
			String requestMethod) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String stackTrace = sw.toString();
		// 첫 번째 에러 라인 추출
		String firstLineOfStackTrace = stackTrace.split("\n")[0];
		// 메시지 포맷팅
		return String.format("**ERROR API URL**\n%s %s\n**MESSAGE**\n`%s`", requestMethod,
				requestUrl, firstLineOfStackTrace);
	}

	public void sendDefaultMessage(Exception exception, String requestUrl,
			String requestMethod) {
		String message = formatDefaultErrorMessage(exception, requestUrl, requestMethod);
		RestTemplate restTemplate = new RestTemplate();
		Map<String, String> body = new HashMap<>();
		body.put("content", message);
		restTemplate.postForEntity(DISCORD_WEBHOOK_URL, body, String.class);
	}

	public void sendWebErrorMessage(DiscordWebErrorMessage discordWebErrorMessage) {
		RestTemplate restTemplate = new RestTemplate();
		Map<String, String> body = new HashMap<>();
		body.put("content", discordWebErrorMessage.toString());
		restTemplate.postForEntity(DISCORD_WEBHOOK_URL, body, String.class);
	}
}
