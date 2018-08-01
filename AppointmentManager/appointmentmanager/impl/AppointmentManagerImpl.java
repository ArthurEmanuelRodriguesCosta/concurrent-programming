package appointmentmanager.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import appointmentmanager.interfaces.Appointment;
import appointmentmanager.interfaces.AppointmentManager;
import appointmentmanager.interfaces.AppointmentNotifier;

public class AppointmentManagerImpl implements AppointmentManager {
	
	private static CopyOnWriteArrayList<Appointment> appointments;
	private static AppointmentNotifier appNotifier;
	private static UtilsImpl utils;
	
	private final static Long HOUR_IN_MILISECONDS = 1000 * 60 * 60L;
	private final static Long MINUTE_IN_MILISECONDS = 1000 * 60L;

	public AppointmentManagerImpl() {
		AppointmentManagerImpl.appointments = new CopyOnWriteArrayList<>();
		AppointmentManagerImpl.appNotifier = new AppointmentNotifierImpl();
		AppointmentManagerImpl.utils = new UtilsImpl();
	}

	@Override
	public boolean addAppointment(Appointment toAddAppointment) {
		return AppointmentManagerImpl.appointments.add(toAddAppointment);
	}

	@Override
	public boolean cancel(int appointmentId) {
		
		for (int i = 0; i < appointments.size(); i++) {
			AppointmentImpl currAppointment = (AppointmentImpl) appointments.get(i);
			if (currAppointment.getId() == appointmentId) {
				return appointments.remove(i) != null;
			}
		}
		
		return false;
	}

	
	public static void main(String[] args) throws InterruptedException {

		AppointmentManagerImpl appManager = new AppointmentManagerImpl();
		
		Thread t0 = new Thread(appManager.appointmentNotifier);
		Thread t1 = new Thread(appManager.canceller);
				
		t0.start();
		t1.start();
		
		for (int i = 0; i < 3; i++) {
			Appointment newAppointment = new AppointmentImpl(i, "appointment id: " + i, System.currentTimeMillis() + MINUTE_IN_MILISECONDS * i, 1000 * 60 * 2L);
			appManager.addAppointment(newAppointment);
		}
		
	}
	
	public Runnable appointmentNotifier = new Runnable() {
        public void run() {
            while (true) {
	        	for (Appointment appointment : appointments) {
	        		Long timeToStart = utils.milliSecondsUntil(appointment.start());
	        		Long timeInSeconds = timeToStart / 1000;
	        		if (timeInSeconds <= 60 && (timeToStart % (1000 * 15) == 0) && (timeToStart > 0)) {
	        			appNotifier.notify(appointment);
	        		}
	        		
	        		if (appointment.start() == System.currentTimeMillis())
        				System.out.println("Appointment " + appointment.getId() + " starting now.");
	        	}
            }
        }
    };
 
    public Runnable canceller = new Runnable() {
        public void run() {
            while (true) {
            	
            	Scanner sc = new Scanner(System.in);
            	
            	int appointmentId = sc.nextInt();
            	
            	cancel(appointmentId);
            	System.out.println(AppointmentManagerImpl.appointments);
            }
       }
    };

}
