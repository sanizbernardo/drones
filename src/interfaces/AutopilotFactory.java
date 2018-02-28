package interfaces;

import pilot.Pilot;

public class AutopilotFactory {
	public static Autopilot createAutopilot() {
		return new Pilot(new int[] {});
	}
}
