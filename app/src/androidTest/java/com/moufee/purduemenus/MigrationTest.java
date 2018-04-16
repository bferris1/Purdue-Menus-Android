package com.moufee.purduemenus;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.moufee.purduemenus.db.AppDatabase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.moufee.purduemenus.db.AppDatabase.MIGRATION_1_2;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class MigrationTest {
    private static final String TEST_DB = "migration_test";

    @Rule
    public MigrationTestHelper helper;

    public MigrationTest() {
        helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                AppDatabase.class.getCanonicalName(),
                new FrameworkSQLiteOpenHelperFactory());
    }

    @Test
    public void migrate1To2() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 1);

        // db has schema version 1. insert some data using SQL queries.
        // cannot use DAO classes because they expect the latest schema.
        db.execSQL("INSERT INTO favorite VALUES ('My Great Food', '123', '321', 1)");
        // simulate adding a duplicate that was permitted in previous version
        db.execSQL("INSERT INTO favorite VALUES ('My Great Food', '232', '321', 1)");

        // Prepare for the next version.
        db.close();

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2);

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        Cursor result = db.query("SELECT * FROM favorite");

        result.moveToFirst();
        assertThat(result.getString(0), equalTo("My Great Food"));
        assertThat(result.getString(1), equalTo("123"));
        assertThat(result.getString(2), equalTo("321"));
        assertThat(result.getInt(3), equalTo(1));
    }
}
