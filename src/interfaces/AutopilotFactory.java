package interfaces;

import pilot.Pilot;

public class AutopilotFactory {
	public static Autopilot createAutopilot() {
		return new Pilot(new int[] {Pilot.WAIT_PATH, Pilot.TAKING_OFF,  Pilot.FLYING, Pilot.LANDING, Pilot.TAXIING});
	}
}
