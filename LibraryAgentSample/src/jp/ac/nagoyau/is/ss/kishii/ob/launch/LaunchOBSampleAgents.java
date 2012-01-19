package jp.ac.nagoyau.is.ss.kishii.ob.launch;

import java.io.IOException;

import jp.ac.nagoyau.is.ss.kishii.ob.team.sample.SampleOBAmbulanceTeam;
import jp.ac.nagoyau.is.ss.kishii.ob.team.sample.SampleOBFireBrigade;
import jp.ac.nagoyau.is.ss.kishii.ob.team.sample.SampleOBPoliceForce;
import rescuecore2.Constants;
import rescuecore2.components.ComponentConnectionException;
import rescuecore2.components.ComponentLauncher;
import rescuecore2.components.TCPComponentLauncher;
import rescuecore2.config.Config;
import rescuecore2.config.ConfigException;
import rescuecore2.connection.ConnectionException;
import rescuecore2.misc.CommandLineOptions;
import rescuecore2.registry.Registry;
import rescuecore2.standard.entities.StandardEntityFactory;
import rescuecore2.standard.entities.StandardPropertyFactory;
import rescuecore2.standard.messages.StandardMessageFactory;
import sample.SampleCentre;

/**
 * Launcher for sample agents. This will launch as many instances of each of the
 * sample agents as possible, all using one connction.
 */
public final class LaunchOBSampleAgents {
	private static final String FIRE_BRIGADE_FLAG = "-fb";
	private static final String POLICE_FORCE_FLAG = "-pf";
	private static final String AMBULANCE_TEAM_FLAG = "-at";
	private static final String CIVILIAN_FLAG = "-cv";

	private LaunchOBSampleAgents() {
	}

	/**
	 * Launch 'em!
	 * 
	 * @param args
	 *            The following arguments are understood: -p <port>, -h
	 *            <hostname>, -fb <fire brigades>, -pf <police forces>, -at
	 *            <ambulance teams>
	 */
	public static void main(String[] args) {
		try {
			Registry.SYSTEM_REGISTRY
					.registerEntityFactory(StandardEntityFactory.INSTANCE);
			Registry.SYSTEM_REGISTRY
					.registerMessageFactory(StandardMessageFactory.INSTANCE);
			Registry.SYSTEM_REGISTRY
					.registerPropertyFactory(StandardPropertyFactory.INSTANCE);
			Config config = new Config();
			args = CommandLineOptions.processArgs(args, config);
			int port = config.getIntValue(Constants.KERNEL_PORT_NUMBER_KEY,
					Constants.DEFAULT_KERNEL_PORT_NUMBER);
			String host = config.getValue(Constants.KERNEL_HOST_NAME_KEY,
					Constants.DEFAULT_KERNEL_HOST_NAME);
			int fb = -1;
			int pf = -1;
			int at = -1;
			// CHECKSTYLE:OFF:ModifiedControlVariable
			for (int i = 0; i < args.length; ++i) {
				if (args[i].equals(FIRE_BRIGADE_FLAG)) {
					fb = Integer.parseInt(args[++i]);
				} else if (args[i].equals(POLICE_FORCE_FLAG)) {
					pf = Integer.parseInt(args[++i]);
				} else if (args[i].equals(AMBULANCE_TEAM_FLAG)) {
					at = Integer.parseInt(args[++i]);
				} else {
					System.err.println("Unrecognised option: " + args[i]);
				}
			}
			// CHECKSTYLE:ON:ModifiedControlVariable
			ComponentLauncher launcher = new TCPComponentLauncher(host, port,
					config);
			connect(launcher, fb, pf, at, config);
		} catch (IOException e) {
			System.err.println("Error connecting agents");
		} catch (ConfigException e) {
			System.err.println("Configuration error");
		} catch (ConnectionException e) {
			System.err.println("Error connecting agents");
		} catch (InterruptedException e) {
			System.err.println("Error connecting agents");
		}
	}

	private static void connect(ComponentLauncher launcher, int fb, int pf,
			int at, Config config) throws InterruptedException,
			ConnectionException {
		int i = 0;
		try {
			while (fb-- != 0) {
				System.out.println("Connecting fire brigade " + (i++) + "...");
				launcher.connect(new SampleOBFireBrigade());
				System.out.println("success");
			}
		} catch (ComponentConnectionException e) {
			System.out.println("failed: " + e.getMessage());
		}
		try {
			while (pf-- != 0) {
				System.out.println("Connecting police force " + (i++) + "...");
				launcher.connect(new SampleOBPoliceForce());
				System.out.println("success");
			}
		} catch (ComponentConnectionException e) {
			System.out.println("failed: " + e.getMessage());
		}
		try {
			while (at-- != 0) {
				System.out
						.println("Connecting ambulance team " + (i++) + "...");
				launcher.connect(new SampleOBAmbulanceTeam());
				System.out.println("success");
			}
		} catch (ComponentConnectionException e) {
			System.out.println("failed: " + e.getMessage());
		}
		// try {
		// while (at-- != 0) {
		// System.out.println("Connecting fire stateion " + (i++) + "...");
		// launcher.connect(new OBFireStation());
		// System.out.println("success");
		// }
		// } catch (ComponentConnectionException e) {
		// System.out.println("failed: " + e.getMessage());
		// }
		try {
			while (true) {
				System.out.println("Connecting centre " + (i++) + "...");
				launcher.connect(new SampleCentre());
				System.out.println("success");
			}
		} catch (ComponentConnectionException e) {
			System.out.println("failed: " + e.getMessage());
		}
	}
}
