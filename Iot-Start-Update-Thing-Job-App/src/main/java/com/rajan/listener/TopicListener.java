/**
 * 
 */
package com.rajan.listener;

import com.amazonaws.services.iot.client.AWSIotMessage;

/**
 * @author rajan
 *
 */
public interface TopicListener {
	public String onTopicResponse(AWSIotMessage message);
}
