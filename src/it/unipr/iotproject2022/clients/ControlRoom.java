package it.unipr.iotproject2022.clients;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ControlRoom {

	private static final int T = 5000;

	private static final int NBRIDGES = 3;
	private static final int MAXPOOL    = 100;
	private static final long IDLETIME  = 5000;
	private ThreadPoolExecutor controllers;

	/**
	 * Class constructor.
	 */
	public ControlRoom() {
		this.controllers = new ThreadPoolExecutor(NBRIDGES, MAXPOOL, IDLETIME,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	/**
	 * This method creates and launches the controllers of the bridges.
	 */
	private void start() {
		for(int i = 0; i < NBRIDGES; ++i) {
			this.controllers.execute(new Controller(i + 1, T));

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method starts the execution.
	 * 
	 * @param args	no arguments needed.
	 */
	public static void main(String[] args) {
		new ControlRoom().start();
	}

}
