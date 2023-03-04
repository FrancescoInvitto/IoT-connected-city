package it.unipr.iotproject2022.resources;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class TrafficLight extends CoapResource {

	private int lightColor;	//1 = green light, 0 = red light
	private String label;
	private List<String> queue; 

	/**
	 * Class constructor.
	 * 
	 * @param name	the name of the resource.
	 */
	public TrafficLight(String name) {
		super(name);
		this.label = name;
		this.queue = new ArrayList<>();	
	}

	public int getLightColor() {
		return lightColor;
	}

	public void setLightColor(int lightColor) {
		this.lightColor = lightColor;
	}

	public List<String> getQueue() {
		return queue;
	}

	public void setQueue(List<String> queue) {
		this.queue = queue;
	}

	public void handleGET(CoapExchange exchange) {
		if(exchange.getRequestText().contains("queue-status"))
			exchange.respond(ResponseCode.CONTENT, Integer.toString(this.queue.size()), MediaTypeRegistry.TEXT_PLAIN);
		else
			exchange.respond(ResponseCode.CONTENT, Integer.toString(this.lightColor), MediaTypeRegistry.TEXT_PLAIN);
	}

	public void handlePUT(CoapExchange exchange) {
		swapColor();
		exchange.respond(ResponseCode.CHANGED, Integer.toString(this.lightColor), MediaTypeRegistry.TEXT_PLAIN);
		this.changed();
	}
	
	public void handleDELETE(CoapExchange exchange) {
		if(this.queue.remove(exchange.getRequestText()))
			exchange.respond(ResponseCode.DELETED);
		else
			exchange.respond(ResponseCode.NOT_FOUND);		
	}

	public void handlePOST(CoapExchange exchange) {
		String m = exchange.getRequestText();
		if(m.contains("init")) {
			this.lightColor = Integer.parseInt(m.replaceAll("[^\\d.]", "")); //removes all non-numbers characters
			printColor(this.lightColor);
		}
		else {
			this.queue.add(m);
			System.out.println("Added to queue of " + this.label + ": " + m);
			printQueue();
		}
		exchange.respond(ResponseCode.CREATED);
	}
	
	/**
	 * This method prints the elements of the queue.
	 */
	private void printQueue() {
		System.out.println(this.label + " queue:");
		
		for(int i = 0; i < this.queue.size(); ++i)
			System.out.println(queue.get(i));
		
		System.out.println("\n");
	}

	/**
	 * This method prints the color of the traffic light.
	 * 
	 * @param c		the light color.
	 */
	private void printColor(int c) {
		if(c == 0)
			System.out.println(this.label + ": Red light");
		else
			System.out.println(this.label + ": Green light");
	}

	/**
	 * This method swaps the color of the light.
	 */
	private void swapColor() {
		if(this.lightColor == 0)
			this.lightColor = 1;	
		else 
			this.lightColor = 0;	

		printColor(this.lightColor);
	}

}
