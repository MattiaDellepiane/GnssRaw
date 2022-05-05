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

import java.util.ArrayList
import java.util.Collections
import java.util.Arrays
import java.lang.Exception
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import kotlin.Throws
import android.location.cts.nano.Ephemeris.GpsNavMessageProto
import android.location.cts.nano.Ephemeris.GpsEphemerisProto
import com.google.common.collect.Lists
import org.apache.commons.math3.linear.LUDecomposition

import org.apache.commons.math3.linear.QRDecomposition

import com.google.common.annotations.VisibleForTesting
import com.google.common.base.Preconditions

/**
 * Computes an iterative least square receiver position solution given the pseudorange (meters) and
 * accumulated delta range (meters) measurements, receiver time of week, week number and the
 * navigation message.
 */
class UserPositionVelocityWeightedLeastSquare {
    private val pseudorangeSmoother: PseudorangeSmoother
    private var geoidHeightMeters = 0.0
    private var elevationApiHelper: ElevationApiHelper? = null
    private var calculateGeoidMeters = true
    private lateinit var geometryMatrix: RealMatrix
    private var truthLocationForCorrectedResidualComputationEcef: DoubleArray? = null

    /** Constructor  */
    constructor(pseudorangeSmoother: PseudorangeSmoother) {
        this.pseudorangeSmoother = pseudorangeSmoother
    }

    /** Constructor with Google Elevation API Key  */
    constructor(pseudorangeSmoother: PseudorangeSmoother,
                elevationApiKey: String) {
        this.pseudorangeSmoother = pseudorangeSmoother
        elevationApiHelper = ElevationApiHelper(elevationApiKey)
    }

    /**
     * Sets the reference ground truth for pseudorange residual correction calculation. If no ground
     * truth is set, no corrected pseudorange residual will be calculated.
     */
    fun setTruthLocationForCorrectedResidualComputationEcef(groundTruthForResidualCorrectionEcef: DoubleArray?) {
        truthLocationForCorrectedResidualComputationEcef = groundTruthForResidualCorrectionEcef
    }

    /**
     * Least square solution to calculate the user position given the navigation message, pseudorange
     * and accumulated delta range measurements. Also calculates user velocity non-iteratively from
     * Least square position solution.
     *
     *
     * The method fills the user position and velocity in ECEF coordinates and receiver clock
     * offset in meters and clock offset rate in meters per second.
     *
     *
     * One can choose between no smoothing, using the carrier phase measurements (accumulated delta
     * range) or the doppler measurements (pseudorange rate) for smoothing the pseudorange. The
     * smoothing is applied only if time has changed below a specific threshold since last invocation.
     *
     *
     * Source for least squares:
     *
     *
     *  * http://www.u-blox.com/images/downloads/Product_Docs/GPS_Compendium%28GPS-X-02007%29.pdf
     * page 81 - 85
     *  * Parkinson, B.W., Spilker Jr., J.J.: ‘Global positioning system: theory and applications’
     * page 412 - 414
     *
     *
     *
     * Sources for smoothing pseudorange with carrier phase measurements:
     *
     *
     *  * Satellite Communications and Navigation Systems book, page 424,
     *  * Principles of GNSS, Inertial, and Multisensor Integrated Navigation Systems, page 388,
     * 389.
     *
     *
     *
     * The function does not modify the smoothed measurement list `immutableSmoothedSatellitesToReceiverMeasurements`
     *
     * @param navMessageProto parameters of the navigation message
     * @param usefulSatellitesToReceiverMeasurements Map of useful satellite PRN to [     ] containing receiver measurements for computing the
     * position solution.
     * @param receiverGPSTowAtReceptionSeconds Receiver estimate of GPS time of week (seconds)
     * @param receiverGPSWeek Receiver estimate of GPS week (0-1024+)
     * @param dayOfYear1To366 The day of the year between 1 and 366
     * @param positionVelocitySolutionECEF Solution array of the following format:
     * [0-2] xyz solution of user.
     * [3] clock bias of user.
     * [4-6] velocity of user.
     * [7] clock bias rate of user.
     * @param positionVelocityUncertaintyEnu Uncertainty of calculated position and velocity solution
     * in meters and mps local ENU system. Array has the following format:
     * [0-2] Enu uncertainty of position solution in meters
     * [3-5] Enu uncertainty of velocity solution in meters per second.
     * @param pseudorangeResidualMeters The pseudorange residual corrected by subtracting expected
     * pseudorange calculated with the use clock bias of the highest elevation satellites.
     */
    @Throws(Exception::class)
    fun calculateUserPositionVelocityLeastSquare(
            navMessageProto: GpsNavMessageProto?,
            usefulSatellitesToReceiverMeasurements: List<GpsMeasurementWithRangeAndUncertainty>?,
            receiverGPSTowAtReceptionSeconds: Double,
            receiverGPSWeek: Int,
            dayOfYear1To366: Int,
            positionVelocitySolutionECEF: DoubleArray?,
            positionVelocityUncertaintyEnu: DoubleArray?,
            pseudorangeResidualMeters: DoubleArray?) {

        // Use PseudorangeSmoother to smooth the pseudorange according to: Satellite Communications and
        // Navigation Systems book, page 424 and Principles of GNSS, Inertial, and Multisensor
        // Integrated Navigation Systems, page 388, 389.
        var receiverGPSTowAtReceptionSeconds = receiverGPSTowAtReceptionSeconds
        var deltaPositionMeters: DoubleArray?
        val immutableSmoothedSatellitesToReceiverMeasurements = pseudorangeSmoother.updatePseudorangeSmoothingResult(
                Collections.unmodifiableList(usefulSatellitesToReceiverMeasurements))
        val mutableSmoothedSatellitesToReceiverMeasurements: MutableList<GpsMeasurementWithRangeAndUncertainty?> = Lists.newArrayList(immutableSmoothedSatellitesToReceiverMeasurements)
        var numberOfUsefulSatellites = getNumberOfUsefulSatellites(mutableSmoothedSatellitesToReceiverMeasurements)
        // Least square position solution is supported only if 4 or more satellites visible
        Preconditions.checkArgument(numberOfUsefulSatellites >= MINIMUM_NUMBER_OF_SATELLITES,
                "At least 4 satellites have to be visible... Only 3D mode is supported...")
        var repeatLeastSquare = false
        var satPosPseudorangeResidualAndWeight: SatellitesPositionPseudorangesResidualAndCovarianceMatrix
        var isFirstWLS = true
        do {
            // Calculate satellites' positions, measurement residuals per visible satellite and
            // weight matrix for the iterative least square
            val doAtmosphericCorrections = false
            satPosPseudorangeResidualAndWeight = calculateSatPosAndPseudorangeResidual(
                    navMessageProto,
                    mutableSmoothedSatellitesToReceiverMeasurements,
                    receiverGPSTowAtReceptionSeconds,
                    receiverGPSWeek,
                    dayOfYear1To366,
                    positionVelocitySolutionECEF,
                    doAtmosphericCorrections)

            // Calculate the geometry matrix according to "Global Positioning System: Theory and
            // Applications", Parkinson and Spilker page 413
            val covarianceMatrixM2: RealMatrix = Array2DRowRealMatrix(satPosPseudorangeResidualAndWeight.covarianceMatrixMetersSquare)
            geometryMatrix = Array2DRowRealMatrix(calculateGeometryMatrix(
                    satPosPseudorangeResidualAndWeight.satellitesPositionsMeters,
                    positionVelocitySolutionECEF))
            var weightedGeometryMatrix: RealMatrix
            var weightMatrixMetersMinus2: RealMatrix? = null
            // Apply weighted least square only if the covariance matrix is not singular (has a non-zero
            // determinant), otherwise apply ordinary least square. The reason is to ignore reported
            // signal to noise ratios by the receiver that can lead to such singularities
            val ludCovMatrixM2 = LUDecomposition(covarianceMatrixM2)
            val det = ludCovMatrixM2.determinant
            if (det <= DOUBLE_ROUND_OFF_TOLERANCE) {
                // Do not weight the geometry matrix if covariance matrix is singular.
                weightedGeometryMatrix = geometryMatrix
            } else {
                weightMatrixMetersMinus2 = ludCovMatrixM2.solver.inverse
                val hMatrix = calculateHMatrix(weightMatrixMetersMinus2, geometryMatrix)
                weightedGeometryMatrix = hMatrix.multiply(geometryMatrix.transpose())
                        .multiply(weightMatrixMetersMinus2)
            }

            // Equation 9 page 413 from "Global Positioning System: Theory and Applications", Parkinson
            // and Spilker
            deltaPositionMeters = GpsMathOperations.matrixByColVectMultiplication(weightedGeometryMatrix.data,
                    satPosPseudorangeResidualAndWeight.pseudorangeResidualsMeters)

            // Apply corrections to the position estimate
            positionVelocitySolutionECEF!![0] += deltaPositionMeters[0]
            positionVelocitySolutionECEF!![1] += deltaPositionMeters[1]
            positionVelocitySolutionECEF!![2] += deltaPositionMeters[2]
            positionVelocitySolutionECEF!![3] += deltaPositionMeters[3]
            // Iterate applying corrections to the position solution until correction is below threshold
            satPosPseudorangeResidualAndWeight = applyWeightedLeastSquare(
                    navMessageProto,
                    mutableSmoothedSatellitesToReceiverMeasurements,
                    receiverGPSTowAtReceptionSeconds,
                    receiverGPSWeek,
                    dayOfYear1To366,
                    positionVelocitySolutionECEF,
                    deltaPositionMeters,
                    doAtmosphericCorrections,
                    satPosPseudorangeResidualAndWeight,
                    weightMatrixMetersMinus2)

            // We use the first WLS iteration results and correct them based on the ground truth position
            // and using a clock error computed from high elevation satellites. The first iteration is
            // used before satellite with high residuals being removed.
            if (isFirstWLS && truthLocationForCorrectedResidualComputationEcef != null) {
                // Snapshot the information needed before high residual satellites are removed
                System.arraycopy(
                        ResidualCorrectionCalculator.calculateCorrectedResiduals(
                                satPosPseudorangeResidualAndWeight,
                                positionVelocitySolutionECEF!!.clone(),
                                truthLocationForCorrectedResidualComputationEcef!!),
                        0 /*source starting pos*/,
                        pseudorangeResidualMeters,
                        0 /*destination starting pos*/,
                        GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES /*length of elements*/)
                isFirstWLS = false
            }
            repeatLeastSquare = false
            val satsWithResidualBelowThreshold = satPosPseudorangeResidualAndWeight.pseudorangeResidualsMeters.size
            // remove satellites that have residuals above RESIDUAL_TO_REPEAT_LEAST_SQUARE_METERS as they
            // worsen the position solution accuracy. If any satellite is removed, repeat the least square
            repeatLeastSquare = removeHighResidualSats(
                    mutableSmoothedSatellitesToReceiverMeasurements,
                    repeatLeastSquare,
                    satPosPseudorangeResidualAndWeight,
                    satsWithResidualBelowThreshold)
        } while (repeatLeastSquare)
        calculateGeoidMeters = false

        // The computed ECEF position will be used next to compute the user velocity.
        // we calculate and fill in the user velocity solutions based on following equation:
        // Weight Matrix * GeometryMatrix * User Velocity Vector
        // = Weight Matrix * deltaPseudoRangeRateWeightedMps
        // Reference: Pratap Misra and Per Enge
        // "Global Positioning System: Signals, Measurements, and Performance" Page 218.

        // Get the number of satellite used in Geometry Matrix
        numberOfUsefulSatellites = geometryMatrix.getRowDimension()
        val rangeRateMps: RealMatrix = Array2DRowRealMatrix(numberOfUsefulSatellites, 1)
        val deltaPseudoRangeRateMps: RealMatrix = Array2DRowRealMatrix(numberOfUsefulSatellites, 1)
        val pseudorangeRateWeight: RealMatrix = Array2DRowRealMatrix(numberOfUsefulSatellites, numberOfUsefulSatellites)

        // Correct the receiver time of week with the estimated receiver clock bias
        receiverGPSTowAtReceptionSeconds = receiverGPSTowAtReceptionSeconds - positionVelocitySolutionECEF!![3] / SPEED_OF_LIGHT_MPS
        var measurementCount = 0

        // Calculate range rates
        for (i in 0 until GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES) {
            if (mutableSmoothedSatellitesToReceiverMeasurements[i] != null) {
                val ephemeridesProto = getEphemerisForSatellite(navMessageProto, i + 1)
                val pseudorangeMeasurementMeters = mutableSmoothedSatellitesToReceiverMeasurements[i]!!.pseudorangeMeters
                val correctedTowAndWeek = calculateCorrectedTransmitTowAndWeek(ephemeridesProto, receiverGPSTowAtReceptionSeconds,
                        receiverGPSWeek, pseudorangeMeasurementMeters)

                // Calculate satellite velocity
                val satPosECEFMetersVelocityMPS = SatellitePositionCalculator.calculateSatellitePositionAndVelocityFromEphemeris(
                        ephemeridesProto,
                        correctedTowAndWeek.gpsTimeOfWeekSeconds,
                        correctedTowAndWeek.weekNumber,
                        positionVelocitySolutionECEF[0],
                        positionVelocitySolutionECEF[1],
                        positionVelocitySolutionECEF[2])

                // Calculate satellite clock error rate
                val satelliteClockErrorRateMps = SatelliteClockCorrectionCalculator.calculateSatClockCorrErrorRate(
                        ephemeridesProto,
                        correctedTowAndWeek.gpsTimeOfWeekSeconds,
                        correctedTowAndWeek.weekNumber.toDouble())

                // Fill in range rates. range rate = satellite velocity (dot product) line-of-sight vector
                rangeRateMps.setEntry(measurementCount, 0, -1 * ((satPosECEFMetersVelocityMPS!!.velocityXMetersPerSec
                        * geometryMatrix.getEntry(measurementCount, 0)) + (satPosECEFMetersVelocityMPS.velocityYMetersPerSec
                        * geometryMatrix.getEntry(measurementCount, 1)) + (satPosECEFMetersVelocityMPS.velocityZMetersPerSec
                        * geometryMatrix.getEntry(measurementCount, 2))))
                deltaPseudoRangeRateMps.setEntry(measurementCount, 0, mutableSmoothedSatellitesToReceiverMeasurements[i]!!.pseudorangeRateMps
                        - rangeRateMps.getEntry(measurementCount, 0) + satelliteClockErrorRateMps
                        - positionVelocitySolutionECEF[7])

                // Calculate the velocity weight matrix by using 1 / square(PseudorangeRate Uncertainty)
                // along the diagonal
                pseudorangeRateWeight.setEntry(measurementCount, measurementCount,
                        1 / (mutableSmoothedSatellitesToReceiverMeasurements[i]!!.pseudorangeRateUncertaintyMps
                                * mutableSmoothedSatellitesToReceiverMeasurements[i]!!.pseudorangeRateUncertaintyMps))
                measurementCount++
            }
        }
        val weightedGeoMatrix = pseudorangeRateWeight.multiply(geometryMatrix)
        val deltaPseudoRangeRateWeightedMps = pseudorangeRateWeight.multiply(deltaPseudoRangeRateMps)
        val qrdWeightedGeoMatrix = QRDecomposition(weightedGeoMatrix)
        val velocityMps = qrdWeightedGeoMatrix.solver.solve(deltaPseudoRangeRateWeightedMps)
        positionVelocitySolutionECEF[4] = velocityMps.getEntry(0, 0)
        positionVelocitySolutionECEF[5] = velocityMps.getEntry(1, 0)
        positionVelocitySolutionECEF[6] = velocityMps.getEntry(2, 0)
        positionVelocitySolutionECEF[7] = velocityMps.getEntry(3, 0)
        val pseudorangeWeight = LUDecomposition(
                Array2DRowRealMatrix(satPosPseudorangeResidualAndWeight.covarianceMatrixMetersSquare
                )
        ).solver.inverse

        // Calculate and store the uncertainties of position and velocity in local ENU system in meters
        // and meters per second.
        val pvUncertainty = calculatePositionVelocityUncertaintyEnu(pseudorangeRateWeight, pseudorangeWeight,
                positionVelocitySolutionECEF)
        System.arraycopy(pvUncertainty,
                0 /*source starting pos*/,
                positionVelocityUncertaintyEnu,
                0 /*destination starting pos*/,
                6 /*length of elements*/)
    }

    /**
     * Calculates the position uncertainty in meters and the velocity uncertainty
     * in meters per second solution in local ENU system.
     *
     *
     *  Reference: Global Positioning System: Signals, Measurements, and Performance
     * by Pratap Misra, Per Enge, Page 206 - 209.
     *
     * @param velocityWeightMatrix the velocity weight matrix
     * @param positionWeightMatrix the position weight matrix
     * @param positionVelocitySolution the position and velocity solution in ECEF
     * @return an array containing the position and velocity uncertainties in ENU coordinate system.
     * [0-2] Enu uncertainty of position solution in meters.
     * [3-5] Enu uncertainty of velocity solution in meters per second.
     */
    fun calculatePositionVelocityUncertaintyEnu(
            velocityWeightMatrix: RealMatrix?, positionWeightMatrix: RealMatrix?,
            positionVelocitySolution: DoubleArray?): DoubleArray? {
        if (geometryMatrix == null) {
            return null
        }
        var velocityH = calculateHMatrix(velocityWeightMatrix, geometryMatrix!!)
        var positionH = calculateHMatrix(positionWeightMatrix, geometryMatrix!!)

        // Calculate the rotation Matrix to convert to local ENU system.
        val rotationMatrix: RealMatrix = Array2DRowRealMatrix(4, 4)
        val llaValues = Ecef2LlaConverter.convertECEFToLLACloseForm(positionVelocitySolution!![0], positionVelocitySolution[1], positionVelocitySolution[2])
        rotationMatrix.setSubMatrix(
                Ecef2EnuConverter.getRotationMatrix(llaValues!!.longitudeRadians,
                        llaValues.latitudeRadians).data, 0, 0)
        rotationMatrix.setEntry(3, 3, 1.0)

        // Convert to local ENU by pre-multiply rotation matrix and multiply rotation matrix transposed
        velocityH = rotationMatrix.multiply(velocityH).multiply(rotationMatrix.transpose())
        positionH = rotationMatrix.multiply(positionH).multiply(rotationMatrix.transpose())

        // Return the square root of diagonal entries
        return doubleArrayOf(
                Math.sqrt(positionH.getEntry(0, 0)), Math.sqrt(positionH.getEntry(1, 1)),
                Math.sqrt(positionH.getEntry(2, 2)), Math.sqrt(velocityH.getEntry(0, 0)),
                Math.sqrt(velocityH.getEntry(1, 1)), Math.sqrt(velocityH.getEntry(2, 2)))
    }

    /**
     * Calculates the measurement connection matrix H as a function of weightMatrix and
     * geometryMatrix.
     *
     *
     *  H = (geometryMatrixTransposed * Weight * geometryMatrix) ^ -1
     *
     *
     *  Reference: Global Positioning System: Signals, Measurements, and Performance, P207
     * @param weightMatrix Weights for computing H Matrix
     * @return H Matrix
     */
    private fun calculateHMatrix(weightMatrix: RealMatrix?, geometryMatrix: RealMatrix): RealMatrix {
        val tempH = geometryMatrix.transpose().multiply(weightMatrix).multiply(geometryMatrix)
        return LUDecomposition(tempH).solver.inverse
    }

    /**
     * Applies weighted least square iterations and corrects to the position solution until correction
     * is below threshold. An exception is thrown if the maximum number of iterations:
     * {@value #MAXIMUM_NUMBER_OF_LEAST_SQUARE_ITERATIONS} is reached without convergence.
     */
    @Throws(Exception::class)
    private fun applyWeightedLeastSquare(
            navMessageProto: GpsNavMessageProto?,
            usefulSatellitesToReceiverMeasurements: List<GpsMeasurementWithRangeAndUncertainty?>,
            receiverGPSTowAtReceptionSeconds: Double,
            receiverGPSWeek: Int,
            dayOfYear1To366: Int,
            positionSolutionECEF: DoubleArray?,
            deltaPositionMeters: DoubleArray?,
            doAtmosphericCorrections: Boolean,
            satPosPseudorangeResidualAndWeight: SatellitesPositionPseudorangesResidualAndCovarianceMatrix,
            weightMatrixMetersMinus2: RealMatrix?): SatellitesPositionPseudorangesResidualAndCovarianceMatrix {
        var deltaPositionMeters = deltaPositionMeters
        var doAtmosphericCorrections = doAtmosphericCorrections
        var satPosPseudorangeResidualAndWeight = satPosPseudorangeResidualAndWeight
        var weightedGeometryMatrix: RealMatrix
        var numberOfIterations = 0
        while ((Math.abs(deltaPositionMeters!![0]) + Math.abs(deltaPositionMeters[1])
                        + Math.abs(deltaPositionMeters[2])) >= LEAST_SQUARE_TOLERANCE_METERS) {
            // Apply ionospheric and tropospheric corrections only if the applied correction to
            // position is below a specific threshold
            if ((Math.abs(deltaPositionMeters[0]) + Math.abs(deltaPositionMeters[1])
                            + Math.abs(deltaPositionMeters[2])) < ATMOSPHERIC_CORRECTIONS_THRESHOLD_METERS) {
                doAtmosphericCorrections = true
            }
            // Calculate satellites' positions, measurement residual per visible satellite and
            // weight matrix for the iterative least square
            satPosPseudorangeResidualAndWeight = calculateSatPosAndPseudorangeResidual(
                    navMessageProto,
                    usefulSatellitesToReceiverMeasurements,
                    receiverGPSTowAtReceptionSeconds,
                    receiverGPSWeek,
                    dayOfYear1To366,
                    positionSolutionECEF,
                    doAtmosphericCorrections)

            // Calculate the geometry matrix according to "Global Positioning System: Theory and
            // Applications", Parkinson and Spilker page 413
            geometryMatrix = Array2DRowRealMatrix(calculateGeometryMatrix(
                    satPosPseudorangeResidualAndWeight.satellitesPositionsMeters, positionSolutionECEF))
            // Apply weighted least square only if the covariance matrix is
            // not singular (has a non-zero determinant), otherwise apply ordinary least square.
            // The reason is to ignore reported signal to noise ratios by the receiver that can
            // lead to such singularities
            weightedGeometryMatrix = if (weightMatrixMetersMinus2 == null) {
                geometryMatrix
            } else {
                val hMatrix = calculateHMatrix(weightMatrixMetersMinus2, geometryMatrix)
                hMatrix.multiply(geometryMatrix.transpose())
                        .multiply(weightMatrixMetersMinus2)
            }

            // Equation 9 page 413 from "Global Positioning System: Theory and Applications",
            // Parkinson and Spilker
            deltaPositionMeters = GpsMathOperations.matrixByColVectMultiplication(
                    weightedGeometryMatrix.data,
                    satPosPseudorangeResidualAndWeight.pseudorangeResidualsMeters)

            // Apply corrections to the position estimate
            positionSolutionECEF!![0] += deltaPositionMeters[0]
            positionSolutionECEF!![1] += deltaPositionMeters[1]
            positionSolutionECEF!![2] += deltaPositionMeters[2]
            positionSolutionECEF!![3] += deltaPositionMeters[3]
            numberOfIterations++
            Preconditions.checkArgument(numberOfIterations <= MAXIMUM_NUMBER_OF_LEAST_SQUARE_ITERATIONS,
                    "Maximum number of least square iterations reached without convergence...")
        }
        return satPosPseudorangeResidualAndWeight
    }

    /**
     * Removes satellites that have residuals above {@value #RESIDUAL_TO_REPEAT_LEAST_SQUARE_METERS}
     * from the `usefulSatellitesToReceiverMeasurements` list. Returns true if any satellite is
     * removed.
     */
    private fun removeHighResidualSats(
            usefulSatellitesToReceiverMeasurements: MutableList<GpsMeasurementWithRangeAndUncertainty?>,
            repeatLeastSquare: Boolean,
            satPosPseudorangeResidualAndWeight: SatellitesPositionPseudorangesResidualAndCovarianceMatrix,
            satsWithResidualBelowThreshold: Int): Boolean {
        var repeatLeastSquare = repeatLeastSquare
        var satsWithResidualBelowThreshold = satsWithResidualBelowThreshold
        for (i in satPosPseudorangeResidualAndWeight.pseudorangeResidualsMeters.indices) {
            if (satsWithResidualBelowThreshold > MINIMUM_NUMBER_OF_SATELLITES) {
                if (Math.abs(satPosPseudorangeResidualAndWeight.pseudorangeResidualsMeters[i])
                        > RESIDUAL_TO_REPEAT_LEAST_SQUARE_METERS) {
                    val prn = satPosPseudorangeResidualAndWeight.satellitePRNs[i]
                    usefulSatellitesToReceiverMeasurements[prn - 1] = null
                    satsWithResidualBelowThreshold--
                    repeatLeastSquare = true
                }
            }
        }
        return repeatLeastSquare
    }

    /**
     * Calculates position of all visible satellites and pseudorange measurement residual
     * (difference of measured to predicted pseudoranges) needed for the least square computation. The
     * result is stored in an instance of [ ]
     *
     * @param navMessageProto parameters of the navigation message
     * @param usefulSatellitesToReceiverMeasurements Map of useful satellite PRN to [     ] containing receiver measurements for computing the
     * position solution
     * @param receiverGPSTowAtReceptionSeconds Receiver estimate of GPS time of week (seconds)
     * @param receiverGpsWeek Receiver estimate of GPS week (0-1024+)
     * @param dayOfYear1To366 The day of the year between 1 and 366
     * @param userPositionECEFMeters receiver ECEF position in meters
     * @param doAtmosphericCorrections boolean indicating if atmospheric range corrections should be
     * applied
     * @return SatellitesPositionPseudorangesResidualAndCovarianceMatrix Object containing satellite
     * prns, satellite positions in ECEF, pseudorange residuals and covariance matrix.
     */
    @Throws(Exception::class)
    fun calculateSatPosAndPseudorangeResidual(
            navMessageProto: GpsNavMessageProto?,
            usefulSatellitesToReceiverMeasurements: List<GpsMeasurementWithRangeAndUncertainty?>,
            receiverGPSTowAtReceptionSeconds: Double,
            receiverGpsWeek: Int,
            dayOfYear1To366: Int,
            userPositionECEFMeters: DoubleArray?,
            doAtmosphericCorrections: Boolean): SatellitesPositionPseudorangesResidualAndCovarianceMatrix {
        val numberOfUsefulSatellites = getNumberOfUsefulSatellites(usefulSatellitesToReceiverMeasurements)
        // deltaPseudorange is the pseudorange measurement residual
        val deltaPseudorangesMeters = DoubleArray(numberOfUsefulSatellites)
        val satellitesPositionsECEFMeters = Array<DoubleArray?>(numberOfUsefulSatellites) { DoubleArray(3) }

        // satellite PRNs
        val satellitePRNs = IntArray(numberOfUsefulSatellites)

        // Ionospheric model parameters
        val alpha = doubleArrayOf(navMessageProto!!.iono.alpha[0], navMessageProto.iono.alpha[1],
                navMessageProto.iono.alpha[2], navMessageProto.iono.alpha[3])
        val beta = doubleArrayOf(navMessageProto.iono.beta[0], navMessageProto.iono.beta[1],
                navMessageProto.iono.beta[2], navMessageProto.iono.beta[3])
        // Weight matrix for the weighted least square
        val covarianceMatrixMetersSquare: RealMatrix = Array2DRowRealMatrix(numberOfUsefulSatellites, numberOfUsefulSatellites)
        calculateSatPosAndResiduals(
                navMessageProto,
                usefulSatellitesToReceiverMeasurements,
                receiverGPSTowAtReceptionSeconds,
                receiverGpsWeek,
                dayOfYear1To366,
                userPositionECEFMeters,
                doAtmosphericCorrections,
                deltaPseudorangesMeters,
                satellitesPositionsECEFMeters,
                satellitePRNs,
                alpha,
                beta,
                covarianceMatrixMetersSquare)
        return SatellitesPositionPseudorangesResidualAndCovarianceMatrix(satellitePRNs,
                satellitesPositionsECEFMeters, deltaPseudorangesMeters,
                covarianceMatrixMetersSquare.data)
    }

    /**
     * Calculates and fill the position of all visible satellites:
     * `satellitesPositionsECEFMeters`, pseudorange measurement residual (difference of
     * measured to predicted pseudoranges): `deltaPseudorangesMeters` and covariance matrix from
     * the weighted least square: `covarianceMatrixMetersSquare`. An array of the satellite PRNs
     * `satellitePRNs` is as well filled.
     */
    @Throws(Exception::class)
    private fun calculateSatPosAndResiduals(
            navMessageProto: GpsNavMessageProto?,
            usefulSatellitesToReceiverMeasurements: List<GpsMeasurementWithRangeAndUncertainty?>,
            receiverGPSTowAtReceptionSeconds: Double,
            receiverGpsWeek: Int,
            dayOfYear1To366: Int,
            userPositionECEFMeters: DoubleArray?,
            doAtmosphericCorrections: Boolean,
            deltaPseudorangesMeters: DoubleArray,
            satellitesPositionsECEFMeters: Array<DoubleArray?>,
            satellitePRNs: IntArray,
            alpha: DoubleArray,
            beta: DoubleArray,
            covarianceMatrixMetersSquare: RealMatrix) {
        // user position without the clock estimate
        var receiverGPSTowAtReceptionSeconds = receiverGPSTowAtReceptionSeconds
        val userPositionTempECEFMeters = doubleArrayOf(userPositionECEFMeters!![0], userPositionECEFMeters[1], userPositionECEFMeters[2])
        var satsCounter = 0
        for (i in 0 until GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES) {
            if (usefulSatellitesToReceiverMeasurements[i] != null) {
                val ephemeridesProto = getEphemerisForSatellite(navMessageProto, i + 1)
                // Correct the receiver time of week with the estimated receiver clock bias
                receiverGPSTowAtReceptionSeconds = receiverGPSTowAtReceptionSeconds - userPositionECEFMeters[3] / SPEED_OF_LIGHT_MPS
                val pseudorangeMeasurementMeters = usefulSatellitesToReceiverMeasurements[i]!!.pseudorangeMeters
                val pseudorangeUncertaintyMeters = usefulSatellitesToReceiverMeasurements[i]!!.pseudorangeUncertaintyMeters

                // Assuming uncorrelated pseudorange measurements, the covariance matrix will be diagonal as
                // follows
                covarianceMatrixMetersSquare.setEntry(satsCounter, satsCounter,
                        pseudorangeUncertaintyMeters * pseudorangeUncertaintyMeters)

                // Calculate time of week at transmission time corrected with the satellite clock drift
                val correctedTowAndWeek = calculateCorrectedTransmitTowAndWeek(ephemeridesProto, receiverGPSTowAtReceptionSeconds,
                        receiverGpsWeek, pseudorangeMeasurementMeters)

                // calculate satellite position and velocity
                val satPosECEFMetersVelocityMPS = SatellitePositionCalculator.calculateSatellitePositionAndVelocityFromEphemeris(ephemeridesProto,
                        correctedTowAndWeek.gpsTimeOfWeekSeconds, correctedTowAndWeek.weekNumber,
                        userPositionECEFMeters[0], userPositionECEFMeters[1], userPositionECEFMeters[2])
                satellitesPositionsECEFMeters[satsCounter]!![0] = satPosECEFMetersVelocityMPS!!.positionXMeters
                satellitesPositionsECEFMeters[satsCounter]!![1] = satPosECEFMetersVelocityMPS.positionYMeters
                satellitesPositionsECEFMeters[satsCounter]!![2] = satPosECEFMetersVelocityMPS.positionZMeters

                // Calculate ionospheric and tropospheric corrections
                var ionosphericCorrectionMeters: Double
                var troposphericCorrectionMeters: Double
                if (doAtmosphericCorrections) {
                    ionosphericCorrectionMeters = (IonosphericModel.ionoKlobucharCorrectionSeconds(
                            userPositionTempECEFMeters,
                            satellitesPositionsECEFMeters[satsCounter],
                            correctedTowAndWeek.gpsTimeOfWeekSeconds,
                            alpha,
                            beta,
                            IonosphericModel.L1_FREQ_HZ)
                            * SPEED_OF_LIGHT_MPS)
                    troposphericCorrectionMeters = calculateTroposphericCorrectionMeters(
                            dayOfYear1To366,
                            satellitesPositionsECEFMeters,
                            userPositionTempECEFMeters,
                            satsCounter)
                } else {
                    troposphericCorrectionMeters = 0.0
                    ionosphericCorrectionMeters = 0.0
                }
                val predictedPseudorangeMeters = calculatePredictedPseudorange(userPositionECEFMeters, satellitesPositionsECEFMeters,
                        userPositionTempECEFMeters, satsCounter, ephemeridesProto, correctedTowAndWeek,
                        ionosphericCorrectionMeters, troposphericCorrectionMeters)

                // Pseudorange residual (difference of measured to predicted pseudoranges)
                deltaPseudorangesMeters[satsCounter] = pseudorangeMeasurementMeters - predictedPseudorangeMeters

                // Satellite PRNs
                satellitePRNs[satsCounter] = i + 1
                satsCounter++
            }
        }
    }

    /** Searches ephemerides list for the ephemeris associated with current satellite in process  */
    private fun getEphemerisForSatellite(navMessageProto: GpsNavMessageProto?,
                                         satPrn: Int): GpsEphemerisProto? {
        val ephemeridesList: List<GpsEphemerisProto> = ArrayList(Arrays.asList(*navMessageProto!!.ephemerids))
        var ephemeridesProto: GpsEphemerisProto? = null
        var ephemerisPrn = 0
        for (ephProtoFromList in ephemeridesList) {
            ephemerisPrn = ephProtoFromList.prn
            if (ephemerisPrn == satPrn) {
                ephemeridesProto = ephProtoFromList
                break
            }
        }
        return ephemeridesProto
    }

    /** Calculates predicted pseudorange in meters  */
    @Throws(Exception::class)
    private fun calculatePredictedPseudorange(
            userPositionECEFMeters: DoubleArray?,
            satellitesPositionsECEFMeters: Array<DoubleArray?>,
            userPositionNoClockECEFMeters: DoubleArray,
            satsCounter: Int,
            ephemeridesProto: GpsEphemerisProto?,
            correctedTowAndWeek: GpsTimeOfWeekAndWeekNumber,
            ionosphericCorrectionMeters: Double,
            troposphericCorrectionMeters: Double): Double {
        // Calculate the satellite clock drift
        val satelliteClockCorrectionMeters = SatelliteClockCorrectionCalculator.calculateSatClockCorrAndEccAnomAndTkIteratively(
                ephemeridesProto,
                correctedTowAndWeek.gpsTimeOfWeekSeconds,
                correctedTowAndWeek.weekNumber.toDouble()).satelliteClockCorrectionMeters
        val satelliteToUserDistanceMeters = GpsMathOperations.vectorNorm(GpsMathOperations.subtractTwoVectors(
                satellitesPositionsECEFMeters[satsCounter], userPositionNoClockECEFMeters))
        // Predicted pseudorange
        return (satelliteToUserDistanceMeters - satelliteClockCorrectionMeters + ionosphericCorrectionMeters
                + troposphericCorrectionMeters + userPositionECEFMeters!![3])
    }

    /** Calculates the Gps tropospheric correction in meters  */
    private fun calculateTroposphericCorrectionMeters(dayOfYear1To366: Int,
                                                      satellitesPositionsECEFMeters: Array<DoubleArray?>, userPositionTempECEFMeters: DoubleArray,
                                                      satsCounter: Int): Double {
        val troposphericCorrectionMeters: Double
        val elevationAzimuthDist = EcefToTopocentricConverter.convertCartesianToTopocentricRadMeters(
                userPositionTempECEFMeters, GpsMathOperations.subtractTwoVectors(
                satellitesPositionsECEFMeters[satsCounter], userPositionTempECEFMeters))
        val lla = Ecef2LlaConverter.convertECEFToLLACloseForm(userPositionTempECEFMeters[0],
                userPositionTempECEFMeters[1], userPositionTempECEFMeters[2])

        // Geoid of the area where the receiver is located is calculated once and used for the
        // rest of the dataset as it change very slowly over wide area. This to save the delay
        // associated with accessing Google Elevation API. We assume this very first iteration of WLS
        // will compute the correct altitude above the ellipsoid of the ground at the latitude and
        // longitude
        if (calculateGeoidMeters) {
            var elevationAboveSeaLevelMeters = 0.0
            if (elevationApiHelper == null) {
                println("No Google API key is set. Elevation above sea level is set to "
                        + "default 0 meters. This may cause inaccuracy in tropospheric correction.")
            } else {
                try {
                    elevationAboveSeaLevelMeters = elevationApiHelper!!
                            .getElevationAboveSeaLevelMeters(
                                    Math.toDegrees(lla!!.latitudeRadians), Math.toDegrees(lla.longitudeRadians)
                            )
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Error when getting elevation from Google Server. "
                            + "Could be wrong Api key or network error. Elevation above sea level is set to "
                            + "default 0 meters. This may cause inaccuracy in tropospheric correction.")
                }
            }
            geoidHeightMeters = ElevationApiHelper.Companion.calculateGeoidHeightMeters(
                    lla!!.altitudeMeters,
                    elevationAboveSeaLevelMeters
            )
            troposphericCorrectionMeters = TroposphericModelEgnos.calculateTropoCorrectionMeters(
                    elevationAzimuthDist!!.elevationRadians, lla.latitudeRadians, elevationAboveSeaLevelMeters,
                    dayOfYear1To366)
        } else {
            troposphericCorrectionMeters = TroposphericModelEgnos.calculateTropoCorrectionMeters(
                    elevationAzimuthDist!!.elevationRadians, lla!!.latitudeRadians,
                    lla.altitudeMeters - geoidHeightMeters, dayOfYear1To366)
        }
        return troposphericCorrectionMeters
    }

    /**
     * Gets the number of useful satellites from a list of
     * [GpsMeasurementWithRangeAndUncertainty].
     */
    private fun getNumberOfUsefulSatellites(
            usefulSatellitesToReceiverMeasurements: List<GpsMeasurementWithRangeAndUncertainty?>): Int {
        // calculate the number of useful satellites
        var numberOfUsefulSatellites = 0
        for (i in usefulSatellitesToReceiverMeasurements.indices) {
            if (usefulSatellitesToReceiverMeasurements[i] != null) {
                numberOfUsefulSatellites++
            }
        }
        return numberOfUsefulSatellites
    }

    /**
     * Class containing satellites' PRNs, satellites' positions in ECEF meters, the pseudorange
     * residual per visible satellite in meters and the covariance matrix of the
     * pseudoranges in meters square
     */
    class SatellitesPositionPseudorangesResidualAndCovarianceMatrix
    /** Constructor  */(
            /** Satellites' PRNs  */
            val satellitePRNs: IntArray,
            /** ECEF positions (meters) of useful satellites  */
            val satellitesPositionsMeters: Array<DoubleArray?>,
            /** Pseudorange measurement residuals (difference of measured to predicted pseudoranges)  */
            val pseudorangeResidualsMeters: DoubleArray,
            /** Pseudorange covariance Matrix for the weighted least squares (meters square)  */
            val covarianceMatrixMetersSquare: Array<DoubleArray>)

    /**
     * Class containing GPS time of week in seconds and GPS week number
     */
    private class GpsTimeOfWeekAndWeekNumber
    /** Constructor  */(
            /** GPS time of week in seconds  */
            val gpsTimeOfWeekSeconds: Double,
            /** GPS week number  */
            val weekNumber: Int)

    companion object {
        private const val SPEED_OF_LIGHT_MPS = 299792458.0
        private const val SECONDS_IN_WEEK = 604800
        private const val LEAST_SQUARE_TOLERANCE_METERS = 4.0e-8

        /** Position correction threshold below which atmospheric correction will be applied  */
        private const val ATMOSPHERIC_CORRECTIONS_THRESHOLD_METERS = 1000.0
        private const val MINIMUM_NUMBER_OF_SATELLITES = 4
        private const val RESIDUAL_TO_REPEAT_LEAST_SQUARE_METERS = 20.0
        private const val MAXIMUM_NUMBER_OF_LEAST_SQUARE_ITERATIONS = 100

        /** GPS C/A code chip width Tc = 1 microseconds  */
        private const val GPS_CHIP_WIDTH_T_C_SEC = 1.0e-6

        /** Narrow correlator with spacing d = 0.1 chip  */
        private const val GPS_CORRELATOR_SPACING_IN_CHIPS = 0.1

        /** Average time of DLL correlator T of 20 milliseconds  */
        private const val GPS_DLL_AVERAGING_TIME_SEC = 20.0e-3

        /** Average signal travel time from GPS satellite and earth  */
        private const val AVERAGE_TRAVEL_TIME_SECONDS = 70.0e-3
        private const val SECONDS_PER_NANO = 1.0e-9
        private const val DOUBLE_ROUND_OFF_TOLERANCE = 0.0000000001

        /**
         * Computes the GPS time of week at the time of transmission and as well the corrected GPS week
         * taking into consideration week rollover. The returned GPS time of week is corrected by the
         * computed satellite clock drift. The result is stored in an instance of
         * [GpsTimeOfWeekAndWeekNumber]
         *
         * @param ephemerisProto parameters of the navigation message
         * @param receiverGpsTowAtReceptionSeconds Receiver estimate of GPS time of week when signal was
         * received (seconds)
         * @param receiverGpsWeek Receiver estimate of GPS week (0-1024+)
         * @param pseudorangeMeters Measured pseudorange in meters
         * @return GpsTimeOfWeekAndWeekNumber Object containing Gps time of week and week number.
         */
        @Throws(Exception::class)
        private fun calculateCorrectedTransmitTowAndWeek(
                ephemerisProto: GpsEphemerisProto?, receiverGpsTowAtReceptionSeconds: Double,
                receiverGpsWeek: Int, pseudorangeMeters: Double): GpsTimeOfWeekAndWeekNumber {
            // GPS time of week at time of transmission: Gps time corrected for transit time (page 98 ICD
            // GPS 200)
            var receiverGpsWeek = receiverGpsWeek
            var receiverGpsTowAtTimeOfTransmission = receiverGpsTowAtReceptionSeconds - pseudorangeMeters / SPEED_OF_LIGHT_MPS

            // Adjust for week rollover
            if (receiverGpsTowAtTimeOfTransmission < 0) {
                receiverGpsTowAtTimeOfTransmission += SECONDS_IN_WEEK.toDouble()
                receiverGpsWeek -= 1
            } else if (receiverGpsTowAtTimeOfTransmission > SECONDS_IN_WEEK) {
                receiverGpsTowAtTimeOfTransmission -= SECONDS_IN_WEEK.toDouble()
                receiverGpsWeek += 1
            }

            // Compute the satellite clock correction term (Seconds)
            val clockCorrectionSeconds = SatelliteClockCorrectionCalculator.calculateSatClockCorrAndEccAnomAndTkIteratively(
                    ephemerisProto, receiverGpsTowAtTimeOfTransmission,
                    receiverGpsWeek.toDouble()).satelliteClockCorrectionMeters / SPEED_OF_LIGHT_MPS

            // Correct with the satellite clock correction term
            var receiverGpsTowAtTimeOfTransmissionCorrectedSec = receiverGpsTowAtTimeOfTransmission + clockCorrectionSeconds

            // Adjust for week rollover due to satellite clock correction
            if (receiverGpsTowAtTimeOfTransmissionCorrectedSec < 0.0) {
                receiverGpsTowAtTimeOfTransmissionCorrectedSec += SECONDS_IN_WEEK.toDouble()
                receiverGpsWeek -= 1
            }
            if (receiverGpsTowAtTimeOfTransmissionCorrectedSec > SECONDS_IN_WEEK) {
                receiverGpsTowAtTimeOfTransmissionCorrectedSec -= SECONDS_IN_WEEK.toDouble()
                receiverGpsWeek += 1
            }
            return GpsTimeOfWeekAndWeekNumber(receiverGpsTowAtTimeOfTransmissionCorrectedSec,
                    receiverGpsWeek)
        }

        /**
         * Calculates the Geometry matrix (describing user to satellite geometry) given a list of
         * satellite positions in ECEF coordinates in meters and the user position in ECEF in meters.
         *
         *
         * The geometry matrix has four columns, and rows equal to the number of satellites. For each
         * of the rows (i.e. for each of the satellites used), the columns are filled with the normalized
         * line–of-sight vectors and 1 s for the fourth column.
         *
         *
         * Source: Parkinson, B.W., Spilker Jr., J.J.: ‘Global positioning system: theory and
         * applications’ page 413
         */
        private fun calculateGeometryMatrix(satellitePositionsECEFMeters: Array<DoubleArray?>,
                                            userPositionECEFMeters: DoubleArray?): Array<DoubleArray> {
            val geometryMatrix = Array(satellitePositionsECEFMeters.size) { DoubleArray(4) }
            for (i in satellitePositionsECEFMeters.indices) {
                geometryMatrix[i][3] = 1.00
            }
            // iterate over all satellites
            for (i in satellitePositionsECEFMeters.indices) {
                val r = doubleArrayOf(satellitePositionsECEFMeters[i]!![0] - userPositionECEFMeters!![0],
                        satellitePositionsECEFMeters[i]!![1] - userPositionECEFMeters[1],
                        satellitePositionsECEFMeters[i]!![2] - userPositionECEFMeters[2])
                val norm = Math.sqrt(Math.pow(r[0], 2.0) + Math.pow(r[1], 2.0) + Math.pow(r[2], 2.0))
                for (j in 0..2) {
                    geometryMatrix[i][j] = (userPositionECEFMeters[j] - satellitePositionsECEFMeters[i]!![j]) / norm
                }
            }
            return geometryMatrix
        }

        /**
         * Uses the common reception time approach to calculate pseudoranges from the time of week
         * measurements reported by the receiver according to http://cdn.intechopen.com/pdfs-wm/27712.pdf.
         * As well computes the pseudoranges uncertainties for each input satellite
         */
        @VisibleForTesting
        fun computePseudorangeAndUncertainties(
                usefulSatellitesToReceiverMeasurements: List<GpsMeasurement>,
                usefulSatellitesToTOWNs: Array<Long?>,
                largestTowNs: Long): List<GpsMeasurementWithRangeAndUncertainty> {
            val usefulSatellitesToPseudorangeMeasurements: MutableList<GpsMeasurementWithRangeAndUncertainty> = Arrays.asList(
                    *arrayOfNulls(GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES))
            for (i in 0 until GpsNavigationMessageStore.Companion.MAX_NUMBER_OF_SATELLITES) {
                if (usefulSatellitesToTOWNs[i] != null) {
                    val deltai = (largestTowNs - usefulSatellitesToTOWNs[i]!!).toDouble()
                    val pseudorangeMeters = (AVERAGE_TRAVEL_TIME_SECONDS + deltai * SECONDS_PER_NANO) * SPEED_OF_LIGHT_MPS
                    val signalToNoiseRatioLinear = Math.pow(10.0, usefulSatellitesToReceiverMeasurements[i].signalToNoiseRatioDb / 10.0)
                    // From Global Positioning System book, Misra and Enge, page 416, the uncertainty of the
                    // pseudorange measurement is calculated next.
                    // For GPS C/A code chip width Tc = 1 microseconds. Narrow correlator with spacing d = 0.1
                    // chip and an average time of DLL correlator T of 20 milliseconds are used.
                    val sigmaMeters = (SPEED_OF_LIGHT_MPS
                            * GPS_CHIP_WIDTH_T_C_SEC
                            * Math.sqrt(GPS_CORRELATOR_SPACING_IN_CHIPS
                            / (4 * GPS_DLL_AVERAGING_TIME_SEC * signalToNoiseRatioLinear)))
                    usefulSatellitesToPseudorangeMeasurements[i] = GpsMeasurementWithRangeAndUncertainty(
                            usefulSatellitesToReceiverMeasurements[i], pseudorangeMeters, sigmaMeters)
                }
            }
            return usefulSatellitesToPseudorangeMeasurements
        }
    }
}