package it.unipr.iotproject2022.entities;

import java.util.Random;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Request;

public class Citizen implements Runnable{

	private int bridgeNumber;
	private int startingSide;
	private int id;
	private boolean choosen = false;
	private CoapClient client;
	private int nBridges;

	/**
	 * Class constructor.
	 * 
	 * @param n		the bridge number.
	 * @param b		the total number of bridges.
	 * @param s		the starting side.
	 * @param i		the citizen ID.
	 */
	public Citizen(int n, int b, int s, int i) {
		this.bridgeNumber = n;
		this.nBridges = b;
		this.startingSide = s;
		this.id = i;
		this.client = new CoapClient();
	}

	public int getBridgeNumber() {
		return bridgeNumber;
	}

	public void setBridgeNumber(int bridgeNumber) {
		this.bridgeNumber = bridgeNumber;
	}

	public int getStartingSide() {
		return startingSide;
	}

	public void setStartingSide(int startingSide) {
		this.startingSide = startingSide;
	}

	/**
	 * This method randomly selects a bridge.
	 * 
	 * @return	the bridge number.
	 */
	private int chooseAnotherBridge() {
		return new Random().nextInt(this.nBridges) + 1;
	}

	/**
	 * This method implements the decision policy.
	 * If the queue length of the traffic light of the currently selected bridge
	 * is at most 3, the citizen chooses to cross that bridge.
	 * 
	 * @param queueLength	the length of the queue.
	 * @return				the decision (true = cross, false = not cross)
	 */
	private boolean takeDecision(int queueLength) {
		if(queueLength <= 3)
			return true;
		else
			return false;
	}

	/**
	 * This method requests the status of the traffic light, in particular the queue length.
	 * 
	 * @param b		the bridge number.
	 * @return		the queue length.
	 */
	private int requestBridgeStatus(int b) {
		this.client.setURI("coap:localhost:568" + this.bridgeNumber + 2 + "/l" + b + "-" + this.startingSide);
		Request req = new Request(Code.GET);
		req.setPayload("queue-status");

		return Integer.parseInt(this.client.advanced(req).getResponseText());
	}

	/**
	 * This method simply converts the starting side to a string.
	 * 
	 * @param s		the starting side.
	 * @return		the appropriate string.
	 */
	private String convertSide(int s) {
		if(s == 1)
			return "left";

		return "right";
	}

	/**
	 * This method prints on the console the information related to the citizen, including:
	 * 		- the starting side
	 * 		- the bridge to which request the status.
	 */
	private void printInfo() {
		System.out.println("\n------------- Citizen " + this.id + " ------------\n"
				+ "Starting side: " + convertSide(this.startingSide) + "\n"
				+ "Requesting status of bridge: b" + this.bridgeNumber + "\n"
				+ "-------------------------------------\n");
	}

	/**
	 * This method runs the behavior of the citizen.
	 * First of all the citizen information is printed on the console.
	 * Then the citizen has to choose the bridge to cross; so while it has not
	 * decided yet, a citizen request the length of the queue, then it uses the decision
	 * policy. If the decision is true (cross the currently selected bridge), then the
	 * citizen creates a new vehicle, otherwise it has to randomly select a new bridge.
	 */
	@Override
	public void run() {
		while(!this.choosen) {
			printInfo();
			int status = requestBridgeStatus(this.bridgeNumber);

			boolean decision = takeDecision(status);

			if(decision) {
				System.out.println("Citizen " + this.id + ": I will cross the bridge b" + this.bridgeNumber);
				this.choosen = true;
				new Vehicle(this.bridgeNumber, this.startingSide, this.id).run();
			}
			else {
				System.out.println("Citizen " + this.id + ": too many cars waiting... choosing another bridge");
				int currBridge = this.bridgeNumber; 

				while(this.bridgeNumber == currBridge)
					this.bridgeNumber = chooseAnotherBridge();

				System.out.println("Citizen " + this.id + ": chosen bridge b" + this.bridgeNumber);
			}
		}
	}

}
