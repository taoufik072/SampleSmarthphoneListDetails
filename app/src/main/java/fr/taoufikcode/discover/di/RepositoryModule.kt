package fr.taoufikcode.discover.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.taoufikcode.data.smartphones.repository.SmartphoneDetailsRepositoryImpl
import fr.taoufikcode.data.smartphones.repository.SmartphonesSummaryRepositoryImpl
import fr.taoufikcode.domain.repository.home.SmartphonesSummaryRepository
import fr.taoufikcode.domain.repository.smartphone.SmartphoneDetailsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(impl: SmartphonesSummaryRepositoryImpl): SmartphonesSummaryRepository

    @Singleton
    @Binds
    abstract fun bindSmartphoneRepository(impl: SmartphoneDetailsRepositoryImpl): SmartphoneDetailsRepository
}
