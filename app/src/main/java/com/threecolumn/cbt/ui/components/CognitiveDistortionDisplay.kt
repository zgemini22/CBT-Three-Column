package com.threecolumn.cbt.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.threecolumn.cbt.data.CognitiveDistortion

/**
 * The distortion's label prefixed with its fixed position (1-10) in the list, e.g. "5. Jumping
 * to Conclusions" — mirroring how "Feeling Good" itself presents the distortions as a numbered
 * list.
 */
@Composable
fun CognitiveDistortion.numberedLabel(): String = "${ordinal + 1}. ${stringResource(labelRes)}"
