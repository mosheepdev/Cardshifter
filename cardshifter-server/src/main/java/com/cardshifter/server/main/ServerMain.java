package com.cardshifter.server.main;

import org.apache.log4j.PropertyConfigurator;

import com.cardshifter.server.model.MainServer;

public class ServerMain {
	
	public static void main(String[] args) {
		PropertyConfigurator.configure(ServerMain.class.getResource("log4j.properties"));
		new MainServer().start();
	}

}
