package taller2.ataller2.services.location;

import android.content.Context;

import taller2.ataller2.model.LocationO;
import taller2.ataller2.services.CustomService;

public interface LocationService extends CustomService {
    LocationO getLocation(Context context);
}
