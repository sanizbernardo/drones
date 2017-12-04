package interfaces;

import physics.LogPilot;
import physics.Motion;

public class AutopilotFactory {
	public static Autopilot createAutopilot() {
		return new Motion();
	}
}
