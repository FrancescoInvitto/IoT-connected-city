package it.unipr.iotproject2022.resources;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class VehiclesCounter extends CoapResource {

	private int carsGreen;
	private int carsRed;
	private List<String> plates;

	/**
	 * Class constructor.
	 * 
	 * @param name	the name of the resource.
	 */
	public VehiclesCounter(String name) {
		super(name);
		this.carsGreen = 0;
		this.carsRed = 0;
		this.plates = new ArrayList<>();
	}

	public int getCarsGreen() {
		return carsGreen;
	}

	public void setCarsGreen(int carsGreen) {
		this.carsGreen = carsGreen;
	}

	public int getCarsRed() {
		return carsRed;
	}

	public void setCarsRed(int carsRed) {
		this.carsRed = carsRed;
	}

	/**
	 * This method prepare a string that contains all the plates of the cars that
	 * have crossed with a red traffic light, separated by a ",".
	 * 
	 * @return	the string.
	 */
	private String prepareList() {
		String list = "";

		for(int i = 0; i < this.plates.size(); ++i)
			list += this.plates.get(i) + ",";

		return list;
	}

	public void handleGET(CoapExchange exchange) {
		String request = exchange.getRequestText();
		if(request.equals("nCars")) {
			int totalCars = this.carsGreen + this.carsRed;
			exchange.respond(ResponseCode.CONTENT, Integer.toString(totalCars), MediaTypeRegistry.TEXT_PLAIN);
		}
		else if(request.equals("redCars")) {
			String listOfPlates = prepareList();
			System.out.println("List of plates: " + listOfPlates);
			exchange.respond(ResponseCode.CONTENT, Integer.toString(carsRed) + ":" + listOfPlates, MediaTypeRegistry.TEXT_PLAIN);
		}
	}

	public void handlePOST(CoapExchange exchange) {
		String[] message = exchange.getRequestText().split(":");

		int lightColor = Integer.parseInt(message[0]);
		String plate = message[1];

		if(lightColor == 0) {
			this.carsRed++;
			this.plates.add(plate);
		}
		else {
			this.carsGreen++;
		}

		exchange.respond(ResponseCode.CREATED);
	}
}
