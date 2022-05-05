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
//Adjusted package name
package com.github.mattiadellepiane.gnssraw.utils.pseudorange

/**
 * Calculate the tropospheric delay based on the ENGOS Tropospheric model.
 *
 *
 * The tropospheric delay is modeled as a combined effect of the delay experienced due to
 * hydrostatic (dry) and wet components of the troposphere. Both delays experienced at zenith are
 * scaled with a mapping function to get the delay at any specific elevation.
 *
 *
 * The tropospheric model algorithm of EGNOS model by Penna, N., A. Dodson and W. Chen (2001)
 * (http://espace.library.curtin.edu.au/cgi-bin/espace.pdf?file=/2008/11/13/file_1/18917) is used
 * for calculating the zenith delays. In this model, the weather parameters are extracted using
 * interpolation from lookup table derived from the US Standard Atmospheric Supplements, 1966.
 *
 *
 * A close form mapping function is built using Guo and Langley, 2003
 * (http://gauss2.gge.unb.ca/papers.pdf/iongpsgnss2003.guo.pdf) which is able to calculate accurate
 * mapping down to 2 degree elevations.
 *
 *
 * Sources:
 *
 * http://espace.library.curtin.edu.au/cgi-bin/espace.pdf?file=/2008/11/13/file_1/18917
 *
 * - http://www.academia.edu/3512180/Assessment_of_UNB3M_neutral
 * _atmosphere_model_and_EGNOS_model_for_near-equatorial-tropospheric_delay_correction
 *
 * - http://gauss.gge.unb.ca/papers.pdf/ion52am.collins.pdf
 *
 * - http://www.navipedia.net/index.php/Tropospheric_Delay#cite_ref-3
 *
 * Hydrostatic and non-hydrostatic mapping functions are obtained from:
 * http://gauss2.gge.unb.ca/papers.pdf/iongpsgnss2003.guo.pdf
 *
 */
object TroposphericModelEgnos {
    // parameters of the EGNOS models
    private const val INDEX_15_DEGREES = 0
    private const val INDEX_75_DEGREES = 4
    private const val LATITUDE_15_DEGREES = 15
    private const val LATITUDE_75_DEGREES = 75

    // Lookup Average parameters
    // Troposphere average pressure mbar
    private val latDegreeToPressureMbarAvgMap = doubleArrayOf(1013.25, 1017.25, 1015.75, 1011.75, 1013.0)

    // Troposphere average temperature Kelvin
    private val latDegreeToTempKelvinAvgMap = doubleArrayOf(299.65, 294.15, 283.15, 272.15, 263.65)

    // Troposphere average water vapor pressure
    private val latDegreeToWVPressureMbarAvgMap = doubleArrayOf(26.31, 21.79, 11.66, 6.78, 4.11)

    // Troposphere average temperature lapse rate K/m
    private val latDegreeToBetaAvgMapKPM = doubleArrayOf(6.30e-3, 6.05e-3, 5.58e-3, 5.39e-3, 4.53e-3)

    // Troposphere average water vapor lapse rate (dimensionless)
    private val latDegreeToLambdaAvgMap = doubleArrayOf(2.77, 3.15, 2.57, 1.81, 1.55)

    // Lookup Amplitude parameters
    // Troposphere amplitude pressure mbar
    private val latDegreeToPressureMbarAmpMap = doubleArrayOf(0.0, -3.75, -2.25, -1.75, -0.5)

    // Troposphere amplitude temperature Kelvin
    private val latDegreeToTempKelvinAmpMap = doubleArrayOf(0.0, 7.0, 11.0, 15.0, 14.5)

    // Troposphere amplitude water vapor pressure
    private val latDegreeToWVPressureMbarAmpMap = doubleArrayOf(0.0, 8.85, 7.24, 5.36, 3.39)

    // Troposphere amplitude temperature lapse rate K/m
    private val latDegreeToBetaAmpMapKPM = doubleArrayOf(0.0, 0.25e-3, 0.32e-3, 0.81e-3, 0.62e-3)

    // Troposphere amplitude water vapor lapse rate (dimensionless)
    private val latDegreeToLambdaAmpMap = doubleArrayOf(0.0, 0.33, 0.46, 0.74, 0.30)

    // Zenith delay dry constant K/mbar
    private const val K1 = 77.604

    // Zenith delay wet constant K^2/mbar
    private const val K2 = 382000.0

    // gas constant for dry air J/kg/K
    private const val RD = 287.054

    // Acceleration of gravity at the atmospheric column centroid m/s^-2
    private const val GM = 9.784

    // Gravity m/s^2
    private const val GRAVITY_MPS2 = 9.80665
    private const val MINIMUM_INTERPOLATION_THRESHOLD = 1e-25
    private const val B_HYDROSTATIC = 0.0035716
    private const val C_HYDROSTATIC = 0.082456
    private const val B_NON_HYDROSTATIC = 0.0018576
    private const val C_NON_HYDROSTATIC = 0.062741
    private const val SOUTHERN_HEMISPHERE_DMIN = 211.0
    private const val NORTHERN_HEMISPHERE_DMIN = 28.0

    // Days recalling that every fourth year is a leap year and has an extra day - February 29th
    private const val DAYS_PER_YEAR = 365.25

    /**
     * Computes the tropospheric correction in meters given the satellite elevation in radians, the
     * user latitude in radians, the user Orthometric height above sea level in meters and the day of
     * the year.
     *
     *
     * Dry and wet delay zenith delay components are calculated and then scaled with the mapping
     * function at the given satellite elevation.
     *
     */
    fun calculateTropoCorrectionMeters(satElevationRadians: Double,
                                       userLatitudeRadian: Double, heightMetersAboveSeaLevel: Double, dayOfYear1To366: Int): Double {
        val dryAndWetMappingValues = computeDryAndWetMappingValuesUsingUNBabcMappingFunction(satElevationRadians,
                userLatitudeRadian, heightMetersAboveSeaLevel)
        val dryAndWetZenithDelays = calculateZenithDryAndWetDelaysSec(userLatitudeRadian, heightMetersAboveSeaLevel, dayOfYear1To366)
        val drydelaySeconds = dryAndWetZenithDelays.dryZenithDelaySec * dryAndWetMappingValues.dryMappingValue
        val wetdelaySeconds = dryAndWetZenithDelays.wetZenithDelaySec * dryAndWetMappingValues.wetMappingValue
        return drydelaySeconds + wetdelaySeconds
    }

    /**
     * Computes the dry and wet mapping values based on the University of Brunswick UNBabc model. The
     * mapping function inputs are satellite elevation in radians, user latitude in radians and user
     * orthometric height above sea level in meters. The function returns
     * `DryAndWetMappingValues` containing dry and wet mapping values.
     *
     *
     * From the many dry and wet mapping functions of components of the troposphere, the method
     * from the University of Brunswick in Canada was selected due to its reasonable computation time
     * and accuracy with satellites as low as 2 degrees elevation.
     *
     * Source: http://gauss2.gge.unb.ca/papers.pdf/iongpsgnss2003.guo.pdf
     */
    private fun computeDryAndWetMappingValuesUsingUNBabcMappingFunction(
            satElevationRadians: Double, userLatitudeRadians: Double, heightMetersAboveSeaLevel: Double): DryAndWetMappingValues {
        var satElevationRadians = satElevationRadians
        if (satElevationRadians > Math.PI / 2.0) {
            satElevationRadians = Math.PI / 2.0
        } else if (satElevationRadians < 2.0 * Math.PI / 180.0) {
            satElevationRadians = Math.toRadians(2.0)
        }

        // dry components mapping parameters
        val aHydrostatic = (1.18972 - 0.026855 * heightMetersAboveSeaLevel / 1000.0 + 0.10664
                * Math.cos(userLatitudeRadians)) / 1000.0
        val numeratorDry = 1.0 + aHydrostatic / (1.0 + B_HYDROSTATIC / (1.0 + C_HYDROSTATIC))
        val denominatorDry = Math.sin(satElevationRadians) + aHydrostatic / (Math.sin(satElevationRadians)
                + B_HYDROSTATIC / (Math.sin(satElevationRadians) + C_HYDROSTATIC))
        val drymap = numeratorDry / denominatorDry

        // wet components mapping parameters
        val aNonHydrostatic = (0.61120 - 0.035348 * heightMetersAboveSeaLevel / 1000.0 - (0.01526
                * Math.cos(userLatitudeRadians))) / 1000.0
        val numeratorWet = 1.0 + aNonHydrostatic / (1.0 + B_NON_HYDROSTATIC / (1.0 + C_NON_HYDROSTATIC))
        val denominatorWet = Math.sin(satElevationRadians) + aNonHydrostatic / (Math.sin(satElevationRadians)
                + B_NON_HYDROSTATIC / (Math.sin(satElevationRadians) + C_NON_HYDROSTATIC))
        val wetmap = numeratorWet / denominatorWet
        return DryAndWetMappingValues(drymap, wetmap)
    }

    /**
     * Computes the combined effect of the delay at zenith experienced due to hydrostatic (dry) and wet
     * components of the troposphere. The function inputs are the user latitude in radians, user
     * orthometric height above sea level in meters and the day of the year (1-366). The function
     * returns a `DryAndWetZenithDelays` containing dry and wet delays at zenith.
     *
     *
     * EGNOS Tropospheric model by Penna et al. (2001) is used in this case.
     * (http://espace.library.curtin.edu.au/cgi-bin/espace.pdf?file=/2008/11/13/file_1/18917)
     *
     */
    private fun calculateZenithDryAndWetDelaysSec(userLatitudeRadians: Double,
                                                  heightMetersAboveSeaLevel: Double, dayOfYear1To366: Int): DryAndWetZenithDelays {
        // interpolated meteorological values
        val pressureMbar: Double
        val tempKelvin: Double
        val waterVaporPressureMbar: Double
        // temperature lapse rate, [K/m]
        val beta: Double
        // water vapor lapse rate, dimensionless
        val lambda: Double
        val absLatitudeDeg = Math.toDegrees(Math.abs(userLatitudeRadians))
        // day of year min constant
        val dmin: Double
        dmin = if (userLatitudeRadians < 0) {
            SOUTHERN_HEMISPHERE_DMIN
        } else {
            NORTHERN_HEMISPHERE_DMIN
        }
        val amplitudeScaleFactor = Math.cos(2 * Math.PI * (dayOfYear1To366 - dmin)
                / DAYS_PER_YEAR)
        if (absLatitudeDeg <= LATITUDE_15_DEGREES) {
            pressureMbar = (latDegreeToPressureMbarAvgMap[INDEX_15_DEGREES]
                    - latDegreeToPressureMbarAmpMap[INDEX_15_DEGREES] * amplitudeScaleFactor)
            tempKelvin = (latDegreeToTempKelvinAvgMap[INDEX_15_DEGREES]
                    - latDegreeToTempKelvinAmpMap[INDEX_15_DEGREES] * amplitudeScaleFactor)
            waterVaporPressureMbar = (latDegreeToWVPressureMbarAvgMap[INDEX_15_DEGREES]
                    - latDegreeToWVPressureMbarAmpMap[INDEX_15_DEGREES] * amplitudeScaleFactor)
            beta = latDegreeToBetaAvgMapKPM[INDEX_15_DEGREES] - latDegreeToBetaAmpMapKPM[INDEX_15_DEGREES]* amplitudeScaleFactor
            lambda = latDegreeToLambdaAmpMap[INDEX_15_DEGREES] - latDegreeToLambdaAmpMap[INDEX_15_DEGREES]* amplitudeScaleFactor
        } else if (absLatitudeDeg > LATITUDE_15_DEGREES && absLatitudeDeg < LATITUDE_75_DEGREES) {
            val key = (absLatitudeDeg / LATITUDE_15_DEGREES).toInt()
            val averagePressureMbar = interpolate((key * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToPressureMbarAvgMap[key - 1], ((key + 1) * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToPressureMbarAvgMap[key], absLatitudeDeg)
            val amplitudePressureMbar = interpolate((key * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToPressureMbarAmpMap[key - 1], ((key + 1) * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToPressureMbarAmpMap[key], absLatitudeDeg)
            pressureMbar = averagePressureMbar - amplitudePressureMbar * amplitudeScaleFactor
            val averageTempKelvin = interpolate((key * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToTempKelvinAvgMap[key - 1], ((key + 1) * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToTempKelvinAvgMap[key], absLatitudeDeg)
            val amplitudeTempKelvin = interpolate((key * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToTempKelvinAmpMap[key - 1], ((key + 1) * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToTempKelvinAmpMap[key], absLatitudeDeg)
            tempKelvin = averageTempKelvin - amplitudeTempKelvin * amplitudeScaleFactor
            val averageWaterVaporPressureMbar = interpolate((key * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToWVPressureMbarAvgMap[key - 1], ((key + 1) * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToWVPressureMbarAvgMap[key], absLatitudeDeg)
            val amplitudeWaterVaporPressureMbar = interpolate((key * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToWVPressureMbarAmpMap[key - 1], ((key + 1) * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToWVPressureMbarAmpMap[key], absLatitudeDeg)
            waterVaporPressureMbar = averageWaterVaporPressureMbar - amplitudeWaterVaporPressureMbar * amplitudeScaleFactor
            val averageBeta = interpolate((key * LATITUDE_15_DEGREES).toDouble(), latDegreeToBetaAvgMapKPM[key - 1], (
                    (key + 1) * LATITUDE_15_DEGREES).toDouble(), latDegreeToBetaAvgMapKPM[key], absLatitudeDeg)
            val amplitudeBeta = interpolate((key * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToBetaAmpMapKPM[key - 1], ((key + 1) * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToBetaAmpMapKPM[key], absLatitudeDeg)
            beta = averageBeta - amplitudeBeta * amplitudeScaleFactor
            val averageLambda = interpolate((key * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToLambdaAvgMap[key - 1], ((key + 1) * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToLambdaAvgMap[key], absLatitudeDeg)
            val amplitudeLambda = interpolate((key * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToLambdaAmpMap[key - 1], ((key + 1) * LATITUDE_15_DEGREES).toDouble(),
                    latDegreeToLambdaAmpMap[key], absLatitudeDeg)
            lambda = averageLambda - amplitudeLambda * amplitudeScaleFactor
        } else {
            pressureMbar = (latDegreeToPressureMbarAvgMap[INDEX_75_DEGREES]
                    - latDegreeToPressureMbarAmpMap[INDEX_75_DEGREES] * amplitudeScaleFactor)
            tempKelvin = (latDegreeToTempKelvinAvgMap[INDEX_75_DEGREES]
                    - latDegreeToTempKelvinAmpMap[INDEX_75_DEGREES] * amplitudeScaleFactor)
            waterVaporPressureMbar = (latDegreeToWVPressureMbarAvgMap[INDEX_75_DEGREES]
                    - latDegreeToWVPressureMbarAmpMap[INDEX_75_DEGREES] * amplitudeScaleFactor)
            beta = latDegreeToBetaAvgMapKPM[INDEX_75_DEGREES] - latDegreeToBetaAmpMapKPM[INDEX_75_DEGREES]* amplitudeScaleFactor
            lambda = latDegreeToLambdaAmpMap[INDEX_75_DEGREES] - latDegreeToLambdaAmpMap[INDEX_75_DEGREES]* amplitudeScaleFactor
        }
        val zenithDryDelayAtSeaLevelSeconds = 1.0e-6 * K1 * RD * pressureMbar / GM
        val zenithWetDelayAtSeaLevelSeconds = (1.0e-6 * K2 * RD
                / (GM * (lambda + 1.0) - beta * RD)) * (waterVaporPressureMbar / tempKelvin)
        val commonBase = 1.0 - beta * heightMetersAboveSeaLevel / tempKelvin
        val powerDry = GRAVITY_MPS2 / (RD * beta)
        val powerWet = (lambda + 1.0) * GRAVITY_MPS2 / (RD * beta) - 1.0
        val zenithDryDelaySeconds = zenithDryDelayAtSeaLevelSeconds * Math.pow(commonBase, powerDry)
        val zenithWetDelaySeconds = zenithWetDelayAtSeaLevelSeconds * Math.pow(commonBase, powerWet)
        return DryAndWetZenithDelays(zenithDryDelaySeconds, zenithWetDelaySeconds)
    }

    /**
     * Interpolates linearly given two points (point1X, point1Y) and (point2X, point2Y). Given the
     * desired value of x (xInterpolated), an interpolated value of y shall be computed and returned.
     */
    private fun interpolate(point1X: Double, point1Y: Double, point2X: Double, point2Y: Double,
                            xOutput: Double): Double {
        // Check that xOutput is between the two interpolation points.
        require(!(point1X < point2X && (xOutput < point1X || xOutput > point2X)
                || point2X < point1X && (xOutput < point2X || xOutput > point1X))) { "Interpolated value is outside the interpolated region" }
        val deltaX = point2X - point1X
        val yOutput: Double
        yOutput = if (Math.abs(deltaX) > MINIMUM_INTERPOLATION_THRESHOLD) {
            point1Y + (xOutput - point1X) / deltaX * (point2Y - point1Y)
        } else {
            point1Y
        }
        return yOutput
    }

    /**
     *
     * A class containing dry and wet mapping values
     */
    private class DryAndWetMappingValues(var dryMappingValue: Double, var wetMappingValue: Double)

    /**
     *
     * A class containing dry and wet delays in seconds experienced at zenith
     */
    private class DryAndWetZenithDelays(var dryZenithDelaySec: Double, var wetZenithDelaySec: Double)
}