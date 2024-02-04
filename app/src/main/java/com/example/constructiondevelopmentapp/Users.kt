package com.example.constructiondevelopmentapp

data class Users(
    var fName : String? = null,
    var email : String? = null,
    var password : String? = null,
    var rePassword : String? = null,
    var contact : String? = null,
    var district : String? = null,
    var job : String? = null,
    var aboutMe : String = "",
    var charges : String = "",
    var uid : String? = null

)