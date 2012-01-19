package jp.ac.nagoyau.is.ss.kishii.ob.launch;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import jp.ac.nagoyau.is.ss.kishii.ob.xml.Agent;
import jp.ac.nagoyau.is.ss.kishii.ob.xml.Team;
import rescuecore2.Constants;
import rescuecore2.components.ComponentConnectionException;
import rescuecore2.components.ComponentLauncher;
import rescuecore2.components.TCPComponentLauncher;
import rescuecore2.config.Config;
import rescuecore2.config.ConfigException;
import rescuecore2.connection.ConnectionException;
import rescuecore2.misc.CommandLineOptions;
import rescuecore2.registry.Registry;
import rescuecore2.standard.components.StandardAgent;
import rescuecore2.standard.entities.StandardEntityFactory;
import rescuecore2.standard.entities.StandardPropertyFactory;
import rescuecore2.standard.messages.StandardMessageFactory;

/**
 * Launcher for sample agents. This will launch as many instances of each of the
 * sample agents as possible, all using one connction.
 */
public final class LaunchSelectedAgent {
	private static final String FIRE_BRIGADE_FLAG = "-fb";
	private static final String POLICE_FORCE_FLAG = "-pf";
	private static final String AMBULANCE_TEAM_FLAG = "-at";
	private static final String CIVILIAN_FLAG = "-cv";

	private LaunchSelectedAgent() {
	}

	/**
	 * Launch 'em!
	 * 
	 * @param args
	 *            The following arguments are understood: -p <port>, -h
	 *            <hostname>, -fb <fire brigades>, -pf <police forces>, -at
	 *            <ambulance teams>
	 * @throws JAXBException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		try {
			long start = System.currentTimeMillis();
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

			JAXBContext context = JAXBContext
					.newInstance("jp.ac.nagoyau.is.ss.kishii.ob.xml");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			File file = new File("TeamConstitution.xml");
			JAXBElement<Team> obj = (JAXBElement<Team>) unmarshaller
					.unmarshal(file);
			Team team = obj.getValue();
			// CHECKSTYLE:ON:ModifiedControlVariable
			ComponentLauncher launcher = new TCPComponentLauncher(host, port,
					config);
			for (Agent agent : team.getAgent()) {
				connect(launcher, agent, config);
			}
			long end = System.currentTimeMillis();
			System.out.println("All agents are connected in " + (end - start)
					+ " [ms]");
		} catch (IOException e) {
			System.err.println("Error connecting agents");
		} catch (ConfigException e) {
			System.err.println("Configuration error");
		} catch (ConnectionException e) {
			System.err.println("Error connecting agents");
		} catch (InterruptedException e) {
			System.err.println("Error connecting agents");
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			System.err.println("Cannot create instance...");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("Cannot create instance...");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private static void connect(ComponentLauncher launcher, Agent agent,
			Config config) throws InterruptedException, ConnectionException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		String classPath = agent.getClassPath().trim();
		Integer num = agent.getNum();
		String[] data = classPath.split("\\.");
		String name = data[data.length - 1];
		Class<? extends StandardAgent<?>> clazz = (Class<? extends StandardAgent<?>>) Class
				.forName(classPath);
		int count = 0;
		try {
			while (num == null || num-- != 0) {
				launcher.connect(clazz.newInstance());
				System.out.println("connection success...:" + name + "("
						+ (count++) + ")");
			}
		} catch (ComponentConnectionException e) {
			System.out.println("No more " + name);
		}
	}
}
