package com.next.api;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	// 定义交换机的名称
	public static final String EXCHANGE_TOPICS_PUSH = "exchange_topics_push";
	
	// 定义队列的名称
	public static final String QUEUE_NAME_PUSH = "queue_name_push";
	
	// 创建交换机
	@Bean(EXCHANGE_TOPICS_PUSH)
	public Exchange exchange() {
		return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_PUSH)	// 定义交换机的名称
				.durable(true)	// 持久化，保证mq重启后依然存在
				.build();
	}
	
	// 定义队列
	@Bean(QUEUE_NAME_PUSH)
	public Queue queue() {
		return new Queue(QUEUE_NAME_PUSH);
	}
	
	// 绑定队列到交换机
	@Bean
	public Binding bindingQueueToExchange(
			@Qualifier(QUEUE_NAME_PUSH) Queue queue,
			@Qualifier(EXCHANGE_TOPICS_PUSH) Exchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("push.#.display").noargs();
	}
	
}
