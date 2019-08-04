package com.bahn.util.task;

import java.io.IOException;

public class TaskKiller {

	public TaskKiller(String task) throws IOException{
		Runtime.getRuntime().exec("taskKill /F /IM " + task);
	}

}
