package appointmentmanager.interfaces;

public interface AppointmentManager {

	public boolean addAppointment (Appointment toAddAppointment);
	
	public boolean cancel(int appointmentId);
}
