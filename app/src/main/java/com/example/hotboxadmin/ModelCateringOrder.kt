package com.example.hotboxadmin

class ModelCateringOrder(
    var city : String,
    var deliverytime : String,
    var home : String,
    var landmark : String,
    var mobno : String,
    var name : String,
    var ordered_at : String,
    var pin : String,
    var road : String,
    var state : String,
    var totalprice : String,
    var uid : String
)
{
    //class def
    var ordid : String = ""
    fun set(id: String){
        this.ordid = id
    }
}