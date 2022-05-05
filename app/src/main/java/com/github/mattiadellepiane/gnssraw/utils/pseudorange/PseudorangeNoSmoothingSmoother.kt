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

import java.util.Collections


/**
 * An implementation of [PseudorangeSmoother] that performs no smoothing.
 *
 *
 *  A new list of [GpsMeasurementWithRangeAndUncertainty] instances is filled with a copy
 * of the input list.
 */
internal class PseudorangeNoSmoothingSmoother : PseudorangeSmoother {
    override fun updatePseudorangeSmoothingResult(
            usefulSatellitesToGPSReceiverMeasurements: List<GpsMeasurementWithRangeAndUncertainty?>?): List<GpsMeasurementWithRangeAndUncertainty?> {
        return Collections.unmodifiableList(usefulSatellitesToGPSReceiverMeasurements)
    }
}