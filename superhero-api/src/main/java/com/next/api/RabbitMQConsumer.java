package com.next.api;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.next.pojo.bo.MovieMQMsgBO;
import com.next.utils.JsonUtils;
import com.next.utils.MoviePushUtil;

@Component
public class RabbitMQConsumer {

	
	@RabbitListener(queues = {RabbitMQConfig.QUEUE_NAME_PUSH})
	public void listenMQPush(String payload, Message msg) {
		System.out.println(payload);
		
		MovieMQMsgBO msgObj = JsonUtils.jsonToPojo(payload, MovieMQMsgBO.class);
		
		MoviePushUtil.doPush(msgObj.getTitle(), msgObj.getText());
	}
	
}
