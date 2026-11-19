package com.mastercash.mastercash.data.db;

import android.content.Context;
import androidx.room.Room;
import com.google.dagger.hilt.android.qualifiers.ApplicationContext;
import com.google.dagger.hilt.android.installIn;
import com.google.dagger.hilt.android.components.ApplicationComponent;
import com.mastercash.mastercash.data.AppDatabase;
import com.mastercash.mastercash.data.MovimentacaoDao;
import com.mastercash.mastercash.data.CategoriaDao;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

@Module
@InstallIn(ApplicationComponent.class)
public class DatabaseModule {

    @Singleton
    @Provides
    public AppDatabase provideDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "mastercash_db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    public MovimentacaoDao provideMovimentacaoDao(AppDatabase database) {
        return database.movimentacaoDao();
    }

    @Singleton
    @Provides
    public CategoriaDao provideCategoriaDao(AppDatabase database) {
        return database.categoriaDao();
    }
}
