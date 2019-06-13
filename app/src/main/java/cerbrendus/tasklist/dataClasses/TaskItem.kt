package cerbrendus.tasklist.dataClasses

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

const val TASK_ITEM_TABLE_NAME = "main_item_list"

@Parcelize
@Entity(tableName = TASK_ITEM_TABLE_NAME)
data class TaskItem(var title: String? = null,
                    @PrimaryKey(autoGenerate = true) var id :Long? = null,
                    var description: String? = null,
                    var group_id: Long = -1,
                    var link_id: Int? = null,
                    var visible: Boolean=true,
                    var pending: Boolean=false,
                    var cleared: Boolean=false,
                    var checked: Boolean=false,
                    var priority: Long = -1,
                    var containsSublist: Boolean=false,
                    var sublist: String = "",
                    var isSublistItem: Boolean = false) : Parcelable {

    override fun equals(other: Any?): Boolean = //visible property is omitted
        (other is TaskItem)&&(id==other.id)&&(checked==other.checked)&&(group_id==other.group_id)&&(priority==other.priority)

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + group_id.hashCode()
        result = 31 * result + (link_id ?: 0)
        result = 31 * result + visible.hashCode()
        result = 31 * result + pending.hashCode()
        result = 31 * result + cleared.hashCode()
        result = 31 * result + checked.hashCode()
        result = 31 * result + priority.hashCode()
        return result
    }

    fun getSublistAsList() : List<Long> = sublist.split(",").map{ it.toLong() }
    fun setSublistFromList(list: List<Long>) { sublist = list.joinToString(",") }

}