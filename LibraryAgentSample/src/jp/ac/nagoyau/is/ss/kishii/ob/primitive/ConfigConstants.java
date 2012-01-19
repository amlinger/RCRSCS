package jp.ac.nagoyau.is.ss.kishii.ob.primitive;

import java.util.ArrayList;
import java.util.List;

import rescuecore2.Constants;
import rescuecore2.config.Config;

public class ConfigConstants {
	public final int MAX_POSSIBLE_SIMURATING_TIME;

	public final int EXTINGUISHABLE_POWER;
	public final int EXTINGUISHABLE_DISTANCE;
	public final int TANK_MAX;

	public final int HP_PERCEPTION_LOS;
	public final int DAMAGE_PERCEPTION_LOS;
	public final int RANGE_OF_VIEW;

	public final int MAX_CLEAR_DISTANCE;

	public final List<ChannelData> channelSettings;

	public static final int BUILDING_SQUARE_MEASURE_CORRECTION_VALUE = 1000000;

	public final int START_ACTION_TIME;
	public final boolean USESPEAK;

	/**
	 * 前回の移動が閉塞が原因で失敗したと判断する移動距離
	 */
	public static final int CANNOT_MOVE_DISTANCE = 2000;

	public ConfigConstants(Config config) {
		this.MAX_POSSIBLE_SIMURATING_TIME = 1000;
		this.EXTINGUISHABLE_POWER = config
				.getIntValue("fire.extinguish.max-sum");
		this.EXTINGUISHABLE_DISTANCE = config.getIntValue(
				"fire.extinguish.max-distance", 50000);
		this.TANK_MAX = config.getIntValue("fire.tank.maximum");
		this.HP_PERCEPTION_LOS = config.getIntValue(
				"perception.los.precision.hp", 1000);
		this.DAMAGE_PERCEPTION_LOS = config.getIntValue(
				"perception.los.precision.damage", 100);
		this.RANGE_OF_VIEW = config.getIntValue("perception.los.max-distance",
				3000);

		this.MAX_CLEAR_DISTANCE = config.getIntValue("clear.repair.distance",
				10000);

		this.USESPEAK = config.getValue(Constants.COMMUNICATION_MODEL_KEY)
				.equals("kernel.standard.ChannelCommunicationModel");
		this.START_ACTION_TIME = config.getIntValue(
				kernel.KernelConstants.IGNORE_AGENT_COMMANDS_KEY, 3);

		List<ChannelData> channelList = new ArrayList<ChannelData>();
		// System.out.println(config.getAllKeys());
		int channelCount = config.getIntValue("comms.channels.count", 0);
		for (int i = 0; i < channelCount; i++) {
			String preKey = "comms.channels." + i + ".";
			String type = config.getValue(preKey + "type");
			if (type != null) {
				if (type.equals("voice")) {
					int range = config.getIntValue(preKey + "range");
					int size = config.getIntValue(preKey + "messages.size");
					// String du = config.getValue(preKey
					// + "noise.input.dropout.use");
					double probability = 0;
					// if (du != null && du.equals("yes")) {
					// probability = config.getFloatValue(preKey
					// + "noise.input.dropout.p");
					// }
					channelList.add(new VoiceChannelData(i, range, size,
							probability));
				} else if (type.equals("radio")) {
					int size = config.getIntValue(preKey + "bandwidth");
					// String du = config.getValue(preKey
					// + "noise.input.dropout.use");
					double dProbability = 0;
					// if (du != null && du.equals("yes")) {
					// dProbability = config.getFloatValue(preKey
					// + "noise.input.dropout.p");
					// }
					// String fu = config.getValue(preKey
					// + "noise.input.failure.use");
					double fProbability = 0;
					// if (du != null && du.equals("yes")) {
					// fProbability = config.getFloatValue(preKey
					// + "noise.input.dropout.p");
					// }
					channelList.add(new RadioChannelData(i, size, dProbability,
							fProbability));
				}
			}
		}
		this.channelSettings = channelList;

	}
	// kernel.agents.think-time
	// kernel.startup.connect-time
	// kernel.communication-model
	// kernel.agents.ignoreuntil
	// kernel.perception

	// comms.channels.1.bandwidth
	// comms.channels.2.bandwidth
	// comms.channels.0.messages.max
	// comms.channels.3.type
	// comms.channels.2.type
	// comms.channels.0.range
	// comms.channels.count
	// comms.channels.max.platoon
	// comms.channels.max.centre
	// comms.channels.3.bandwidth
	// comms.channels.0.type
	// comms.channels.0.messages.size
	// comms.channels.1.type

	// fire.extinguish.max-sum
	// fire.tank.maximum
	// fire.extinguish.max-distance

	// clear.repair.rate
	// clear.repair.distance

	// scenario.agents.po
	// scenario.agents.fb
	// scenario.agents.ac
	// scenario.agents.at
	// scenario.agents.pf
	// scenario.agents.fs

}
