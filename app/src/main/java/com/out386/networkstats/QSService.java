package com.out386.networkstats;

import android.content.Intent;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class QSService extends TileService {

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        tile.setState(Tile.STATE_INACTIVE);
        tile.updateTile();

        startForegroundService(new Intent(this, MyService.class));
        super.onStartListening();
    }
}
