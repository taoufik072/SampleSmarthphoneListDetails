package fr.taoufikcode.discover.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.taoufikcode.data.smartphones.local.SmartphoneDatabase
import fr.taoufikcode.data.smartphones.local.dao.HomeDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSmartphoneDatabase(
        @ApplicationContext context: Context
    ): SmartphoneDatabase {
        return Room.databaseBuilder(
            context,
            SmartphoneDatabase::class.java,
            "smartphone_database"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideHomeDao(database: SmartphoneDatabase): HomeDao {
        return database.homeListDao()
    }
}
