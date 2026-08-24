package com.threecolumn.cbt.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.threecolumn.cbt.data.CognitiveDistortion

/**
 * The distortion's label prefixed with its fixed display number (1-10), e.g. "5. Jumping to
 * Conclusions — Mind Reading" — mirroring how "Feeling Good" itself presents the distortions as
 * a numbered list. Mind Reading and Fortune Telling share #5.
 */
@Composable
fun CognitiveDistortion.numberedLabel(): String = "${number}. ${stringResource(labelRes)}"
