package org.qosp.notes

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.qosp.notes.data.AppDatabase
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val testDb = "migration-test"

    // Array of all migrations.
    private val ALL_MIGRATIONS = arrayOf(
        AppDatabase.MIGRATION_1_2,
        AppDatabase.MIGRATION_2_3,
    )

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrateAlreadyIn3() {
        // Create the earliest version of the database.
        helper.createDatabase(testDb, 3).apply {
            close()
        }

        // Open latest version of the database. Room validates the schema
        // once all migrations execute.
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
            testDb
        ).addMigrations(*ALL_MIGRATIONS).build().apply {
            openHelper.writableDatabase.close()
        }
    }

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        var db = helper.createDatabase(testDb, 2).apply {
            // db has schema version 1. insert some data using SQL queries.
            // You cannot use DAO classes because they expect the latest schema.
            execSQL(
                """
                insert into cloud_ids(mappingId, localNoteId, remoteNoteId, provider, isDeletedLocally, isBeingUpdated)
                values ( 1, 33, 44, "nextcloud", 0, 0 );
            """.trimIndent()
            )

            //     val mappingId: Long = 0L,
            //    val localNoteId: Long,
            //    val remoteNoteId: Long?,
            //    val provider: CloudService?,
            //    val extras: String?,
            //    val isDeletedLocally: Boolean,
            //    val isBeingUpdated: Boolean = false,
            // Prepare for the next version.
            close()
        }


        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
//        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.

    }


    @Test
    @Throws(IOException::class)
    fun migrate2to3() {
        // Create the earliest version of the database.
        helper.createDatabase(testDb, 2).apply {
            close()
        }

        // Open latest version of the database. Room validates the schema
        // once all migrations execute.
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
            testDb
        ).addMigrations(*ALL_MIGRATIONS).build().apply {
            openHelper.writableDatabase.close()
        }
    }


    @Test
    @Throws(IOException::class)
    fun migrate3to4() {
        // Create the earliest version of the database.
        helper.createDatabase(testDb, 3).apply {
            close()
        }

        var db = helper.runMigrationsAndValidate(testDb, 4, true, )



        // Open latest version of the database. Room validates the schema
        // once all migrations execute.
//        Room.databaseBuilder(
//            InstrumentationRegistry.getInstrumentation().targetContext,
//            AppDatabase::class.java,
//            testDb
//        ).addMigrations(*ALL_MIGRATIONS).build().apply {
//            openHelper.writableDatabase.close()
//        }
    }
}
