package cerbrendus.tasklist.Database


import androidx.lifecycle.LiveData
import androidx.room.*
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TaskItem

//Created by Brendan on 29-12-2018.
@Dao
interface ItemDAO {
    //
    @Insert
    fun insertItems(vararg items: TaskItem)
    @Update
    fun updateItems(vararg items: TaskItem)
    @Delete
    fun deleteItems(vararg items: TaskItem)

    @Query("SELECT * FROM main_item_list WHERE cleared = 0 ORDER BY priority ASC")
    fun getAllItems(): LiveData<List<TaskItem>>
    @Query("SELECT * FROM main_item_list WHERE cleared = 1")
    fun getAllClearedItems(): LiveData<List<TaskItem>>
    @Query("SELECT * FROM main_item_list WHERE checked = 1 and cleared = 0")
    fun getAllCheckedItems(): LiveData<List<TaskItem>>

    @Query("SELECT MAX(priority) FROM main_item_list")
    fun getMaxPriority(): Long


    //for groups
    @Insert
    fun createGroup(group: Group)
    @Delete
    fun deleteGroup(group: Group)
    @Update
    fun updateGroup(group: Group)
    @Query("SELECT * FROM group_list")
    fun getGroupList() : LiveData<List<Group>>
    @Query("SELECT * FROM group_list WHERE id = :groupId LIMIT 1")
    fun getGroup(groupId: Long) : Group
    @Query("SELECT * FROM main_item_list WHERE group_id = :groupId")
    fun getItemsInGroup(groupId: Int) : LiveData<List<TaskItem>>
}