package com.example.scanet

import org.json.JSONObject
import java.net.URL

class IP_Check {
    var ip = JSONObject(URL("https://api.ipify.org?format=json").readText())["ip"]


    fun details():JSONObject {

        var list = URL("https://api.ipgeolocation.io/ipgeo?apiKey=a6231e080ea5437896813735833ae862&ip=" +ip).readText( Charsets.UTF_8)


        val data = JSONObject(list)

        return data


    }

}