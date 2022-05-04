/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//NOTICE: File edited (MattiaDellepiane)
//Adjusted imports of artifacts and package name
package com.github.mattiadellepiane.gnssraw.utils.pseudorange


import java.lang.Exception

import kotlin.Throws

import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import com.google.common.base.Preconditions

/**
 * A helper class to access the Google Elevation API for computing the Terrain Elevation Above Sea
 * level at a given location (lat, lng). An Elevation API key is required for getting elevation
 * above sea level from Google server.
 *
 *
 *  For more information please see:
 * https://developers.google.com/maps/documentation/elevation/start
 *
 *
 *  A key can be conveniently acquired from:
 * https://developers.google.com/maps/documentation/elevation/get-api-key
 */
class ElevationApiHelper(elevationApiKey: String) {
    private var elevationApiKey = ""

    /**
     * Gets elevation (height above sea level) via the Google elevation API by requesting
     * elevation for a given latitude and longitude. Longitude and latitude should be in decimal
     * degrees and the returned elevation will be in meters.
     */
    @Throws(Exception::class)
    fun getElevationAboveSeaLevelMeters(latitudeDegrees: Double,
                                        longitudeDegrees: Double): Double {
        val url = (GOOGLE_ELEVATION_API_HTTP_ADDRESS
                + latitudeDegrees
                + ","
                + longitudeDegrees
                + "&key="
                + elevationApiKey)
        var elevationMeters = "0.0"
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        val content = urlConnection.inputStream
        val buffer = BufferedReader(InputStreamReader(content, StandardCharsets.UTF_8))
        var line: String
        while (buffer.readLine().also { line = it } != null) {
            line = line.trim { it <= ' ' }
            if (line.startsWith(ELEVATION_XML_STRING)) {
                // read the part of the line after the opening tag <elevation>
                val substring = line.substring(ELEVATION_XML_STRING.length, line.length)
                // read the part of the line until before the closing tag <elevation>
                elevationMeters = substring.substring(0, substring.length - ELEVATION_XML_STRING.length - 1)
            }
        }
        return elevationMeters.toDouble()
    }

    companion object {
        private const val ELEVATION_XML_STRING = "<elevation>"
        private const val GOOGLE_ELEVATION_API_HTTP_ADDRESS = "https://maps.googleapis.com/maps/api/elevation/xml?locations="

        /**
         * Calculates the geoid height by subtracting the elevation above sea level from the ellipsoid
         * height in altitude meters.
         */
        fun calculateGeoidHeightMeters(altitudeMeters: Double,
                                       elevationAboveSeaLevelMeters: Double): Double {
            return altitudeMeters - elevationAboveSeaLevelMeters
        }
    }

    /**
     * A constructor that passes the `elevationApiKey`. If the user pass an empty string for
     * API Key, an `IllegalArgumentException` will be thrown.
     */
    init {
        // An Elevation API key must be provided for getting elevation from Google Server.
        Preconditions.checkArgument(!elevationApiKey.isEmpty())
        this.elevationApiKey = elevationApiKey
    }
}