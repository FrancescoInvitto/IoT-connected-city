package it.unipr.iotproject2022.servers;

import org.eclipse.californium.core.CoapServer;

import it.unipr.iotproject2022.resources.TrafficLight;
import it.unipr.iotproject2022.resources.VehiclesCounter;

public class Bridges extends CoapServer {

	private static final int NBRIDGES = 3;

	public Bridges() {
	}

	public Bridges(int... ports) {
		super(ports);
	}

	/**
	 * This method creates the bridges and starts the CoAP server.
	 * Each bridge is represented by a pair of traffic lights.
	 * Each bridge has associated a vehicles counter.
	 * 
	 * @param args	no arguments needed.
	 */
	public static void main(String[] args) {
		Bridges b = new Bridges(5683);

		TrafficLight t_left;
		TrafficLight t_right;
		VehiclesCounter c;

		for(int i = 1; i <= NBRIDGES; ++i) {
			t_left = new TrafficLight("l" + i + "-1");
			t_left.setObservable(true);
			t_left.getAttributes().setObservable();

			t_right = new TrafficLight("l" + i + "-2");
			t_right.setObservable(true);
			t_right.getAttributes().setObservable();

			c = new VehiclesCounter("c" + i);

			b.add(t_left, t_right, c);
		}

		b.start();
	}

}
