package cerbrendus.tasklist.dataClasses

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "group_list")
data class Group(@PrimaryKey(autoGenerate = true) var id :Long? = null,
                 var title : String,
                 var visibleInMain : Boolean =  true) : Parcelable {

}