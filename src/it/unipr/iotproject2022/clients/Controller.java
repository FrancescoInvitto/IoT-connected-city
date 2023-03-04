package it.unipr.iotproject2022.clients;

import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;

public class Controller implements Runnable{

	private static final String BASEURI = "coap:localhost:5683/l";
	private int bridgeNumber;
	private int swapPeriod;
	private CoapClient p;
	private String uri1, uri2;

	/**
	 * Class constructor.
	 * 
	 * @param bN	the bridge number the controller has to manage.
	 * @param t		the swapping period.
	 */	
	public Controller(int bN, int t) {
		this.bridgeNumber = bN;
		this.swapPeriod = t;
		this.uri1 = BASEURI + this.bridgeNumber + "-" + "1";
		this.uri2 = BASEURI + this.bridgeNumber + "-" + "2";
		this.p = new CoapClient();
	}
	
	/**
	 * This method swaps the status of the pair of traffic lights.
	 * 
	 * @param req	the CoAP request.
	 */
	private void swapStatus(Request req) {
		try {
			Thread.sleep(this.swapPeriod);

			p.setURI(uri1);

			req = new Request(Code.PUT);
			p.advanced(req);

			p.setURI(uri2);

			req = new Request(Code.PUT);
			p.advanced(req);				
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method initializes the pair of traffic lights.
	 */	
	private void initializeTrafficLights() {
		this.p.setURI(uri1);
		
		int first = ThreadLocalRandom.current().nextInt(2);
		int second = 1 - first;

		Request init = new Request(Code.POST);
		init.setPayload("init" + Integer.toString(first));

		p.advanced(init);

		p.setURI(uri2);
		init = new Request(Code.POST);
		init.setPayload("init" + Integer.toString(second));
		p.advanced(init);
	}

	/**
	 * This method starts the execution of the controller.
	 */	
	@Override
	public void run() {
		initializeTrafficLights();
		
		Request swap = null;

		while(true) 
			swapStatus(swap);	
	}

}
