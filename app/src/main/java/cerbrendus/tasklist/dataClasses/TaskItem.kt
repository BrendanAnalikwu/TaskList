package cerbrendus.tasklist.dataClasses

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

const val TASK_ITEM_TABLE_NAME = "main_item_list"

@Parcelize
@Entity(tableName = TASK_ITEM_TABLE_NAME)
data class TaskItem(var title: String? = null,
                    @PrimaryKey(autoGenerate = true) var id :Long? = null,
                    var description: String? = null,
                    var group_id: Int? = null,
                    var link_id: Int? = null,
                    var visible: Boolean=true,
                    var pending: Boolean=false,
                    var cleared: Boolean=false,
                    var checked: Boolean=false) : Parcelable {}