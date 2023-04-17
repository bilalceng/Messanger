package com.raywenderlich.messanger.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize

class User(val uid: String , val password: String, val userName: String, val email: String , val profileImageUrl: String): Parcelable {
    constructor(): this("","","","","")


    companion object{
        fun newInstance(uid: String,
                        password: String,
                        userName: String,
                        email: String,
                        profileImageUrl: String
        ) = User(uid,password, userName,email,profileImageUrl)
    }
}