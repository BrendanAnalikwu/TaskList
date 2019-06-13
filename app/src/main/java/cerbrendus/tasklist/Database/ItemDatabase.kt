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
@Database(entities = arrayOf(TaskItem::class, Group::class),version = 22)
abstract class ItemDatabase: RoomDatabase() {
    abstract fun itemDAO() : ItemDAO

    companion object {
        private var INSTANCE: ItemDatabase? = null

        fun getInstance(context: Context): ItemDatabase? {
            if (INSTANCE == null) {
                synchronized(ItemDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        ItemDatabase::class.java,"item.db")
                        .addMigrations(MIGRATION_PRIORITY_RESET)
                        .addMigrations(MIGRATION_COLOR_ADD)
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance(){
            INSTANCE = null
        }

        val MIGRATION_PRIORITY_RESET = object : Migration(20,21) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("BEGIN TRANSACTION")
                //database.execSQL("ALTER TABLE $TASK_ITEM_TABLE_NAME ADD COLUMN priority INTEGER NOT NULL DEFAULT -1")
                database.execSQL("UPDATE $TASK_ITEM_TABLE_NAME SET priority=id")
                database.execSQL("COMMIT")
            }

        }

        val MIGRATION_COLOR_ADD = object : Migration(21,22) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("BEGIN TRANSACTION")
                database.execSQL("ALTER TABLE $GROUP_TABLE_NAME ADD COLUMN color INTEGER NOT NULL DEFAULT ${0xffff003f.toInt()}")
                //database.execSQL("UPDATE $TASK_ITEM_TABLE_NAME SET priority=id")
                database.execSQL("COMMIT")
            }

        }
    }

}