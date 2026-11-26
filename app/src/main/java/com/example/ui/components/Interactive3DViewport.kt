package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.ui.Lab3DState
import com.example.ui.theme.AxisXRed
import com.example.ui.theme.AxisYGreen
import com.example.ui.theme.AxisZBlue
import kotlin.math.*

private data class Vec3(val x: Float, val y: Float, val z: Float) {
    operator fun plus(o: Vec3) = Vec3(x + o.x, y + o.y, z + o.z)
    operator fun minus(o: Vec3) = Vec3(x - o.x, y - o.y, z - o.z)
    operator fun times(s: Float) = Vec3(x * s, y * s, z * s)
    fun dot(o: Vec3) = x * o.x + y * o.y + z * o.z
    fun cross(o: Vec3) = Vec3(
        y * o.z - z * o.y,
        z * o.x - x * o.z,
        x * o.y - y * o.x
    )
    fun normalized(): Vec3 {
        val len = sqrt(x * x + y * y + z * z).coerceAtLeast(1e-5f)
        return Vec3(x / len, y / len, z / len)
    }
}

private data class Mesh3D(
    val vertices: List<Vec3>,
    val faces: List<List<Int>> // CCW indices
)

private val BaseColors = listOf(
    Color(0xFF22C55E), // Emerald
    Color(0xFF0EA5E9), // Sky
    Color(0xFFF59E0B), // Gold
    Color(0xFFF43F5E), // Coral
    Color(0xFFA855F7)  // Violet
)

@Composable
fun Interactive3DViewport(
    state: Lab3DState,
    onRotateDrag: (deltaYaw: Float, deltaPitch: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val bgTop = Color(0xFF0F1B16)
    val bgBottom = Color(0xFF1A2D24)
    val meshColor = BaseColors[state.baseColorIndex.coerceIn(BaseColors.indices)]

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.verticalGradient(listOf(bgTop, bgBottom)))
            .border(1.5.dp, Color(0xFF2B4639), RoundedCornerShape(22.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onRotateDrag(dragAmount.x * 0.55f, dragAmount.y * 0.45f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f + 16f
            val scaleBase = min(w, h) * 0.34f * state.zoom * state.fovScale

            val yawRad = state.yawDeg * (PI.toFloat() / 180f)
            val pitchRad = state.pitchDeg * (PI.toFloat() / 180f)

            fun rotateVec(v: Vec3): Vec3 {
                // Yaw around Y
                val cosY = cos(yawRad)
                val sinY = sin(yawRad)
                val x1 = v.x * cosY - v.z * sinY
                val z1 = v.x * sinY + v.z * cosY
                // Pitch around X
                val cosP = cos(pitchRad)
                val sinP = sin(pitchRad)
                val y2 = v.y * cosP - z1 * sinP
                val z2 = v.y * sinP + z1 * cosP
                return Vec3(x1, y2, z2)
            }

            fun project(v: Vec3): Pair<Offset, Float> {
                val r = rotateVec(v)
                val camDist = 4.6f / state.fovScale.coerceIn(0.65f, 1.55f)
                val denom = (camDist - r.z).coerceAtLeast(1.1f)
                val persp = camDist / denom
                val sx = cx + r.x * scaleBase * persp
                val sy = cy - r.y * scaleBase * persp
                return Offset(sx, sy) to r.z
            }

            // 1. Floor Reference Grid (Y = -1.0)
            val gridColor = Color(0xFF2B493B)
            for (i in -3..3) {
                val f = i * 0.55f
                val (p1, _) = project(Vec3(f, -1.0f, -1.65f))
                val (p2, _) = project(Vec3(f, -1.0f, 1.65f))
                drawLine(gridColor, p1, p2, strokeWidth = if (i == 0) 2.2f else 1.2f)

                val (p3, _) = project(Vec3(-1.65f, -1.0f, f))
                val (p4, _) = project(Vec3(1.65f, -1.0f, f))
                drawLine(gridColor, p3, p4, strokeWidth = if (i == 0) 2.2f else 1.2f)
            }

            // 2. X/Y/Z Coordinate Axes Gizmo
            val axisLen = if (state.renderMode == "Axes") 1.75f else 1.25f
            val origin = project(Vec3(0f, 0f, 0f)).first
            val xEnd = project(Vec3(axisLen, 0f, 0f)).first
            val yEnd = project(Vec3(0f, axisLen, 0f)).first
            val zEnd = project(Vec3(0f, 0f, axisLen)).first

            drawLine(AxisXRed, origin, xEnd, strokeWidth = 4f, cap = StrokeCap.Round)
            drawLine(AxisYGreen, origin, yEnd, strokeWidth = 4f, cap = StrokeCap.Round)
            drawLine(AxisZBlue, origin, zEnd, strokeWidth = 4f, cap = StrokeCap.Round)
            drawCircle(AxisXRed, radius = 6f, center = xEnd)
            drawCircle(AxisYGreen, radius = 6f, center = yEnd)
            drawCircle(AxisZBlue, radius = 6f, center = zEnd)

            // 3. Keyframe Animation Offset & Squash/Stretch
            val t = (state.currentFrame / state.totalFrames.coerceAtLeast(1)).coerceIn(0f, 1f)
            val wave = sin(t * 2f * PI.toFloat())
            val bounceHeight = when (state.easingMode) {
                "Linear" -> abs(1f - 2f * t)
                "Ease In-Out" -> 0.5f - 0.5f * cos(t * 2f * PI.toFloat())
                else -> abs(wave) // Bounce arc
            }
            val liftY = -0.35f + bounceHeight * 0.95f
            val squashFactor = if (state.squashStretchEnabled && bounceHeight < 0.14f) {
                1f - (0.14f - bounceHeight) * 2.1f
            } else if (state.squashStretchEnabled && bounceHeight > 0.7f) {
                1.08f
            } else {
                1.0f
            }
            val stretchXZ = 1f / sqrt(squashFactor.coerceIn(0.65f, 1.3f))

            // 4. Build Selected Mesh
            val rawMesh = buildPrimitiveMesh(state.primitive)
            val animVerts = rawMesh.vertices.map { v ->
                Vec3(
                    x = v.x * stretchXZ,
                    y = v.y * squashFactor + liftY,
                    z = v.z * stretchXZ
                )
            }

            // 5. Light Vectors (World -> Rotated View Space)
            val keyRad = state.keyAngleDeg * (PI.toFloat() / 180f)
            val keyDir = Vec3(cos(keyRad), 0.75f, sin(keyRad)).normalized()
            val fillDir = Vec3(-cos(keyRad), 0.25f, -sin(keyRad) * 0.5f).normalized()
            val rimDir = Vec3(0f, 0.55f, -1f).normalized()
            val viewDir = Vec3(0f, 0f, 1f)

            data class ProjectedFace(
                val points: List<Offset>,
                val avgZ: Float,
                val fillColor: Color
            )

            val projectedFaces = rawMesh.faces.mapNotNull { face ->
                val worldPts = face.map { animVerts[it] }
                val rotPts = worldPts.map { rotateVec(it) }
                val v0 = rotPts[0]
                val v1 = rotPts[1]
                val v2 = rotPts[2]
                val normal = (v1 - v0).cross(v2 - v0).normalized()

                // Backface cull in Shaded mode so depth sorting stays crisp
                if (state.renderMode == "Shaded" && normal.z < -0.08f) {
                    return@mapNotNull null
                }

                // Compute PBR-inspired shading
                val ambient = 0.14f
                val kRot = rotateVec(keyDir).normalized()
                val fRot = rotateVec(fillDir).normalized()
                val rRot = rotateVec(rimDir).normalized()

                val keyDiff = if (state.keyLightEnabled) max(0f, normal.dot(kRot)) * 0.85f else 0f
                val fillDiff = if (state.fillLightEnabled) max(0f, normal.dot(fRot)) * 0.34f else 0f
                val rimDot = if (state.rimLightEnabled) {
                    val fresnel = (1f - abs(normal.dot(viewDir))).pow(2.0f)
                    fresnel * max(0f, normal.dot(rRot) + 0.35f) * 0.72f
                } else 0f

                val specPower = ((1f - state.roughness) * 42f + 4f)
                val halfVec = (kRot + viewDir).normalized()
                val spec = if (state.keyLightEnabled) {
                    max(0f, normal.dot(halfVec)).pow(specPower) * (0.25f + state.metallic * 0.75f) * (1.05f - state.roughness * 0.7f)
                } else 0f

                val diffuseMult = (ambient + keyDiff + fillDiff).coerceIn(0.1f, 1.25f)
                val metallicTint = 1f - state.metallic * 0.25f
                val r = (meshColor.red * diffuseMult * metallicTint + spec + rimDot * 0.65f).coerceIn(0f, 1f)
                val g = (meshColor.green * diffuseMult * metallicTint + spec + rimDot * 0.9f).coerceIn(0f, 1f)
                val b = (meshColor.blue * diffuseMult * metallicTint + spec + rimDot).coerceIn(0f, 1f)

                val screenPts = worldPts.map { project(it).first }
                val avgZ = rotPts.map { it.z }.average().toFloat()
                ProjectedFace(screenPts, avgZ, Color(r, g, b, 1f))
            }.sortedBy { it.avgZ }

            // 6. Draw Faces
            projectedFaces.forEach { pf ->
                val path = Path().apply {
                    pf.points.forEachIndexed { idx, pt ->
                        if (idx == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
                    }
                    close()
                }
                if (state.renderMode == "Shaded") {
                    drawPath(path, color = pf.fillColor)
                    drawPath(path, color = Color.Black.copy(alpha = 0.16f), style = Stroke(width = 1.4f))
                } else {
                    drawPath(
                        path,
                        color = meshColor.copy(alpha = 0.14f)
                    )
                    drawPath(
                        path,
                        color = Color(0xFF4ADE80),
                        style = Stroke(width = 2.2f)
                    )
                }
            }

            // 7. Vertex Dots in Wireframe / Axes mode
            if (state.renderMode != "Shaded") {
                animVerts.forEach { v ->
                    val (pt, _) = project(v)
                    drawCircle(Color(0xFFFBBF24), radius = 4.5f, center = pt)
                }
            }

            // 8. Rule of Thirds Camera Composition Grid
            if (state.showRuleOfThirds) {
                val guideColor = Color.White.copy(alpha = 0.42f)
                drawLine(guideColor, Offset(w / 3f, 0f), Offset(w / 3f, h), strokeWidth = 1.5f)
                drawLine(guideColor, Offset(w * 2f / 3f, 0f), Offset(w * 2f / 3f, h), strokeWidth = 1.5f)
                drawLine(guideColor, Offset(0f, h / 3f), Offset(w, h / 3f), strokeWidth = 1.5f)
                drawLine(guideColor, Offset(0f, h * 2f / 3f), Offset(w, h * 2f / 3f), strokeWidth = 1.5f)
            }
        }

        // Compact Axis Legend + Hud Overlay
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black.copy(alpha = 0.48f))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("X", style = MaterialTheme.typography.labelSmall, color = AxisXRed)
            Text("Y", style = MaterialTheme.typography.labelSmall, color = AxisYGreen)
            Text("Z", style = MaterialTheme.typography.labelSmall, color = AxisZBlue)
            Text(
                "• ${state.primitive} • Drag to orbit",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.85f)
            )
        }

        if (state.isPlayingAnim || state.currentFrame > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Frame ${state.currentFrame.roundToInt()} / ${state.totalFrames} (${state.fps}fps)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFDE68A)
                )
            }
        }
    }
}

private fun buildPrimitiveMesh(name: String): Mesh3D {
    return when (name) {
        "Pyramid" -> {
            val v = listOf(
                Vec3(0f, 0.72f, 0f),
                Vec3(-0.65f, -0.55f, 0.65f),
                Vec3(0.65f, -0.55f, 0.65f),
                Vec3(0.65f, -0.55f, -0.65f),
                Vec3(-0.65f, -0.55f, -0.65f)
            )
            val f = listOf(
                listOf(0, 1, 2),
                listOf(0, 2, 3),
                listOf(0, 3, 4),
                listOf(0, 4, 1),
                listOf(1, 4, 3, 2)
            )
            Mesh3D(v, f)
        }
        "Octahedron" -> {
            val v = listOf(
                Vec3(0f, 0.78f, 0f),
                Vec3(0.68f, 0f, 0f),
                Vec3(0f, 0f, 0.68f),
                Vec3(-0.68f, 0f, 0f),
                Vec3(0f, 0f, -0.68f),
                Vec3(0f, -0.78f, 0f)
            )
            val f = listOf(
                listOf(0, 2, 1), listOf(0, 3, 2), listOf(0, 4, 3), listOf(0, 1, 4),
                listOf(5, 1, 2), listOf(5, 2, 3), listOf(5, 3, 4), listOf(5, 4, 1)
            )
            Mesh3D(v, f)
        }
        "Cylinder" -> {
            val sides = 10
            val r = 0.55f
            val topY = 0.62f
            val botY = -0.62f
            val topVerts = (0 until sides).map { i ->
                val a = (i.toFloat() / sides) * 2f * PI.toFloat()
                Vec3(cos(a) * r, topY, sin(a) * r)
            }
            val botVerts = (0 until sides).map { i ->
                val a = (i.toFloat() / sides) * 2f * PI.toFloat()
                Vec3(cos(a) * r, botY, sin(a) * r)
            }
            val verts = topVerts + botVerts
            val sideFaces = (0 until sides).map { i ->
                val next = (i + 1) % sides
                listOf(next, i, sides + i, sides + next)
            }
            val topCap = (0 until sides).reversed().toList()
            val botCap = (0 until sides).map { sides + it }
            Mesh3D(verts, sideFaces + listOf(topCap, botCap))
        }
        "Rocket" -> {
            val sides = 8
            val r = 0.38f
            val noseTip = Vec3(0f, 0.92f, 0f) // index 0
            val ringTop = (0 until sides).map { i ->
                val a = (i.toFloat() / sides) * 2f * PI.toFloat()
                Vec3(cos(a) * r, 0.32f, sin(a) * r)
            } // 1..8
            val ringBot = (0 until sides).map { i ->
                val a = (i.toFloat() / sides) * 2f * PI.toFloat()
                Vec3(cos(a) * r, -0.52f, sin(a) * r)
            } // 9..16
            // 3 fins
            val finVerts = listOf(
                Vec3(0.36f, -0.15f, 0f), Vec3(0.75f, -0.62f, 0f), Vec3(0.36f, -0.52f, 0f), // 17,18,19
                Vec3(-0.36f, -0.15f, 0f), Vec3(-0.75f, -0.62f, 0f), Vec3(-0.36f, -0.52f, 0f) // 20,21,22
            )
            val verts = listOf(noseTip) + ringTop + ringBot + finVerts
            val noseFaces = (0 until sides).map { i ->
                val next = (i + 1) % sides
                listOf(0, 1 + next, 1 + i)
            }
            val bodyFaces = (0 until sides).map { i ->
                val next = (i + 1) % sides
                listOf(1 + next, 1 + i, 9 + i, 9 + next)
            }
            val bottomCap = (0 until sides).map { 9 + it }
            val fins = listOf(
                listOf(17, 18, 19), listOf(17, 19, 18),
                listOf(20, 22, 21), listOf(20, 21, 22)
            )
            Mesh3D(verts, noseFaces + bodyFaces + listOf(bottomCap) + fins)
        }
        else -> { // Cube
            val s = 0.56f
            val v = listOf(
                Vec3(-s, -s, s), Vec3(s, -s, s), Vec3(s, s, s), Vec3(-s, s, s),
                Vec3(-s, -s, -s), Vec3(s, -s, -s), Vec3(s, s, -s), Vec3(-s, s, -s)
            )
            val f = listOf(
                listOf(0, 1, 2, 3), // front
                listOf(5, 4, 7, 6), // back
                listOf(4, 0, 3, 7), // left
                listOf(1, 5, 6, 2), // right
                listOf(3, 2, 6, 7), // top
                listOf(4, 5, 1, 0)  // bottom
            )
            Mesh3D(v, f)
        }
    }
}
