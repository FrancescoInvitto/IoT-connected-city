package it.unipr.iotproject2022.entities;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Request;

public class Police implements Runnable{

	private int nBridges;
	CoapClient client;
	private int vehicles[];
	private int badGuys;
	private List<String> badGuysList;

	/**
	 * Class constructor.
	 * 
	 * @param n		the total number of bridges.
	 */
	public Police(int n) {
		this.nBridges = n;
		this.client = new CoapClient();
		this.vehicles = new int[this.nBridges];
		this.badGuysList = new ArrayList<>();
	}

	/**
	 * This method sums the number of vehicles that have crossed all the bridges.
	 * 
	 * @return		the sum of vehicles.
	 */
	private int sumVehicles() {
		int sum = 0;

		for(int i = 0; i < this.vehicles.length; ++i)
			sum += this.vehicles[i];

		return sum;
	}

	/**
	 * This method prints on the console the traffic statistics, including:
	 * 		- the number of cars that have crossed each single bridge
	 * 		- the total number of cars that have crossed all the bridges
	 * 		- the number of cars that have crossed with a red traffic light
	 * 		- the list of plates of cars that have crossed with a red traffic light.
	 */
	private void printTrafficStatistics() { 
		System.out.println("\n----- Police station -----");

		for(int i = 0; i < this.vehicles.length; ++i) {
			System.out.println("Number of cars that have crossed the bridge b" + (i + 1) + ": " + this.vehicles[i] + " (" + (this.vehicles[i] * 100) / sumVehicles() + "%)" );
		}

		System.out.println("Total cars: " + sumVehicles());
		System.out.println("Number of cars that have crossed with a red traffic light: " + this.badGuys + " (" + (this.badGuys * 100) / sumVehicles() + "% of total)");
		System.out.println("List of plates of cars that have crossed with a red traffic light:");
		for(int j = 0; j < this.badGuysList.size(); ++j)
			System.out.println(this.badGuysList.get(j));

	}

	/**
	 * This method asks the vehicles counter of the bridges the number of cars
	 * that have crossed with a red traffic light.
	 */
	private void checkBadGuys() {
		this.badGuys = 0;

		for(int i = 1; i <= this.nBridges; ++i) {
			this.client.setURI("coap:localhost:5683/c" + i);

			Request r = new Request(Code.GET);
			r.setPayload("redCars");

			String[] message = this.client.advanced(r).getResponseText().split(":");

			this.badGuys += Integer.parseInt(message[0]);

			String[] list = message[1].split(",");

			for(int j = 0; j < list.length; ++j)
				this.badGuysList.add(list[j]);		
		}
	}

	/**
	 * This method asks the vehicles counter of the bridges the number of cars
	 * that have crossed them.
	 */
	private void checkVehicleNumber() {
		for(int i = 1; i <= this.nBridges; ++i) {
			this.client.setURI("coap:localhost:5683/c" + i);
			Request r = new Request(Code.GET);
			r.setPayload("nCars");
			vehicles[i - 1] = Integer.parseInt(this.client.advanced(r).getResponseText());
		}
	}
	
	/**
	 * This method defines the behavior of the police station.
	 */
	@Override
	public void run() {
		checkVehicleNumber();
		checkBadGuys();
		printTrafficStatistics();
	}

}
