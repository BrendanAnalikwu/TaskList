package cerbrendus.tasklist.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cerbrendus.tasklist.dataClasses.GROUP_TABLE_NAME
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TASK_ITEM_TABLE_NAME
import cerbrendus.tasklist.dataClasses.TaskItem

//Created by Brendan on 29-12-2018.
@Database(entities = arrayOf(TaskItem::class, Group::class),version = 7)
abstract class ItemDatabase: RoomDatabase() {
    abstract fun itemDAO() : ItemDAO

    companion object {
        private var INSTANCE: ItemDatabase? = null

        fun getInstance(context: Context): ItemDatabase? {
            if (INSTANCE == null) {
                synchronized(ItemDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        ItemDatabase::class.java,"item.db")
                        .addMigrations(MIGRATION_6_7)
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance(){
            INSTANCE = null
        }

        val MIGRATION_6_7 = object : Migration(6,7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("BEGIN TRANSACTION")
                database.execSQL("ALTER TABLE $GROUP_TABLE_NAME RENAME TO old_group_table")
                database.execSQL("CREATE TABLE $GROUP_TABLE_NAME (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title TEXT NULL," +
                        "visibleInMain INTEGER NOT NULL)")
                database.execSQL("INSERT INTO $GROUP_TABLE_NAME (id, title, visibleInMain) SELECT id, title," +
                        "visibleInMain FROM old_group_table")
                database.execSQL("COMMIT")
            }

        }
    }

}