package appointmentmanager.impl;

import appointmentmanager.interfaces.Appointment;

public class AppointmentImpl implements Appointment {
	
	private int id;
	private String description;
	private Long start;
	private Long duration;
	private boolean isActive;
	
	public AppointmentImpl(int id, String description, Long start, Long duration) {
		this.id = id;
		this.description = description;
		this.start = start;
		this.duration = duration;
		this.isActive = true;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public Long start() {
		return this.start;
	}

	@Override
	public Long duration() {
		return this.duration;
	}

}
