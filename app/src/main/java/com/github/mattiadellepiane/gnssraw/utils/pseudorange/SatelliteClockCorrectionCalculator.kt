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


import java.lang.Exception

import kotlin.Throws

import android.location.cts.nano.Ephemeris.GpsEphemerisProto


/**
 * Calculates the GPS satellite clock correction based on parameters observed from the navigation
 * message
 *
 * Source: Page 88 - 90 of the ICD-GPS 200
 */
object SatelliteClockCorrectionCalculator {
    private const val SPEED_OF_LIGHT_MPS = 299792458.0
    private const val EARTH_UNIVERSAL_GRAVITATIONAL_CONSTANT_M3_SM2 = 3.986005e14
    private const val RELATIVISTIC_CONSTANT_F = -4.442807633e-10
    private const val SECONDS_IN_WEEK = 604800
    private const val ACCURACY_TOLERANCE = 1.0e-11
    private const val MAX_ITERATIONS = 100

    /**
     * Computes the GPS satellite clock correction term in meters iteratively following page 88 - 90
     * and 98 - 100 of the ICD GPS 200. The method returns a pair of satellite clock correction in
     * meters and Kepler Eccentric Anomaly in Radians.
     *
     * @param ephemerisProto parameters of the navigation message
     * @param receiverGpsTowAtTimeOfTransmission Receiver estimate of GPS time of week when signal was
     * transmitted (seconds)
     * @param receiverGpsWeekAtTimeOfTransmission Receiver estimate of GPS week when signal was
     * transmitted (0-1024+)
     * @throws Exception
     */
    @Throws(Exception::class)
    fun calculateSatClockCorrAndEccAnomAndTkIteratively(
            ephemerisProto: GpsEphemerisProto?, receiverGpsTowAtTimeOfTransmission: Double,
            receiverGpsWeekAtTimeOfTransmission: Double): SatClockCorrection {
        // Units are not added in the variable names to have the same name as the ICD-GPS200
        // Mean anomaly (radians)
        var meanAnomalyRad: Double
        // Kepler's Equation for Eccentric Anomaly iteratively (Radians)
        var eccentricAnomalyRad: Double
        // Semi-major axis of orbit (meters)
        val a = ephemerisProto!!.rootOfA * ephemerisProto.rootOfA
        // Computed mean motion (radians/seconds)
        val n0 = Math.sqrt(EARTH_UNIVERSAL_GRAVITATIONAL_CONSTANT_M3_SM2 / (a * a * a))
        // Corrected mean motion (radians/seconds)
        val n = n0 + ephemerisProto.deltaN
        // In the following, Receiver GPS week and ephemeris GPS week are used to correct for week
        // rollover when calculating the time from clock reference epoch (tcSec)
        val timeOfTransmissionIncludingRxWeekSec = receiverGpsWeekAtTimeOfTransmission * SECONDS_IN_WEEK + receiverGpsTowAtTimeOfTransmission
        // time from clock reference epoch (seconds) page 88 ICD-GPS200
        var tcSec = (timeOfTransmissionIncludingRxWeekSec
                - (ephemerisProto.week * SECONDS_IN_WEEK + ephemerisProto.toc))
        // Correction for week rollover
        tcSec = fixWeekRollover(tcSec)
        var oldEccentricAnomalyRad = 0.0
        var newSatClockCorrectionSeconds = 0.0
        var relativisticCorrection = 0.0
        var changeInSatClockCorrection = 0.0
        // Initial satellite clock correction (unknown relativistic correction). Iterate to correct
        // with the relativistic effect and obtain a stable
        val initSatClockCorrectionSeconds = (ephemerisProto.af0
                + ephemerisProto.af1 * tcSec + ephemerisProto.af2 * tcSec * tcSec) - ephemerisProto.tgd
        var satClockCorrectionSeconds = initSatClockCorrectionSeconds
        var tkSec: Double
        var satClockCorrectionsCounter = 0
        do {
            var eccentricAnomalyCounter = 0
            // time from ephemeris reference epoch (seconds) page 98 ICD-GPS200
            tkSec = timeOfTransmissionIncludingRxWeekSec - (ephemerisProto.week * SECONDS_IN_WEEK + ephemerisProto.toe
                    + satClockCorrectionSeconds)
            // Correction for week rollover
            tkSec = fixWeekRollover(tkSec)
            // Mean anomaly (radians)
            meanAnomalyRad = ephemerisProto.m0 + n * tkSec
            // eccentric anomaly (radians)
            eccentricAnomalyRad = meanAnomalyRad
            // Iteratively solve for Kepler's eccentric anomaly according to ICD-GPS200 page 99
            do {
                oldEccentricAnomalyRad = eccentricAnomalyRad
                eccentricAnomalyRad = meanAnomalyRad + ephemerisProto.e * Math.sin(eccentricAnomalyRad)
                eccentricAnomalyCounter++
                if (eccentricAnomalyCounter > MAX_ITERATIONS) {
                    throw Exception("Kepler Eccentric Anomaly calculation did not converge in "
                            + MAX_ITERATIONS + " iterations")
                }
            } while (Math.abs(oldEccentricAnomalyRad - eccentricAnomalyRad) > ACCURACY_TOLERANCE)
            // relativistic correction term (seconds)
            relativisticCorrection = (RELATIVISTIC_CONSTANT_F * ephemerisProto.e
                    * ephemerisProto.rootOfA * Math.sin(eccentricAnomalyRad))
            // satellite clock correction including relativistic effect
            newSatClockCorrectionSeconds = initSatClockCorrectionSeconds + relativisticCorrection
            changeInSatClockCorrection = Math.abs(satClockCorrectionSeconds - newSatClockCorrectionSeconds)
            satClockCorrectionSeconds = newSatClockCorrectionSeconds
            satClockCorrectionsCounter++
            if (satClockCorrectionsCounter > MAX_ITERATIONS) {
                throw Exception("Satellite Clock Correction calculation did not converge in "
                        + MAX_ITERATIONS + " iterations")
            }
        } while (changeInSatClockCorrection > ACCURACY_TOLERANCE)
        tkSec = timeOfTransmissionIncludingRxWeekSec - (ephemerisProto.week * SECONDS_IN_WEEK + ephemerisProto.toe
                + satClockCorrectionSeconds)
        // return satellite clock correction (meters) and Kepler Eccentric Anomaly in Radians
        return SatClockCorrection(satClockCorrectionSeconds * SPEED_OF_LIGHT_MPS,
                eccentricAnomalyRad, tkSec)
    }

    /**
     * Calculates Satellite Clock Error Rate in (meters/second) by subtracting the Satellite
     * Clock Error Values at t+0.5s and t-0.5s.
     *
     *
     * This approximation is more accurate than differentiating because both the orbital
     * and relativity terms have non-linearities that are not easily differentiable.
     */
    @Throws(Exception::class)
    fun calculateSatClockCorrErrorRate(
            ephemerisProto: GpsEphemerisProto?, receiverGpsTowAtTimeOfTransmissionSeconds: Double,
            receiverGpsWeekAtTimeOfTransmission: Double): Double {
        val satClockCorrectionPlus = calculateSatClockCorrAndEccAnomAndTkIteratively(
                ephemerisProto, receiverGpsTowAtTimeOfTransmissionSeconds + 0.5,
                receiverGpsWeekAtTimeOfTransmission)
        val satClockCorrectionMinus = calculateSatClockCorrAndEccAnomAndTkIteratively(
                ephemerisProto, receiverGpsTowAtTimeOfTransmissionSeconds - 0.5,
                receiverGpsWeekAtTimeOfTransmission)
        return (satClockCorrectionPlus.satelliteClockCorrectionMeters
                - satClockCorrectionMinus.satelliteClockCorrectionMeters)
    }

    /**
     * Method to check for week rollover according to ICD-GPS 200 page 98.
     *
     *
     * Result should be between -302400 and 302400 if the ephemeris is within one week of
     * transmission, otherwise it is adjusted to the correct range
     */
    private fun fixWeekRollover(time: Double): Double {
        var correctedTime = time
        if (time > SECONDS_IN_WEEK / 2.0) {
            correctedTime = time - SECONDS_IN_WEEK
        }
        if (time < -SECONDS_IN_WEEK / 2.0) {
            correctedTime = time + SECONDS_IN_WEEK
        }
        return correctedTime
    }

    /**
     *
     * Class containing the satellite clock correction parameters: The satellite clock correction in
     * meters, Kepler Eccentric Anomaly in Radians and the time from the reference epoch in seconds.
     */
    class SatClockCorrection
    /**
     * Constructor
     */(
            /**
             * Satellite clock correction in meters
             */
            val satelliteClockCorrectionMeters: Double,
            /**
             * Kepler Eccentric Anomaly in Radians
             */
            val eccentricAnomalyRadians: Double,
            /**
             * Time from the reference epoch in Seconds
             */
            val timeFromRefEpochSec: Double)
}