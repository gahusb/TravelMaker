package com.example.bgg89.travelmaker_project.Data

class Payment {
    var pay_id : Int = 0
    var pay_fk : Int = 0
    var withdrawordeposit : Int = 0
    var cashorcredit : Int = 0
    var payment_title : String = ""
    var payment_amount : String = ""
    var pay_hour : String = ""
    var pay_min : String = ""
    var pay_date : String = ""

    constructor(id : Int, fk : Int, withdrawordeposit : Int, cashorcredit : Int, payment_title : String, payment_amount : String, pay_hour : String, pay_min : String, pay_date : String ){
        this.pay_id = id
        this.pay_fk = fk
        this.withdrawordeposit = withdrawordeposit
        this.cashorcredit = cashorcredit
        this.payment_title = payment_title
        this.payment_amount = payment_amount
        this.pay_hour = pay_hour
        this.pay_min = pay_min
        this.pay_date = pay_date
    }
}