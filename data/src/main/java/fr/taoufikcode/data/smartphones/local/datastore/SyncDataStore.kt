package fr.taoufikcode.data.smartphones.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map

class SyncDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val HOME_LAST_SYNC = longPreferencesKey("home_last_sync")
    }

    fun lastSyncDateHome() =
        dataStore.data.map { preferences -> preferences[HOME_LAST_SYNC] ?: 0L }


    suspend fun saveSyncDateHome(timestamp: Long) {
        dataStore.edit { it[HOME_LAST_SYNC] = timestamp }
    }

}
