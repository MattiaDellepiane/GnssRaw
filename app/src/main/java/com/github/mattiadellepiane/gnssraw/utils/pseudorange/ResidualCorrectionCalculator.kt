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
import java.util.Arrays
import com.github.mattiadellepiane.gnssraw.utils.pseudorange.UserPositionVelocityWeightedLeastSquare.SatellitesPositionPseudorangesResidualAndCovarianceMatrix
import com.google.common.base.Preconditions

/**
 * A tool with the methods to perform the pseudorange residual analysis.
 *
 *
 * The tool allows correcting the pseudorange residuals computed in WLS by removing the user
 * clock error. The user clock bias is computed using the highest elevation satellites as those are
 * assumed not to suffer from multipath. The reported residuals are provided at the input ground
 * truth position by applying an adjustment using the distance of WLS to satellites vs ground-truth
 * to satellites.
 */
object ResidualCorrectionCalculator {
    /**
     * The threshold for the residual of user clock bias per satellite with respect to the best user
     * clock bias.
     */
    private const val BEST_USER_CLOCK_BIAS_RESIDUAL_THRESHOLD_METERS = 10.0

    /* The number of satellites we pick for calculating the best user clock bias */
    private const val MIN_SATS_FOR_BIAS_COMPUTATION = 4

    /**
     * Corrects the pseudorange residual by the best user clock bias estimation computed from the top
     * elevation satellites.
     *
     * @param satellitesPositionPseudorangesResidual satellite position and pseudorange residual info
     * passed in from WLS
     * @param positionVelocitySolutionECEF position velocity solution passed in from WLS
     * @param groundTruthInputECEFMeters the reference position in ECEF meters
     * @return an array contains the corrected pseudorange residual in meters for each satellite
     */
    fun calculateCorrectedResiduals(
            satellitesPositionPseudorangesResidual: SatellitesPositionPseudorangesResidualAndCovarianceMatrix,
            positionVelocitySolutionECEF: DoubleArray,
            groundTruthInputECEFMeters: DoubleArray): DoubleArray? {
        val residuals = satellitesPositionPseudorangesResidual.pseudorangeResidualsMeters.clone()
        val satellitePrn = satellitesPositionPseudorangesResidual.satellitePRNs.clone()
        val satelliteElevationDegree = DoubleArray(residuals.size)
        val satelliteResidualsListAndElevation = arrayOfNulls<SatelliteElevationAndResiduals>(residuals.size)

        // Check the alignment between inputs
        Preconditions.checkArgument(residuals.size == satellitePrn.size)

        // Apply residual corrections per satellite
        for (i in residuals.indices) {
            // Calculate the delta of user-satellite distance between ground truth and WLS solution
            // and use the delta to adjust the residuals computed from the WLS. With this adjustments all
            // residuals will be as if they are computed with respect to the ground truth rather than
            // the WLS.
            val satellitePos = satellitesPositionPseudorangesResidual.satellitesPositionsMeters[i]
            val wlsUserSatelliteDistance = GpsMathOperations.vectorNorm(
                    GpsMathOperations.subtractTwoVectors(
                            Arrays.copyOf(positionVelocitySolutionECEF, 3),
                            satellitePos))
            val groundTruthSatelliteDistance = GpsMathOperations.vectorNorm(
                    GpsMathOperations.subtractTwoVectors(groundTruthInputECEFMeters, satellitePos))

            // Compute the adjustment for satellite i
            val groundTruthAdjustment = wlsUserSatelliteDistance - groundTruthSatelliteDistance

            // Correct the input residual with the adjustment to ground truth
            residuals[i] = residuals[i] - groundTruthAdjustment

            // Calculate the elevation in degrees of satellites
            val topocentricAedValues = EcefToTopocentricConverter.calculateElAzDistBetween2Points(
                    groundTruthInputECEFMeters, satellitesPositionPseudorangesResidual.satellitesPositionsMeters[i]
            )
            satelliteElevationDegree[i] = Math.toDegrees(topocentricAedValues!!.elevationRadians)

            // Store the computed satellite elevations and residuals into a SatelliteElevationAndResiduals
            // list with clock correction removed.
            satelliteResidualsListAndElevation[i] = SatelliteElevationAndResiduals(
                    satelliteElevationDegree[i], residuals[i]
                    + positionVelocitySolutionECEF[3], satellitePrn[i])
        }
        val bestUserClockBiasMeters = calculateBestUserClockBias(satelliteResidualsListAndElevation)

        // Use the best clock bias to correct the residuals to ensure that the receiver clock errors are
        // removed from the reported residuals in the analysis
        val correctedResidualsMeters = GpsMathOperations.createAndFillArray(
                GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES, Double.NaN)
        for (element in satelliteResidualsListAndElevation) {
            correctedResidualsMeters!![element!!.svID - 1] = element.residual - bestUserClockBiasMeters
        }
        return correctedResidualsMeters
    }

    /**
     * Computes the user clock bias by iteratively averaging the clock bias of top elevation
     * satellites.
     *
     * @param satelliteResidualsAndElevationList a list of satellite elevation and
     * pseudorange residuals
     * @return the corrected best user clock bias
     */
    private fun calculateBestUserClockBias(
            satelliteResidualsAndElevationList: Array<SatelliteElevationAndResiduals?>): Double {

        // Sort the satellites by descending order of their elevations
        Arrays.sort(
                satelliteResidualsAndElevationList
        ) { o1, o2 -> java.lang.Double.compare(o2!!.elevationDegree, o1!!.elevationDegree) }

        // Pick up the top elevation satellites
        val topElevationSatsResiduals = GpsMathOperations.createAndFillArray(
                MIN_SATS_FOR_BIAS_COMPUTATION, Double.NaN)
        var numOfUsefulSatsToComputeBias = 0
        var i = 0
        while (i < satelliteResidualsAndElevationList.size
                && i < topElevationSatsResiduals!!.size) {
            topElevationSatsResiduals[i] = satelliteResidualsAndElevationList[i]!!.residual
            numOfUsefulSatsToComputeBias++
            i++
        }
        var meanResidual: Double
        var deltaResidualFromMean: DoubleArray?
        var maxDeltaIndex = -1

        // Iteratively remove the satellites with highest residuals with respect to the mean of the
        // residuals until the highest residual in the list is below threshold.
        do {
            if (maxDeltaIndex >= 0) {
                topElevationSatsResiduals!![maxDeltaIndex] = Double.NaN
                numOfUsefulSatsToComputeBias--
            }
            meanResidual = GpsMathOperations.meanOfVector(topElevationSatsResiduals)
            deltaResidualFromMean = GpsMathOperations.subtractByScalar(topElevationSatsResiduals, meanResidual)
            maxDeltaIndex = GpsMathOperations.maxIndexOfVector(deltaResidualFromMean)
        } while (deltaResidualFromMean!![maxDeltaIndex] > BEST_USER_CLOCK_BIAS_RESIDUAL_THRESHOLD_METERS
                && numOfUsefulSatsToComputeBias > 2)
        return meanResidual
    }

    /** A container for satellite residual and elevationDegree information  */
    private class SatelliteElevationAndResiduals internal constructor(
            /** Satellite elevation in degrees with respect to the user  */
            val elevationDegree: Double,
            /** Satellite pseudorange or pseudorange rate residual with clock correction removed  */
            val residual: Double,
            /** Satellite ID  */
            val svID: Int)
}