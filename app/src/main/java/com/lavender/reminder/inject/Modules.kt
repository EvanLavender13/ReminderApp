package com.lavender.reminder.inject

import android.content.Context
import androidx.room.Room
import com.lavender.reminder.data.reminder.ReminderDao
import com.lavender.reminder.data.reminder.ReminderDatabase
import com.lavender.reminder.data.work.WorkStateDao
import com.lavender.reminder.data.work.WorkStateDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReminderDatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): ReminderDatabase {
        return Room.databaseBuilder(
            context.applicationContext, ReminderDatabase::class.java, "Reminders.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideReminderDao(database: ReminderDatabase): ReminderDao = database.reminderDao()
}

@Module
@InstallIn(SingletonComponent::class)
object WorkStateDatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): WorkStateDatabase {
        return Room.databaseBuilder(
            context.applicationContext, WorkStateDatabase::class.java, "WorkState.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideWorkStateDao(database: WorkStateDatabase): WorkStateDao = database.workStateDao()
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IoDispatcher

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class DefaultDispatcher

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object CoroutinesModule {

    @Provides
    @IoDispatcher
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DefaultDispatcher
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Singleton
    @ApplicationScope
    fun providesCoroutineScope(
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}