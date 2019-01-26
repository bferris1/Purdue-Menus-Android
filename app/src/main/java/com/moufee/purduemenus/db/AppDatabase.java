package com.moufee.purduemenus.db;

import com.moufee.purduemenus.menus.Favorite;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Favorite.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FavoriteDao favoriteDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE temp_table(" +
                            "`itemName` TEXT NOT NULL, " +
                            "`favoriteId` TEXT NOT NULL, " +
                            "`itemId` TEXT NOT NULL, " +
                            "`isVegetarian` INTEGER NOT NULL, " +
                            "PRIMARY KEY(`itemId`));");
            database.execSQL("INSERT OR IGNORE INTO temp_table SELECT * FROM favorite");
            database.execSQL("DROP TABLE favorite");
            database.execSQL("ALTER TABLE temp_table RENAME TO Favorite");
        }
    };
}
