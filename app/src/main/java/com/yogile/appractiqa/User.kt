package com.yogile.appractiqa

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot

class User(doc:DocumentSnapshot) {
    var isAdmin = doc["isAdmin"] as Boolean
    var groupCode = doc["groupCode"] as String
    var name = doc["name"] as String
    var logoUrl = doc["logo"] as String
    var age = doc["age"] as Long
}

