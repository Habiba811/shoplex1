package com.alpha.shoplex.model.pojo

class Properties {
    var title: String = ""
    var property: ArrayList<String> = arrayListOf()

    constructor()

    constructor(title: String, property: ArrayList<String>) {
        this.title = title
        this.property = property
    }
}