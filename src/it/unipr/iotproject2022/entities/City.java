package it.unipr.iotproject2022.entities;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class City{

	private static final int N = 70;
	private static final int NBRIDGES = 3;
	private static final int MAXPOOL = 100;
	private static final long IDLETIME = 5000;
	private ThreadPoolExecutor entities;

	/**
	 * Class constructor.
	 */
	public City() {
		this.entities = new ThreadPoolExecutor(N, MAXPOOL, IDLETIME,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	/**
	 * This method prints the information of the simulation.
	 */
	private void printSimulationInfo() {
		System.out.println("\nStarting simulation with: " + N + " citizens, a police station and " + NBRIDGES + " bridges");
		System.out.println("The simulation will start in 3 seconds...");

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method defines the behavior.
	 * First of all the information of the simulation is printed on the console.
	 * Then N citizens are created and executed and the police is started.
	 */
	private void start() {
		printSimulationInfo();

		for(int i = 0; i < N; ++i) {
			this.entities.execute(new Citizen(new Random().nextInt(NBRIDGES) + 1, NBRIDGES, new Random().nextInt(2) + 1, i + 1));

			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.entities.execute(new Police(NBRIDGES));
		this.entities.shutdown();
	}

	/**
	 * This method starts the execution.
	 * 
	 * @param args	no arguments needed.
	 */
	public static void main(String[] args) {
		new City().start();
	}

}
