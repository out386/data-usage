package com.out386.networkstats;

import android.content.Intent;
import android.service.quicksettings.TileService;

public class QSService extends TileService {
    @Override
    public void onStartListening() {
        super.onStartListening();
        startForegroundService(new Intent(this, MyService.class));
    }
}
