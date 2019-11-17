package model;

import java.util.*;

public interface Strategy {
	public enum SelectionPolicy {
		SHORTEST_TIME, SHORTEST_QUEUE
	}
	
	public void addTask(List<Server> servers, Client c) throws Exception;
}
