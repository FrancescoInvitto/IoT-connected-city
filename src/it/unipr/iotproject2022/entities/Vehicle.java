package it.unipr.iotproject2022.entities;

import java.util.UUID;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;

public class Vehicle implements Runnable{

	private final String plate;
	private CoapClient client;
	private int bridgeNumber;
	private int citizenID;
	private int startingSide;
	private boolean isGreen;
	private boolean isBadGuy;

	/**
	 * Class constructor.
	 * 
	 * @param bridge	the number of the bridge to cross.
	 * @param side		the starting side.
	 * @param citID		the citizen ID.
	 */
	public Vehicle(int bridge, int side, int citID) {
		this.plate = UUID.randomUUID().toString().substring(0, 7);
		this.citizenID = citID;
		this.bridgeNumber = bridge;
		this.startingSide = side;
		this.isBadGuy = false;
		this.isGreen = false;
		this.client = new CoapClient();
	}

	public String getId() {
		return plate;
	}

	/**
	 * This method is used to cross the bridge.
	 * First of all a DELETE request is sent to the bridge in order
	 * to remove the plate from the queue. Then a POST request is sent
	 * to the vehicles counter of the bridge, that will increment the 
	 * number of vehicles that have crossed.
	 * 
	 * @param status	the color of the traffic light.
	 */
	private void crossBridge(int status) {
		Request cross = new Request(Code.DELETE);
		cross.setPayload(this.plate);

		if(this.client.advanced(cross).getCode() == ResponseCode.DELETED) {
			System.out.println("Vehicle of citizen " + this.citizenID + ": I have crossed the bridge b" + this.bridgeNumber);
			this.client.setURI("coap:localhost:5683/c" + this.bridgeNumber);
			cross = new Request(Code.POST);
			cross.setPayload(Integer.toString(status) + ":" + this.plate);
			this.client.advanced(cross);
		}
		else
			System.out.println("Vehicle of citizen " + this.citizenID + ": error on crossing the bridge b" + this.bridgeNumber);
	}

	private void updateStatus() {
		this.isGreen = true;
	}

	/**
	 * This method is used to request the light color of the traffic light.
	 * 
	 * @return the observe relation to the resource.
	 */
	private CoapObserveRelation requestBridgeStatus() {		
		return this.client.observe(new CoapHandler() {

			@Override
			public void onLoad(CoapResponse response) {
				int color = Integer.parseInt(response.getResponseText());

				if(color == 1) 
					updateStatus();
			}

			@Override
			public void onError() {
				System.err.println("Error on observing the resource.");
			}

		});
	}

	/**
	 * This method sends the plate of the vehicle to the traffic light.
	 */
	private void sendId() {
		this.client.setURI("coap:localhost:5683/l" + this.bridgeNumber + "-" + this.startingSide);

		Request req = new Request(Code.POST);
		req.setType(Type.NON);
		req.setPayload(this.plate, MediaTypeRegistry.TEXT_PLAIN);
		this.client.advanced(req);	
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
	 * This method prints on the console the information of the vehicle, including:
	 * 		- the plate number
	 * 		- the starting side
	 * 		- the bridge to cross.
	 */
	private void printInfo() {
		System.out.println("\n-------- Vehicle of citizen " + this.citizenID + " --------\n"
				+ "Plate: " + this.plate + "\n"
				+ "Starting side: " + convertSide(this.startingSide) + "\n"
				+ "Bridge to cross: b" + this.bridgeNumber + "\n"
				+ "---------------------------------------\n");
	}

	/**
	 * This method is used to choose the bad guys.
	 * If the citizen ID is a multiple of 10, the vehicles behaves as a bad guy.
	 */
	private void defineBehavior() {
		if((this.citizenID % 10) == 0)
			this.isBadGuy = true;
	}

	/**
	 * This method runs the vehicle.
	 * First of all it is selected how the vehicles behaves.
	 * Then the information of the vehicle are printed on the console.
	 * Then the plate is sent to the traffic light, before asking its status.
	 * If the behavior is "normal", the vehicles will wait for the traffic light
	 * turning green, otherwise it will cross anyway, even if the color is red.
	 */
	public void run() {
		defineBehavior();
		printInfo();
		sendId();

		CoapObserveRelation obs = requestBridgeStatus();

		if(!this.isBadGuy) {
			while(!this.isGreen) {	
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			System.out.println("Vehicle of citizen " + this.citizenID + ": it's green, I will pass.");
			crossBridge(1);
		}
		else {
			System.out.println("Vehicle of citizen " + this.citizenID + ": I don't care about the light color, I will pass anyway.");
			if(this.isGreen)
				crossBridge(1);
			else
				crossBridge(0);
		}

		obs.reactiveCancel();

	}	

}
