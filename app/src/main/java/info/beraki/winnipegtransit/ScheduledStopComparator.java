package info.beraki.winnipegtransit;

import com.crashlytics.android.Crashlytics;

import java.util.Comparator;

import info.beraki.winnipegtransit.Adapter.StopAdapter;
import info.beraki.winnipegtransit.Model.Schedule.ScheduledStop;

/**
 * Created by Beraki on 3/27/2018.
 */

class ScheduledStopComparator implements Comparator<ScheduledStop> {

    @Override
    public int compare(ScheduledStop scheduledStop1, ScheduledStop scheduledStop2) {

        long scheduledArrivalEst1=0;
        long scheduledArrivalEst2=0;

        if(scheduledStop1 != null && scheduledStop2 != null) {

            String ISOEstString1,ISOEstString2;

            try {
                ISOEstString1 = scheduledStop1.getTimes().getArrival().getEstimated();
                ISOEstString2 = scheduledStop2.getTimes().getArrival().getEstimated();
                scheduledArrivalEst1 = StopActivity.getEtaFromTime(ISOEstString1);
                scheduledArrivalEst2 = StopActivity.getEtaFromTime(ISOEstString2);
            } catch (NullPointerException e) {
                Crashlytics.log(e.getCause() + "-" + scheduledStop1.toString() + scheduledStop2.toString());
            }
        }
        return Long.compare(scheduledArrivalEst1, scheduledArrivalEst2);
    }
}
