package com.cardshifter.gdx.api.abstr;

import com.cardshifter.gdx.api.messages.Message;

public abstract class RequestMessage extends Message {
	
	public RequestMessage(String string) {
		super(string);
	}

	private int requestId;
	
	public final int getRequestId() {
		return requestId;
	}

}