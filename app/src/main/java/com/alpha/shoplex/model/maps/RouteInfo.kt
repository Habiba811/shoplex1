package com.alpha.shoplex.model.maps

class RouteInfo(distance: String, duration: String) {
    var distance: String? = distance
    var duration: String? = duration

    fun getAvgSpeed(): String {
        if (distance == "N/A") return "N/A"
        if (distance == "N/A") return "N/A"
        val dis = distance!!.replace(",", "").split(" ".toRegex()).toTypedArray()[0].toDouble()
        var dur = 1.0
        val values = listOf(*duration!!.split(" ".toRegex()).toTypedArray())
        if (duration!!.contains("day") && duration!!.contains("hour") && duration!!.contains("min")) {
            dur = (values[0].toInt() * 24 * 60).toDouble()
            dur += (values[2].toInt() * 60).toDouble()
            dur += values[4].toInt().toDouble()
        } else if (duration!!.contains("day") && duration!!.contains("hour")) {
            dur = (values[0].toInt() * 24 * 60).toDouble()
            dur += (values[2].toInt() * 60).toDouble()
        } else if (duration!!.contains("hour") && duration!!.contains("min")) {
            dur = (values[0].toInt() * 60).toDouble()
            dur += values[2].toInt().toDouble()
        } else if (duration!!.contains("min")) {
            dur = values[0].toInt().toDouble()
        }
        dur /= 60.0
        return (dis / dur).toInt().toString() + " km/h"
    }

    override fun toString(): String {
        return "Distance: $distance, Duration: $duration"
    }
}