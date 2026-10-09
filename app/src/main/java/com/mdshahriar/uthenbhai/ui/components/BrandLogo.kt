package com.mdshahriar.uthenbhai.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mdshahriar.uthenbhai.R

/** Shows the supplied logo without altering its artwork or stretching its proportions. */
@Composable
fun BrandLogo(modifier: Modifier = Modifier) {
    Box(modifier.clip(RoundedCornerShape(14.dp)).background(Color.White),
        contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.brand_logo),
            contentDescription = "Uthen Bhai logo",
            modifier = Modifier.fillMaxSize().graphicsLayer {
                scaleX = 1.35f
                scaleY = 1.35f
            },
            contentScale = ContentScale.Fit
        )
    }
}
