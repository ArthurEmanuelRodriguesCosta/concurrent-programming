package appointmentmanager.impl;

import appointmentmanager.interfaces.Utils;

public class UtilsImpl implements Utils {

	@Override
	public long milliSecondsUntil(long timeStamp) {
		long mSecUntil = timeStamp - System.currentTimeMillis();
		return mSecUntil;
	}
	
}
