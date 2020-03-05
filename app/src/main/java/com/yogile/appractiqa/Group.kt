package com.yogile.appractiqa

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot

class Group(doc:DocumentSnapshot) {
    var name = doc["name"] as String
    var logoUrl = doc["url"] as String
    var uid = doc.id
}

