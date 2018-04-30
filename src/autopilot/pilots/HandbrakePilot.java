package autopilot.pilots;

import autopilot.PilotPart;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import utils.Utils;

public class HandbrakePilot extends PilotPart {
	private float maxR;
	private boolean ended;
	private float time;

	@Override
	public AutopilotOutputs timePassed(AutopilotInputs input) {
		if (this.time == -1f)
			this.time = input.getElapsedTime() + 2f;
		
		if (input.getElapsedTime() < this.time)
			return Utils.buildOutputs(0, 0, 0, 0, 0, maxR, maxR, maxR);
		
		this.ended = true;
		return Utils.buildOutputs(0, 0, 0, 0, 0, 0, 0, 0);
	}

	@Override
	public String taskName() { return "Handbrake"; }

	@Override
	public void initialize(AutopilotConfig config) {
		this.maxR = config.getRMax();
		this.ended = false;
		this.time = -1f;
	}

	@Override
	public boolean ended() { return ended; }

	@Override
	public void close() { }
}