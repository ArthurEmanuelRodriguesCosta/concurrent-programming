package appointmentmanager.impl;

import appointmentmanager.interfaces.Appointment;
import appointmentmanager.interfaces.AppointmentNotifier;

public class AppointmentNotifierImpl implements AppointmentNotifier {
	
	public AppointmentNotifierImpl() {
		
	}

	@Override
	public void notify(Appointment app) {
		System.out.println("Notifying appointment " + app.getId());
	}
}
