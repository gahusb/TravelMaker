package com.example.bgg89.travelmaker_project.Data

class Schedule {
    var sch_id : Int = 0
    var sch_fk : Int = 0
    var sch_title : String = ""
    var sub_title : String = ""
    var start_time_hour : Int = 0
    var start_time_min : Int = 0
    var end_time_hour : Int = 0
    var end_time_min : Int = 0
    var sch_date : String = ""

    constructor(sch_id : Int, sch_fk : Int, sch_title : String, sub_title : String, start_time_hour : Int, start_time_min : Int , end_time_hour : Int, end_time_min : Int, sch_date : String){
        this.sch_id = sch_id
        this.sch_fk = sch_fk
        this.sch_title = sch_title
        this.sub_title = sub_title
        this.start_time_hour = start_time_hour
        this.start_time_min = start_time_min
        this.end_time_hour = end_time_hour
        this.end_time_min = end_time_min
        this.sch_date = sch_date
    }
}