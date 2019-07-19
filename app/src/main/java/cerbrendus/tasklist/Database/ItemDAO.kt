package cerbrendus.tasklist.Database


import androidx.lifecycle.LiveData
import androidx.room.*
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TASK_ITEM_TABLE_NAME
import cerbrendus.tasklist.dataClasses.TaskItem

//Created by Brendan on 29-12-2018.
@Dao
interface ItemDAO {
    //
    @Insert
    suspend fun insertForResult(item: TaskItem) : Long
    @Update
    fun updateItems(vararg items: TaskItem)
    @Delete
    suspend fun deleteItems(vararg items: TaskItem)

    @Query("SELECT * FROM main_item_list WHERE cleared = 0")
    fun getAllItems(): LiveData<List<TaskItem>>
    @Query("SELECT * FROM main_item_list")
    fun getItems(): LiveData<List<TaskItem>>
    @Query("SELECT * FROM main_item_list WHERE cleared = 1")
    fun getAllClearedItems(): LiveData<List<TaskItem>>
    @Query("SELECT * FROM main_item_list WHERE checked = 1 and cleared = 0")
    fun getAllCheckedItems(): LiveData<List<TaskItem>>

    @Query("SELECT MAX(priority) FROM main_item_list")
    suspend fun getMaxPriority(): Long

    @Query("UPDATE $TASK_ITEM_TABLE_NAME SET checked = :checked_val WHERE id = :id")
    fun updateChecked(id : Long, checked_val : Boolean)
    /*@Query("UPDATE $TASK_ITEM_TABLE_NAME SET priority = :priority_val WHERE id = :id")
    fun updatePriority(id : Long, priority_val : Long)*/


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