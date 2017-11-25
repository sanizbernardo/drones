package interfaces;

import physics.LogPilot;

public class AutopilotFactory {
	public static Autopilot createAutopilot() {
		return new LogPilot();
	}
}
