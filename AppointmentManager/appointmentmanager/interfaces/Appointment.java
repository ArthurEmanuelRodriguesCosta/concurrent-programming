package appointmentmanager.interfaces;

public interface Appointment {

	public int getId();
	
	public String getDescription();
	
	public Long start();
	
	public Long duration();
}
