package interfaces;

import physics.Motion;

public class AutopilotFactory {
	public static Autopilot createAutopilot() {
		return new Motion();
	}
}
