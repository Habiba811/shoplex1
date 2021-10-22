package com.alpha.shoplex.model.pojo

import java.util.*

class Report {

    var type: String = ""
    var reportComment: String = ""
    lateinit var date: Date

    constructor()

    constructor(type: String, reportComment: String, date: Date) {
        this.type = type
        this.reportComment = reportComment
        this.date = date
    }
}