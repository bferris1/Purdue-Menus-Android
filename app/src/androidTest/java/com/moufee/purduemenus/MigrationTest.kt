package com.moufee.purduemenus

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.moufee.purduemenus.db.AppDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    @get:Rule
    var helper: MigrationTestHelper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
            AppDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory())

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        var db = helper.createDatabase(TEST_DB, 1)
        // db has schema version 1. insert some data using SQL queries.
        // cannot use DAO classes because they expect the latest schema.
        db.execSQL("INSERT INTO favorite VALUES ('My Great Food', '123', '321', 1)")
        // simulate adding a duplicate that was permitted in previous version
        db.execSQL("INSERT INTO favorite VALUES ('My Great Food', '232', '321', 1)")
        // Prepare for the next version.
        db.close()
        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, AppDatabase.MIGRATION_1_2)
        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        val result = db.query("SELECT * FROM favorite")
        result.moveToFirst()
        assertThat(result.getString(0)).isEqualTo("My Great Food")
        assertThat(result.getString(1)).isEqualTo("123")
        assertThat(result.getString(2)).isEqualTo("321")
        assertThat(result.getInt(3)).isEqualTo(1)
    }

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        helper.createDatabase(TEST_DB, 2)
        helper.runMigrationsAndValidate(TEST_DB, 3, true, AppDatabase.MIGRATION_2_3)
        // MigrationTestHelper automatically verifies the schema changes,
        // This migration just creates a new table, so no data validation needed
    }

    companion object {
        private const val TEST_DB = "migration_test"
    }

}