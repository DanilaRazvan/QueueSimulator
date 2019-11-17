package model;

public class Client {
	
	private int id;
	private int arrivalTime;
	private int finishTime;
	private int processingPeriod;
	
	public Client() {
	}
	
	public Client(int id, int arrivalTime, int finishTime, int processingPeriod) {
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.finishTime = finishTime;
		this.processingPeriod = processingPeriod;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}

	public int getProcessingPeriod() {
		return processingPeriod;
	}

	public void setProcessingPeriod(int processingPeriod) {
		this.processingPeriod = processingPeriod;
	}

	@Override
	public String toString() {
		String s = "ID: " + id + " Arrival: " + arrivalTime + " Processing: " + processingPeriod + "\n";
		return s;
	}
}
