package cerbrendus.tasklist.dataClasses

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

const val GROUP_TABLE_NAME = "group_list"

@Parcelize
@Entity(tableName = GROUP_TABLE_NAME)
data class Group(@PrimaryKey(autoGenerate = true) var id :Long? = null,
                 var title : String? = null,
                 var visibleInMain : Boolean =  true,
                 var color : Int = 0xffff003f.toInt()
) : Parcelable