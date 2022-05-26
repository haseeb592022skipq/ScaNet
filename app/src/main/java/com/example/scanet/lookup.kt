package com.example.scanet

import com.stealthcopter.networktools.subnet.Device
import java.net.URL
import java.util.ArrayList


class lookup {
    fun lookupVendor(mac: String) = URL("https://api.macaddress.io/v1?apiKey=at_6LUkiZoGJUUoEDDtnO1CuxxDUtVwS&output=vendor&search=" + mac).readText()
    fun scan(list : List<Device>):List<Device>{
        for (i in list) {
            try {


                i.vendor = lookupVendor(i.mac)


            }
            catch (e: Exception){
                i.vendor = "-"
            }

        }

        return list
    }
}