package com.sood.market.data.scheduler;

import jakarta.inject.Singleton;

@Singleton
public class SnapshotManager {

    private boolean snapshotSaved = false;

    public boolean shouldSaveSnapshot() {
        return !snapshotSaved;
    }

    public void markSnapshotSaved() {
        snapshotSaved = true;
    }

    public void reset() {
        snapshotSaved = false;
    }
}