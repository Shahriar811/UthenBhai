package com.mdshahriar.uthenbhai.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import com.mdshahriar.uthenbhai.model.VehicleType

/**
 * Premium, high-definition stylized vector illustrations for vehicles (Bike, CNG, Car).
 * Crafted with sleek aerodynamic profiles, multi-layered wheel rims, specular glass highlights,
 * and soft ambient drop shadows inspired by modern mobility designs (Pathao & Uber).
 */
@Composable
fun VehicleSticker(vehicle: VehicleType, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val baseW = 120f
        val baseH = 80f
        val scaleFactor = minOf(size.width / baseW, size.height / baseH)

        withTransform({
            translate(
                left = (size.width - baseW * scaleFactor) / 2f,
                top = (size.height - baseH * scaleFactor) / 2f
            )
            scale(scaleFactor, scaleFactor, Offset.Zero)
        }) {
            // Ambient ground contact shadow with dual-layer soft blur
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x38000000), Color(0x12000000), Color.Transparent),
                    center = Offset(60f, 66f),
                    radius = 48f
                ),
                topLeft = Offset(10f, 62f),
                size = Size(100f, 9f)
            )

            when (vehicle) {
                VehicleType.BIKE -> drawClassyBike()
                VehicleType.CNG -> drawClassyCNG()
                VehicleType.CAR -> drawClassyCar()
            }
        }
    }
}

/**
 * Modern commuter sports bike illustration.
 */
private fun DrawScope.drawClassyBike() {
    val tireColor = Color(0xFF1E232A)
    val rimOuter = Color(0xFF475569)
    val rimInner = Color(0xFFE2E8F0)
    val bodyPrimary = Color(0xFFFF6D00) // Vibrant Saffron / Orange
    val bodyAccent = Color(0xFFFF9100)
    val frameDark = Color(0xFF0F172A)
    val chrome = Color(0xFFCBD5E1)

    // Rear Wheel
    drawCircle(tireColor, 13f, Offset(26f, 57f))
    drawCircle(rimOuter, 9.5f, Offset(26f, 57f))
    drawCircle(rimInner, 6f, Offset(26f, 57f))
    drawCircle(Color(0xFFE11D48), 2.5f, Offset(23f, 55f)) // Brake Caliper
    drawCircle(frameDark, 2.5f, Offset(26f, 57f)) // Axle

    // Front Wheel
    drawCircle(tireColor, 13f, Offset(94f, 57f))
    drawCircle(rimOuter, 9.5f, Offset(94f, 57f))
    drawCircle(rimInner, 6f, Offset(94f, 57f))
    drawCircle(Color(0xFFE11D48), 2.5f, Offset(91f, 55f)) // Brake Caliper
    drawCircle(frameDark, 2.5f, Offset(94f, 57f)) // Axle

    // Exhaust muffler
    val exhaust = Path().apply {
        moveTo(36f, 56f)
        lineTo(18f, 51f)
        lineTo(19f, 47f)
        lineTo(40f, 52f)
        close()
    }
    drawPath(exhaust, Brush.linearGradient(listOf(Color(0xFF475569), Color(0xFF94A3B8))))
    drawRoundRect(chrome, Offset(16f, 48f), Size(8f, 4f), CornerRadius(2f))

    // Trellis / Swingarm Frame
    val swingarm = Path().apply {
        moveTo(26f, 57f)
        lineTo(48f, 48f)
        lineTo(58f, 54f)
        lineTo(48f, 38f)
        close()
    }
    drawPath(swingarm, frameDark, style = Stroke(3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))

    // Engine block core
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B))),
        topLeft = Offset(46f, 43f),
        size = Size(20f, 15f),
        cornerRadius = CornerRadius(4f)
    )

    // Front telescopic fork suspension
    drawLine(
        brush = Brush.linearGradient(listOf(Color(0xFFE2E8F0), Color(0xFF64748B))),
        start = Offset(94f, 57f),
        end = Offset(81f, 26f),
        strokeWidth = 3.5f,
        cap = StrokeCap.Round
    )

    // Contoured Sport Seat
    val seat = Path().apply {
        moveTo(35f, 34f)
        cubicTo(42f, 35f, 50f, 34f, 57f, 32f)
        lineTo(62f, 35f)
        lineTo(43f, 41f)
        close()
    }
    drawPath(seat, Color(0xFF0F172A))
    // Seat stitching highlight
    drawLine(Color(0xFF475569), Offset(37f, 35f), Offset(55f, 33f), 1.5f, cap = StrokeCap.Round)

    // Aerodynamic Fuel Tank & Cowling
    val fuelTank = Path().apply {
        moveTo(54f, 33f)
        cubicTo(58f, 24f, 70f, 23f, 79f, 27f)
        lineTo(82f, 35f)
        lineTo(66f, 42f)
        close()
    }
    drawPath(
        fuelTank,
        brush = Brush.horizontalGradient(listOf(bodyPrimary, bodyAccent))
    )
    // Tank highlight line
    drawLine(Color.White.copy(alpha = 0.55f), Offset(60f, 26f), Offset(76f, 28f), 1.5f, cap = StrokeCap.Round)

    // Handlebar & Mirrors
    drawLine(frameDark, Offset(80f, 27f), Offset(77f, 20f), 2.5f, cap = StrokeCap.Round)
    drawLine(frameDark, Offset(77f, 20f), Offset(86f, 19f), 3f, cap = StrokeCap.Round)
    drawCircle(Color(0xFF0F172A), 2f, Offset(75f, 17f)) // Mirror

    // Front Headlamp & Fairing
    val headlightFairing = Path().apply {
        moveTo(82f, 27f)
        lineTo(92f, 30f)
        lineTo(87f, 39f)
        close()
    }
    drawPath(headlightFairing, bodyPrimary)
    // LED Headlight projector
    drawOval(
        brush = Brush.horizontalGradient(listOf(Color(0xFFFEF08A), Color.White)),
        topLeft = Offset(90f, 30f),
        size = Size(5f, 7f)
    )
    // Headlight glow beam
    drawOval(
        color = Color(0x30FACC15),
        topLeft = Offset(93f, 29f),
        size = Size(10f, 10f)
    )
}

/**
 * Iconic Bangladeshi CNG auto-rickshaw in classy modern style.
 */
private fun DrawScope.drawClassyCNG() {
    val tireColor = Color(0xFF1E232A)
    val cngGreen = Color(0xFF15803D) // Emerald Green
    val cngGreenDark = Color(0xFF14532D)
    val cngRoof = Color(0xFF0F172A)
    val glassColor = Color(0xFFBAE6FD)
    val glassGleam = Color.White.copy(alpha = 0.6f)
    val chrome = Color(0xFFE2E8F0)

    // Rear Wheel
    drawCircle(tireColor, 11f, Offset(29f, 58f))
    drawCircle(Color(0xFF64748B), 7.5f, Offset(29f, 58f))
    drawCircle(chrome, 4.5f, Offset(29f, 58f))
    drawCircle(tireColor, 2f, Offset(29f, 58f))

    // Front Wheel & Mudguard
    drawCircle(tireColor, 10f, Offset(93f, 59f))
    drawCircle(Color(0xFF64748B), 6.5f, Offset(93f, 59f))
    drawCircle(chrome, 4f, Offset(93f, 59f))
    drawArc(
        color = cngRoof,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(81f, 47f),
        size = Size(24f, 15f),
        style = Stroke(3f)
    )

    // Main Cabin Body
    val body = Path().apply {
        moveTo(18f, 44f)
        lineTo(18f, 56f)
        lineTo(86f, 56f)
        lineTo(102f, 50f)
        lineTo(104f, 42f)
        lineTo(87f, 40f)
        lineTo(84f, 30f)
        lineTo(70f, 16f)
        lineTo(26f, 16f)
        cubicTo(20f, 20f, 18f, 32f, 18f, 44f)
        close()
    }
    drawPath(
        body,
        brush = Brush.verticalGradient(
            colors = listOf(cngGreen, cngGreenDark),
            startY = 16f,
            endY = 56f
        )
    )

    // Curved Aerodynamic Roof Canopy
    val roof = Path().apply {
        moveTo(16f, 25f)
        cubicTo(20f, 13f, 32f, 12f, 68f, 12f)
        lineTo(74f, 16f)
        lineTo(18f, 25f)
        close()
    }
    drawPath(roof, cngRoof)
    drawRoundRect(cngRoof, Offset(22f, 11f), Size(52f, 5f), CornerRadius(3f))

    // Windshield (Front Glass)
    val windshield = Path().apply {
        moveTo(73f, 18f)
        lineTo(85f, 31f)
        lineTo(85f, 40f)
        lineTo(71f, 40f)
        close()
    }
    drawPath(windshield, glassColor)
    // Windshield reflection slash
    drawLine(glassGleam, Offset(76f, 20f), Offset(73f, 38f), 2.5f, cap = StrokeCap.Round)

    // Passenger Side Open Mesh Grille Area
    val windowOpening = Path().apply {
        moveTo(24f, 24f)
        lineTo(66f, 24f)
        lineTo(66f, 40f)
        lineTo(24f, 40f)
        close()
    }
    drawPath(windowOpening, Color(0x33000000))
    // Safety protective grille bars
    for (x in listOf(33f, 43f, 53f, 63f)) {
        drawLine(Color(0xFFCBD5E1), Offset(x, 24f), Offset(x, 40f), 1.5f)
    }
    drawLine(Color(0xFFCBD5E1), Offset(24f, 32f), Offset(66f, 32f), 1.5f)

    // Lower Protection Bumper Bar
    drawLine(chrome, Offset(16f, 53f), Offset(105f, 53f), 2.5f, cap = StrokeCap.Round)

    // Front Headlamp & Amber Signal
    drawRoundRect(
        brush = Brush.horizontalGradient(listOf(Color(0xFFFDE047), Color.White)),
        topLeft = Offset(101f, 43f),
        size = Size(6f, 5f),
        cornerRadius = CornerRadius(2f)
    )
    drawCircle(Color(0x35FACC15), 5f, Offset(105f, 45f)) // Headlamp flare
}

/**
 * Modern executive sedan illustration with sleek aerodynamic lines.
 */
private fun DrawScope.drawClassyCar() {
    val tireColor = Color(0xFF1E232A)
    val carBodyPrimary = Color(0xFF334155) // Slate Charcoal / Midnight
    val carBodyHighlight = Color(0xFF475569)
    val glassColor = Color(0xFF93C5FD)
    val glassGleam = Color.White.copy(alpha = 0.65f)
    val rimColor = Color(0xFFE2E8F0)
    val chrome = Color(0xFFCBD5E1)

    // Rear Wheel
    drawCircle(tireColor, 11f, Offset(29f, 58f))
    drawCircle(Color(0xFF475569), 8f, Offset(29f, 58f))
    drawCircle(rimColor, 5.5f, Offset(29f, 58f))
    drawCircle(Color(0xFFE11D48), 2.5f, Offset(27f, 56f)) // Sport Brake Caliper
    drawCircle(tireColor, 2f, Offset(29f, 58f))

    // Front Wheel
    drawCircle(tireColor, 11f, Offset(91f, 58f))
    drawCircle(Color(0xFF475569), 8f, Offset(91f, 58f))
    drawCircle(rimColor, 5.5f, Offset(91f, 58f))
    drawCircle(Color(0xFFE11D48), 2.5f, Offset(89f, 56f)) // Sport Brake Caliper
    drawCircle(tireColor, 2f, Offset(91f, 58f))

    // Car Body Aerodynamic Silhouette
    val body = Path().apply {
        moveTo(10f, 52f)
        lineTo(12f, 44f)
        cubicTo(16f, 40f, 26f, 38f, 32f, 38f) // Trunk curve
        cubicTo(40f, 25f, 50f, 21f, 74f, 21f) // Sleek Coupe Roofline
        cubicTo(82f, 21f, 92f, 34f, 98f, 39f) // Hood Slope
        lineTo(108f, 42f)
        cubicTo(111f, 44f, 111f, 49f, 109f, 53f) // Front Bumper
        lineTo(10f, 53f)
        close()
    }
    drawPath(
        body,
        brush = Brush.verticalGradient(
            colors = listOf(carBodyHighlight, carBodyPrimary),
            startY = 21f,
            endY = 54f
        )
    )

    // Side Shoulder Accent Line (Reflective waist crease)
    val shoulderLine = Path().apply {
        moveTo(16f, 42f)
        lineTo(36f, 40f)
        lineTo(96f, 40f)
        lineTo(107f, 44f)
    }
    drawPath(shoulderLine, Color.White.copy(alpha = 0.35f), style = Stroke(1.5f, cap = StrokeCap.Round))

    // Tinted Panoramic Windows (Rear, Front & Pillar)
    val windows = Path().apply {
        moveTo(42f, 25f)
        lineTo(72f, 25f)
        lineTo(86f, 38f)
        lineTo(33f, 38f)
        close()
    }
    drawPath(windows, glassColor)

    // B-Pillar divider
    drawLine(Color(0xFF0F172A), Offset(58f, 25f), Offset(58f, 38f), 2.5f)

    // Windshield Gleam
    drawLine(glassGleam, Offset(74f, 26f), Offset(83f, 36f), 2f, cap = StrokeCap.Round)
    drawLine(glassGleam, Offset(47f, 26f), Offset(42f, 36f), 1.5f, cap = StrokeCap.Round)

    // Side Mirror
    drawRoundRect(
        color = carBodyHighlight,
        topLeft = Offset(77f, 36f),
        size = Size(5f, 3.5f),
        cornerRadius = CornerRadius(1.5f)
    )

    // Flush Door Handles
    drawLine(chrome, Offset(44f, 42f), Offset(49f, 42f), 1.5f, cap = StrokeCap.Round)
    drawLine(chrome, Offset(64f, 42f), Offset(69f, 42f), 1.5f, cap = StrokeCap.Round)

    // Front Jewel-Eye LED Headlamp
    val headlight = Path().apply {
        moveTo(103f, 42f)
        lineTo(108f, 43f)
        lineTo(105f, 47f)
        close()
    }
    drawPath(
        headlight,
        brush = Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color.White))
    )
    drawCircle(Color(0x3538BDF8), 5f, Offset(107f, 44f)) // Light halo

    // Rear LED Tail Lamp
    val taillight = Path().apply {
        moveTo(10f, 45f)
        lineTo(14f, 45f)
        lineTo(12f, 49f)
        lineTo(10f, 49f)
        close()
    }
    drawPath(taillight, Color(0xFFEF4444))
}

/**
 * Compact, stylish icon glyph for vehicles used in buttons, chips, and map markers.
 * Automatically respects the specified tint color or MaterialTheme onSurface.
 */
@Composable
fun VehicleIcon(
    vehicle: VehicleType,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    val activeTint = if (tint != Color.Unspecified) tint else androidx.compose.material3.LocalContentColor.current
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val s = minOf(w / 24f, h / 24f)

        withTransform({
            translate(left = (w - 24f * s) / 2f, top = (h - 24f * s) / 2f)
            scale(s, s, Offset.Zero)
        }) {
            when (vehicle) {
                VehicleType.BIKE -> {
                    // Sleek Bike Glyph
                    drawCircle(activeTint, 3.2f, Offset(5f, 17f), style = Stroke(1.8f))
                    drawCircle(activeTint, 3.2f, Offset(19f, 17f), style = Stroke(1.8f))
                    // Frame
                    val bikePath = Path().apply {
                        moveTo(5f, 17f)
                        lineTo(9.5f, 12f)
                        lineTo(14.5f, 12f)
                        lineTo(19f, 17f)
                    }
                    drawPath(bikePath, activeTint, style = Stroke(1.8f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    // Tank & Seat & Handlebar
                    drawLine(activeTint, Offset(9.5f, 12f), Offset(12f, 16.5f), 1.8f, cap = StrokeCap.Round)
                    drawLine(activeTint, Offset(12f, 16.5f), Offset(14.5f, 12f), 1.8f, cap = StrokeCap.Round)
                    drawLine(activeTint, Offset(14.5f, 12f), Offset(16.5f, 8f), 1.8f, cap = StrokeCap.Round)
                    drawLine(activeTint, Offset(15f, 8f), Offset(18f, 8f), 2.0f, cap = StrokeCap.Round)
                    // Seat
                    drawLine(activeTint, Offset(7.5f, 11f), Offset(10.5f, 11f), 2.2f, cap = StrokeCap.Round)
                }
                VehicleType.CNG -> {
                    // Auto-rickshaw Glyph
                    drawCircle(activeTint, 2.8f, Offset(6f, 18f), style = Stroke(1.6f))
                    drawCircle(activeTint, 2.8f, Offset(18f, 18f), style = Stroke(1.6f))
                    val cngPath = Path().apply {
                        moveTo(4f, 15f)
                        lineTo(4f, 10f)
                        cubicTo(5f, 6.5f, 7f, 6f, 14f, 6f)
                        lineTo(16f, 8f)
                        lineTo(18.5f, 11.5f)
                        lineTo(20f, 14f)
                        lineTo(20f, 17f)
                        lineTo(4f, 17f)
                        close()
                    }
                    drawPath(cngPath, activeTint, style = Stroke(1.6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    // Front glass divider
                    drawLine(activeTint, Offset(14f, 7f), Offset(16f, 12f), 1.4f, cap = StrokeCap.Round)
                    // Open window
                    drawRoundRect(activeTint, Offset(6f, 8.5f), Size(6.5f, 5f), CornerRadius(1f), style = Stroke(1.2f))
                }
                VehicleType.CAR -> {
                    // Sleek Sedan Glyph
                    drawCircle(activeTint, 2.6f, Offset(6f, 17f), style = Stroke(1.6f))
                    drawCircle(activeTint, 2.6f, Offset(18f, 17f), style = Stroke(1.6f))
                    val carPath = Path().apply {
                        moveTo(2.5f, 15f)
                        cubicTo(3.5f, 13f, 5.5f, 12.5f, 7.5f, 12.5f)
                        cubicTo(9.5f, 9f, 12f, 7.5f, 15.5f, 7.5f)
                        lineTo(18f, 11f)
                        lineTo(21f, 13f)
                        cubicTo(22f, 14f, 22f, 16f, 21.5f, 16.5f)
                        lineTo(2.5f, 16.5f)
                        close()
                    }
                    drawPath(carPath, activeTint, style = Stroke(1.6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    // Window cut
                    val carWindows = Path().apply {
                        moveTo(8.5f, 12f)
                        lineTo(10.5f, 9f)
                        lineTo(14.8f, 9f)
                        lineTo(17f, 12f)
                        close()
                    }
                    drawPath(carWindows, activeTint, style = Stroke(1.2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    drawLine(activeTint, Offset(12.5f, 9f), Offset(12.5f, 12f), 1.2f)
                }
            }
        }
    }
}

