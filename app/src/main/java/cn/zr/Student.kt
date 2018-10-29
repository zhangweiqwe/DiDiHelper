package cn.zr

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class Student(var name: String = "1", var arr: Array<String> = arrayOf("fds", "fdasf", "fdsafdsaf")):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.createStringArray()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeStringArray(arr)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Student> {
        override fun createFromParcel(parcel: Parcel): Student {
            return Student(parcel)
        }

        override fun newArray(size: Int): Array<Student?> {
            return arrayOfNulls(size)
        }
    }


}