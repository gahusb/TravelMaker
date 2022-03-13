package com.example.bgg89.travelmaker_project.Data

class Travel {
    var id : Int = 0
    var title : String = ""
    var name : String = ""
    var start : String = ""
    var end : String = ""

    constructor(id : Int, title : String, name : String, start : String, end : String){
        this.id = id
        this.title = title
        this.name = name
        this.start = start
        this.end = end
    }
}