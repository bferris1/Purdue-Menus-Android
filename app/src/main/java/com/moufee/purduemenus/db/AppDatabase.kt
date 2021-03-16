package com.moufee.purduemenus.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.moufee.purduemenus.repository.data.menus.Favorite
import com.moufee.purduemenus.repository.data.menus.Location

@Database(entities = [Favorite::class, Location::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun locationDao(): LocationDao

    companion object {
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `Location` (" +
                                         "`Name` TEXT NOT NULL, `LocationId` TEXT NOT NULL, " +
                                         "`FormalName` TEXT NOT NULL, " +
                                         "`displayOrder` INTEGER NOT NULL, " +
                                         "`isHidden` INTEGER NOT NULL," +
                                         "PRIMARY KEY(`LocationId`))")
            }
        }
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                        "CREATE TABLE temp_table(" +
                                "`itemName` TEXT NOT NULL, " +
                                "`favoriteId` TEXT NOT NULL, " +
                                "`itemId` TEXT NOT NULL, " +
                                "`isVegetarian` INTEGER NOT NULL, " +
                                "PRIMARY KEY(`itemId`));")
                database.execSQL("INSERT OR IGNORE INTO temp_table SELECT * FROM favorite")
                database.execSQL("DROP TABLE favorite")
                database.execSQL("ALTER TABLE temp_table RENAME TO Favorite")
            }
        }
    }
}