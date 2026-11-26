package com.example.data.curriculum

import com.example.data.model.*
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.random.Random

object ProceduralExerciseGenerator {

    /**
     * Generates a non-repetitive, validated batch of exercises tailored to department,
     * difficulty tier, and optional exercise mode. Supports thousands of unique combinations.
     */
    fun generateSession(
        departmentId: Int?, // null = Mixed across all 12 departments
        difficulty: DifficultyTier,
        preferredType: ExerciseType? = null,
        count: Int = 6,
        seed: Long = System.currentTimeMillis()
    ): List<ExerciseItem> {
        val rng = Random(seed)
        val result = mutableListOf<ExerciseItem>()
        val usedPrompts = mutableSetOf<String>()

        var attempts = 0
        while (result.size < count && attempts < count * 8) {
            attempts++
            val targetDept = departmentId ?: rng.nextInt(1, 13)
            val chosenType = preferredType ?: pickTypeForDept(targetDept, difficulty, rng)
            val item = buildExercise(targetDept, difficulty, chosenType, rng, attempts)
            if (usedPrompts.add(item.prompt)) {
                result.add(item)
            }
        }
        return result
    }

    private fun pickTypeForDept(deptId: Int, diff: DifficultyTier, rng: Random): ExerciseType {
        val pool = when (deptId) {
            1 -> listOf(ExerciseType.COORDINATE_3D, ExerciseType.MULTIPLE_CHOICE, ExerciseType.FIND_ERROR, ExerciseType.MATCH_PAIRS, ExerciseType.FILL_BLANK)
            2, 3 -> listOf(ExerciseType.MULTIPLE_CHOICE, ExerciseType.FIND_ERROR, ExerciseType.MATCH_PAIRS, ExerciseType.PRACTICAL_BRIEF)
            4 -> listOf(ExerciseType.MULTIPLE_CHOICE, ExerciseType.FIND_ERROR, ExerciseType.FILL_BLANK, ExerciseType.MATCH_PAIRS)
            5 -> listOf(ExerciseType.MULTIPLE_CHOICE, ExerciseType.FIND_ERROR, ExerciseType.MATCH_PAIRS, ExerciseType.COORDINATE_3D)
            6 -> listOf(ExerciseType.MULTIPLE_CHOICE, ExerciseType.FIND_ERROR, ExerciseType.FILL_BLANK, ExerciseType.MATCH_PAIRS)
            7, 8, 9 -> listOf(ExerciseType.ANIMATION_TIMING, ExerciseType.MULTIPLE_CHOICE, ExerciseType.FIND_ERROR, ExerciseType.FILL_BLANK, ExerciseType.MATCH_PAIRS)
            else -> listOf(ExerciseType.MULTIPLE_CHOICE, ExerciseType.FIND_ERROR, ExerciseType.ANIMATION_TIMING, ExerciseType.MATCH_PAIRS)
        }
        return if (diff == DifficultyTier.BASIC && rng.nextFloat() < 0.25f) {
            ExerciseType.MICRO_SPRINT
        } else {
            pool[rng.nextInt(pool.size)]
        }
    }

    private fun buildExercise(
        deptId: Int,
        difficulty: DifficultyTier,
        type: ExerciseType,
        rng: Random,
        seq: Int
    ): ExerciseItem {
        val uid = "PROC-D$deptId-${difficulty.name.take(3)}-${type.name.take(4)}-${rng.nextInt(1000, 9999)}-$seq"
        val xp = (15 * difficulty.xpMultiplier).roundToInt()

        return when (type) {
            ExerciseType.COORDINATE_3D -> buildCoordinateChallenge(uid, deptId, difficulty, rng, xp)
            ExerciseType.ANIMATION_TIMING -> buildTimingChallenge(uid, deptId, difficulty, rng, xp)
            ExerciseType.MATCH_PAIRS -> buildMatchPairsChallenge(uid, deptId, difficulty, rng, xp)
            ExerciseType.FIND_ERROR -> buildFindErrorChallenge(uid, deptId, difficulty, rng, xp)
            ExerciseType.FILL_BLANK -> buildFillBlankChallenge(uid, deptId, difficulty, rng, xp)
            ExerciseType.PRACTICAL_BRIEF -> buildPracticalChallenge(uid, deptId, difficulty, rng, xp)
            ExerciseType.MICRO_SPRINT, ExerciseType.MULTIPLE_CHOICE -> buildParametricMcq(uid, deptId, difficulty, type, rng, xp)
        }
    }

    private fun buildCoordinateChallenge(
        id: String, deptId: Int, diff: DifficultyTier, rng: Random, xp: Int
    ): ExerciseItem {
        val sx = rng.nextInt(-5, 6)
        val sy = rng.nextInt(0, 6)
        val sz = rng.nextInt(-5, 6)
        val axisIndex = rng.nextInt(3)
        val stepRange = when (diff) {
            DifficultyTier.BASIC, DifficultyTier.EASY -> listOf(-3, -2, 2, 3, 4)
            DifficultyTier.INTERMEDIATE, DifficultyTier.DIFFICULT -> listOf(-7, -5, 5, 6, 8)
            DifficultyTier.ADVANCED, DifficultyTier.EXPERT -> listOf(-12, -9, 9, 11, 14)
        }
        val delta = stepRange[rng.nextInt(stepRange.size)]
        val axisName = listOf("X (Left/Right)", "Y (Up/Down)", "Z (Forward/Back)")[axisIndex]
        val shortAxis = listOf("X", "Y", "Z")[axisIndex]
        val endVec = when (axisIndex) {
            0 -> Triple(sx + delta, sy, sz)
            1 -> Triple(sx, sy + delta, sz)
            else -> Triple(sx, sy, sz + delta)
        }

        val correctStr = "(${endVec.first}, ${endVec.second}, ${endVec.third})"
        val wrong1 = "(${sx + delta}, ${sy + delta}, $sz)"
        val wrong2 = "(${sx - delta}, $sy, ${sz + delta})"
        val wrong3 = if (axisIndex == 0) "($sx, ${sy + delta}, $sz)" else "(${sx + delta}, $sy, $sz)"
        val rawOptions = listOf(correctStr, wrong1, wrong2, wrong3).distinct().toMutableList()
        while (rawOptions.size < 4) {
            rawOptions.add("(${sx + rng.nextInt(1, 9)}, ${sy + rng.nextInt(1, 9)}, $sz)")
        }
        val shuffled = rawOptions.shuffled(rng)
        val correctIdx = shuffled.indexOf(correctStr)
        val signDelta = if (delta > 0) "+$delta" else "$delta"

        return ExerciseItem(
            id = id,
            departmentId = deptId,
            topic = "3D Coordinates & Transforms",
            difficulty = diff,
            type = ExerciseType.COORDINATE_3D,
            learningObjective = "Compute exact world-space XYZ translation vectors",
            prompt = "A 3D object sits at ($sx, $sy, $sz). You drag the $axisName arrow by $signDelta units. What is its new position?",
            options = shuffled,
            correctOptionIndex = correctIdx,
            coordSpec = CoordinateChallengeSpec(
                targetAxis = shortAxis,
                targetDeltaLabel = "$signDelta on $shortAxis",
                startVec = Triple(sx, sy, sz),
                endVec = endVec
            ),
            hint = "Only the $shortAxis value changes by $signDelta; the other two coordinates stay identical.",
            explanation = "Starting at ($sx, $sy, $sz) and translating $signDelta along $shortAxis gives $correctStr.",
            xpReward = xp,
            softwareLinkNote = "Try this exact move in Prisma3D or the Pomana 3D Lab!"
        )
    }

    private fun buildTimingChallenge(
        id: String, deptId: Int, diff: DifficultyTier, rng: Random, xp: Int
    ): ExerciseItem {
        val fpsOptions = when (diff) {
            DifficultyTier.BASIC, DifficultyTier.EASY -> listOf(24, 30)
            else -> listOf(12, 24, 30, 60)
        }
        val fps = fpsOptions[rng.nextInt(fpsOptions.size)]
        val durationQuarterSec = rng.nextInt(2, 9) // 0.5s to 2.0s in 0.25s steps
        val seconds = durationQuarterSec * 0.25f
        val targetFrame = (fps * seconds).roundToInt()
        val totalFrames = (fps * 2.5f).roundToInt().coerceAtLeast(targetFrame + 6)
        val easings = listOf("Ease In-Out", "Bounce Out", "Linear", "Stepped (Twos)")
        val targetEasing = when (deptId) {
            8 -> "Stepped (Twos)"
            9 -> "Ease In-Out"
            else -> easings[rng.nextInt(2)]
        }

        val secLabel = if (seconds % 1f == 0f) "${seconds.toInt()}.0" else "$seconds"

        return ExerciseItem(
            id = id,
            departmentId = deptId,
            topic = "Timeline & Keyframe Timing",
            difficulty = diff,
            type = ExerciseType.ANIMATION_TIMING,
            learningObjective = "Convert real-world duration and FPS into exact keyframe numbers",
            prompt = "Scene frame rate is $fps fps. Place the impact keyframe exactly $secLabel seconds after Frame 0 using the slider below.",
            timingSpec = TimingChallengeSpec(
                fps = fps,
                totalFrames = totalFrames,
                targetFrame = targetFrame,
                toleranceFrames = if (diff.ordinal <= 1) 2 else 1,
                targetEasing = targetEasing
            ),
            hint = "Formula: Target Frame = Seconds ($secLabel) × FPS ($fps).",
            explanation = "$secLabel seconds × $fps fps = Frame $targetFrame. Pairing this with $targetEasing sells believable timing.",
            xpReward = xp + 5,
            softwareLinkNote = "Set this exact frame range in Prisma3D's bottom timeline."
        )
    }

    private fun buildMatchPairsChallenge(
        id: String, deptId: Int, diff: DifficultyTier, rng: Random, xp: Int
    ): ExerciseItem {
        val banks: Map<Int, List<MatchPair>> = mapOf(
            1 to listOf(
                MatchPair("Vertex", "Single 3D corner point"),
                MatchPair("Edge", "Line linking two vertices"),
                MatchPair("Face", "Flat polygon surface"),
                MatchPair("Origin", "World center (0, 0, 0)")
            ),
            2 to listOf(
                MatchPair("Extrude", "Pull a face out into depth"),
                MatchPair("Bevel", "Chamfer sharp edges for highlights"),
                MatchPair("Loop Cut", "Slice an edge ring around mesh"),
                MatchPair("Boolean", "Carve or union two shapes")
            ),
            3 to listOf(
                MatchPair("Move Brush", "Adjust big silhouette fast"),
                MatchPair("Crease Brush", "Carve sharp folds & wrinkles"),
                MatchPair("X-Symmetry", "Mirror strokes left to right"),
                MatchPair("Retopology", "Rebuild sculpt in clean quads")
            ),
            4 to listOf(
                MatchPair("Metallic = 1", "Pure metal conductor surface"),
                MatchPair("Roughness = 0.1", "Mirror-glossy specular highlight"),
                MatchPair("UV Seam", "Cut line to unwrap 3D to 2D"),
                MatchPair("Normal Map", "Fakes micro bumps cheaply")
            ),
            5 to listOf(
                MatchPair("Key Light", "Primary directional light source"),
                MatchPair("Fill Light", "Softens deep shadow side"),
                MatchPair("Rim Light", "Back edge silhouette halo"),
                MatchPair("85mm Lens", "Flattering portrait compression")
            ),
            6 to listOf(
                MatchPair("Pelvis Bone", "Root center-of-mass parent"),
                MatchPair("IK Rig", "Pins hands/feet to world goals"),
                MatchPair("FK Rig", "Rotates parent-to-child arcs"),
                MatchPair("Weight Paint", "Controls vertex pull per bone")
            ),
            7 to listOf(
                MatchPair("Anticipation", "Wind-up before main action"),
                MatchPair("Squash & Stretch", "Volume-preserving impact flex"),
                MatchPair("Follow-Through", "Hair/tail settles after body stops"),
                MatchPair("Bezier Curve", "Smooth ease-in and ease-out")
            ),
            8 to listOf(
                MatchPair("On Twos", "Hold each pose for 2 frames"),
                MatchPair("Smear Frame", "Stretched limb bridging fast motion"),
                MatchPair("Cel Shader", "Graphic stepped cartoon lighting"),
                MatchPair("Stepped Curve", "Instant snap without blend")
            ),
            9 to listOf(
                MatchPair("Overshoot", "Pop past 100% then settle"),
                MatchPair("Turntable", "Smooth 360° product orbit"),
                MatchPair("Attack Loop", "Minimal wind-up for game input"),
                MatchPair("Wheel Sync", "Angle = Distance / Radius")
            ),
            10 to listOf(
                MatchPair("Emitter", "Spawns particles with velocity"),
                MatchPair("Substeps", "Prevents fast cloth clipping"),
                MatchPair("Compositing", "Adds bloom & color grading"),
                MatchPair("Voxel Grid", "3D cells for smoke & fire")
            ),
            11 to listOf(
                MatchPair("PNG Sequence", "Crash-safe frame-by-frame export"),
                MatchPair("Denoiser", "Cleans grain at lower samples"),
                MatchPair("Animatic", "Timed storyboard edit test"),
                MatchPair("Foley Sync", "Footstep audio on contact frame")
            ),
            12 to listOf(
                MatchPair("LOD0 -> LOD2", "Swap simpler meshes at distance"),
                MatchPair("Instancing", "Share one mesh data block"),
                MatchPair("Freeze Scale", "Reset scale cleanly to (1,1,1)"),
                MatchPair("Geometry Nodes", "Non-destructive procedural setup")
            )
        )

        val pairs = (banks[deptId] ?: banks[1]!!).shuffled(rng).take(4)
        return ExerciseItem(
            id = id,
            departmentId = deptId,
            topic = CurriculumCatalog.getDepartment(deptId)?.title ?: "3D Concepts",
            difficulty = diff,
            type = ExerciseType.MATCH_PAIRS,
            learningObjective = "Connect core 3D terminology with practical studio functions",
            prompt = "Tap each concept on the left and match it with its exact role on the right.",
            matchPairs = pairs,
            hint = "Eliminate the terms you are 100% sure of first.",
            explanation = pairs.joinToString(" • ") { "${it.left} → ${it.right}" },
            xpReward = xp + 5
        )
    }

    private fun buildFindErrorChallenge(
        id: String, deptId: Int, diff: DifficultyTier, rng: Random, xp: Int
    ): ExerciseItem {
        data class ErrorCase(val topic: String, val scenario: String, val fix: String, val distractors: List<String>, val why: String)

        val cases = listOf(
            ErrorCase(
                "Topology",
                "A character's knee collapses flat like a bent soda straw whenever rotated 90 degrees. Which mesh flaw caused this?",
                "Only 1 edge loop sits at the knee instead of 3 supporting quad loops",
                listOf("The camera FOV is 35mm", "The texture file is PNG", "The scene runs at 24fps"),
                "Deforming joints need 3 tight quad edge loops to hold volume when bent."
            ),
            ErrorCase(
                "Normals",
                "Inside a game engine, one side of your modeled house looks completely invisible from outside, yet visible from inside!",
                "Inverted Face Normals pointing inward instead of outward (Recalculate Normals)",
                listOf("Missing audio track", "Excessive Rim light", "Camera Depth of Field is too shallow"),
                "Game engines cull backfaces; flipping/recalculating normals outward makes walls visible."
            ),
            ErrorCase(
                "PBR Shading",
                "A wooden crate shines like liquid chrome under studio lights. Which material slider was set wrong?",
                "Metallic was mistakenly raised near 1.0 instead of 0.0",
                listOf("Roughness was set to 0.85", "UV map had clean seams", "Key light was warm orange"),
                "Wood is dielectric (Metallic = 0.0); Metallic = 1.0 gives metallic reflection behavior."
            ),
            ErrorCase(
                "Rigging",
                "When lifting the character's head bone, the floor grid and camera move with it!",
                "The floor or camera was accidentally parented under the Head bone",
                listOf("The scene uses 3-point lighting", "The head mesh uses quads", "Denoising is enabled"),
                "Check the Outliner hierarchy so only head accessories (hats, glasses) are children of Head."
            ),
            ErrorCase(
                "Lighting",
                "Shadows on your outdoor sunny plaza look razor-sharp like laser cutouts. What parameter needs adjusting?",
                "Sun/Key Light angular size/radius is near 0 — widen it slightly for natural penumbra softness",
                listOf("Set Metallic to 1.0 on grass", "Switch animation to Twos", "Freeze scale transforms"),
                "Point-sized lights create harsh artificial shadows; realistic sun/area lights have soft edge falloff."
            ),
            ErrorCase(
                "Animation Curves",
                "A character comes to a sudden mechanical halt at Frame 30, then drifts backward slightly before settling at Frame 40.",
                "Unwanted Bezier handle overshoot in the Graph Editor",
                listOf("Too many vertices on the shoe", "PNG sequence rendering", "X-Symmetry was enabled during sculpting"),
                "Bezier auto-handles can dip below/above keyframe values; flatten or clamp handles cleanly."
            ),
            ErrorCase(
                "UV Mapping",
                "A brick wall texture looks razor-crisp on the front face but stretches into ugly long streaks along the doorway thickness.",
                "The extruded inner doorway faces were never re-unwrapped in UV space",
                listOf("FPS was set to 30", "Rim light intensity was 0.8", "Bones had IK enabled"),
                "After extruding new geometry, re-unwrap UVs so new faces occupy proper 2D texture area."
            ),
            ErrorCase(
                "Pipeline",
                "Exporting a prop from Blender/Prisma3D to a game engine makes it appear 100x too huge or rotated 90° flat on its back.",
                "Unapplied Scale/Rotation transforms and Y-up vs Z-up export axis mismatch",
                listOf("Audio sample rate was 48kHz", "Roughness map was grayscale", "Viewport grid was hidden"),
                "Apply transforms (Scale 1,1,1) and verify export Up-Axis settings."
            )
        )
        val picked = cases[rng.nextInt(cases.size)]
        val allOptions = (listOf(picked.fix) + picked.distractors).shuffled(rng)
        return ExerciseItem(
            id = id,
            departmentId = deptId,
            topic = picked.topic,
            difficulty = diff,
            type = ExerciseType.FIND_ERROR,
            learningObjective = "Diagnose real-world 3D production errors quickly",
            prompt = picked.scenario,
            options = allOptions,
            correctOptionIndex = allOptions.indexOf(picked.fix),
            hint = "Look for the structural 3D flaw affecting geometry, transforms, shaders, or curves.",
            explanation = picked.why,
            xpReward = xp + 5
        )
    }

    private fun buildFillBlankChallenge(
        id: String, deptId: Int, diff: DifficultyTier, rng: Random, xp: Int
    ): ExerciseItem {
        data class BlankItem(val topic: String, val prompt: String, val answer: String, val hint: String, val explanation: String)

        val fps = listOf(24, 30, 60)[rng.nextInt(3)]
        val secs = rng.nextInt(2, 6)
        val framesAns = (fps * secs).toString()

        val radius = rng.nextInt(1, 4)
        val cubesCount = rng.nextInt(2, 6)
        val totalCubeVerts = (cubesCount * 8).toString()

        val items = listOf(
            BlankItem(
                "Frame Math",
                "At $fps fps, an animation lasting $secs seconds contains exactly ___ frames.",
                framesAns,
                "Multiply seconds ($secs) by FPS ($fps).",
                "$secs × $fps = $framesAns frames."
            ),
            BlankItem(
                "Mesh Budget",
                "If 1 separate unmerged cube has 8 vertices, $cubesCount separate cubes have ___ vertices total.",
                totalCubeVerts,
                "8 vertices × $cubesCount cubes.",
                "Each standalone cube contributes 8 vertices: $cubesCount × 8 = $totalCubeVerts."
            ),
            BlankItem(
                "Axis Color",
                "In standard RGB = XYZ gizmos, the ___ axis is always colored Green.",
                "Y",
                "R=X, G=?, B=Z.",
                "Green maps to the Y axis."
            ),
            BlankItem(
                "PBR Rule",
                "For non-metal materials like wood, skin, and cloth, Metallic should be set to ___.",
                "0",
                "Enter a single digit (0 or 1).",
                "Dielectrics have Metallic = 0; pure conductors have Metallic = 1."
            ),
            BlankItem(
                "Lighting Ratio",
                "In three-point lighting, the main bright light shaping shadows is called the ___ light.",
                "Key",
                "Three-point lights are ___, Fill, and Rim.",
                "The Key light is the primary directional light."
            ),
            BlankItem(
                "Rigging",
                "To keep a walking character's feet locked firmly on the ground, animators switch leg controllers to ___.",
                "IK",
                "Two-letter abbreviation: Inverse Kinematics.",
                "IK (Inverse Kinematics) anchors the foot goal in world space."
            )
        )
        val picked = items[rng.nextInt(items.size)]
        return ExerciseItem(
            id = id,
            departmentId = deptId,
            topic = picked.topic,
            difficulty = diff,
            type = ExerciseType.FILL_BLANK,
            learningObjective = "Recall exact technical values and 3D terminology without multiple-choice clues",
            prompt = picked.prompt,
            fillBlankExpected = picked.answer,
            hint = picked.hint,
            explanation = picked.explanation,
            xpReward = xp + 5
        )
    }

    private fun buildPracticalChallenge(
        id: String, deptId: Int, diff: DifficultyTier, rng: Random, xp: Int
    ): ExerciseItem {
        val projects = CurriculumCatalog.allProjects.filter { it.departmentId == deptId }
            .ifEmpty { CurriculumCatalog.allProjects }
        val proj = projects[rng.nextInt(projects.size)]
        return ExerciseItem(
            id = id,
            departmentId = deptId,
            topic = "Practical Studio Brief",
            difficulty = diff,
            type = ExerciseType.PRACTICAL_BRIEF,
            learningObjective = "Apply skills inside a real mobile or desktop 3D app",
            prompt = "Build Brief — ${proj.title}: ${proj.description}",
            options = listOf(
                "Completed in ${proj.recommendedSoftware} & verified rubric",
                "Need a hint / breakdown from Pomana AI first"
            ),
            correctOptionIndex = 0,
            hint = proj.rubricChecklist.joinToString(" • "),
            explanation = "Great job practicing in real 3D software! Upload a screenshot in the Projects & Portfolio tab to track it.",
            xpReward = xp + 15,
            softwareLinkNote = "${proj.recommendedSoftware} — ${proj.freeVsPaidNote}"
        )
    }

    private fun buildParametricMcq(
        id: String, deptId: Int, diff: DifficultyTier, type: ExerciseType, rng: Random, xp: Int
    ): ExerciseItem {
        // Procedural quantitative questions across lighting, camera, topology, VFX, and timing
        val variant = rng.nextInt(5)
        return when (variant) {
            0 -> {
                val baseTris = listOf(400, 800, 1200, 2500)[rng.nextInt(4)]
                val subdTris = baseTris * 4
                val opts = listOf(
                    "$subdTris triangles (each quad splits into 4)",
                    "${baseTris * 2} triangles",
                    "${baseTris / 2} triangles",
                    "${baseTris * 16} triangles"
                ).shuffled(rng)
                ExerciseItem(
                    id = id, departmentId = deptId, topic = "Subdivision Math", difficulty = diff, type = type,
                    learningObjective = "Predict polygon growth across subdivision levels",
                    prompt = "A low-poly character starts at $baseTris triangles. Adding 1 level of Catmull-Clark Subdivision multiplies quad face count by 4. Roughly how many triangles result?",
                    options = opts,
                    correctOptionIndex = opts.indexOf("$subdTris triangles (each quad splits into 4)"),
                    hint = "Multiply $baseTris by 4.",
                    explanation = "Every single level of subdivision quadruples polygon count ($baseTris × 4 = $subdTris), so use subdivision sparingly on mobile!"
                )
            }
            1 -> {
                val keyWatt = listOf(100, 200, 400)[rng.nextInt(3)]
                val fillWatt = (keyWatt * 0.3f).roundToInt()
                val opts = listOf(
                    "~${fillWatt}W (about 30% of Key for clear shadow modeling)",
                    "${keyWatt}W (identical brightness to Key)",
                    "${keyWatt * 4}W (4x brighter than Key)",
                    "0W with camera deleted"
                ).shuffled(rng)
                ExerciseItem(
                    id = id, departmentId = deptId, topic = "Lighting Ratio", difficulty = diff, type = type,
                    learningObjective = "Balance Key and Fill intensities for dimensional shading",
                    prompt = "Your studio Key Light is set to ${keyWatt}W. What Fill Light intensity creates dramatic, dimensional shading without pitch-black shadows?",
                    options = opts,
                    correctOptionIndex = opts.indexOf("~${fillWatt}W (about 30% of Key for clear shadow modeling)"),
                    hint = "Keep Fill around 25–35% of Key intensity.",
                    explanation = "A 3:1 Key-to-Fill ratio (~${fillWatt}W) preserves crisp form while keeping shadow details legible."
                )
            }
            2 -> {
                val wheelRadius = listOf(0.5, 1.0)[rng.nextInt(2)]
                val circumference = ((2 * PI * wheelRadius) * 100).roundToInt() / 100.0
                val opts = listOf(
                    "~${circumference} meters (2 × π × radius)",
                    "${wheelRadius} meters",
                    "${wheelRadius * 2} meters",
                    "100 meters"
                ).shuffled(rng)
                ExerciseItem(
                    id = id, departmentId = deptId, topic = "Vehicle Physics", difficulty = diff, type = type,
                    learningObjective = "Calculate exact travel distance per 360° wheel rotation",
                    prompt = "A vehicle tire has radius ${wheelRadius}m. Exactly how far forward must the car move during one full 360° rotation so the tire doesn't skid?",
                    options = opts,
                    correctOptionIndex = opts.indexOf("~${circumference} meters (2 × π × radius)"),
                    hint = "Use circle circumference C = 2 × π × r.",
                    explanation = "1 full revolution covers 2πr ≈ ${circumference}m."
                )
            }
            3 -> {
                val res = listOf(32, 48, 64)[rng.nextInt(3)]
                val scale2 = res * 2
                val opts = listOf(
                    "8× heavier (memory scales by 2 × 2 × 2 in 3D)",
                    "2× heavier",
                    "4× heavier",
                    "Half as heavy"
                ).shuffled(rng)
                ExerciseItem(
                    id = id, departmentId = deptId, topic = "VFX Domain Cost", difficulty = diff, type = type,
                    learningObjective = "Understand cubic scaling in 3D fluid/smoke grids",
                    prompt = "Increasing a smoke simulation grid from $res to $scale2 resolution doubles X, Y, and Z subdivisions. How much does voxel count increase?",
                    options = opts,
                    correctOptionIndex = opts.indexOf("8× heavier (memory scales by 2 × 2 × 2 in 3D)"),
                    hint = "Multiply 2 × 2 × 2 across all 3 spatial axes.",
                    explanation = "3D grids scale cubically (2³ = 8×), which is why VFX artists test sims at low resolution first."
                )
            }
            else -> {
                val fromSample = listOf(12, 24)[rng.nextInt(2)]
                val promptTxt = if (fromSample == 12) {
                    "You want crisp stop-motion / anime energy at 24fps export. Holding every pose for 2 frames yields how many unique pose changes per second?"
                } else {
                    "A walk cycle takes 1.0 second at 24fps. At which frame does the opposite leg hit its symmetrical mid-cycle Contact pose?"
                }
                val correct = if (fromSample == 12) "12 pose changes/sec" else "Frame 12"
                val distractors = if (fromSample == 12) listOf("48 pose changes/sec", "60 pose changes/sec", "2 pose changes/sec") else listOf("Frame 3", "Frame 48", "Frame 60")
                val opts = (listOf(correct) + distractors).shuffled(rng)
                ExerciseItem(
                    id = id, departmentId = deptId, topic = "Animation Timing", difficulty = diff, type = type,
                    learningObjective = "Reason accurately about frame-by-frame pose spacing",
                    prompt = promptTxt,
                    options = opts,
                    correctOptionIndex = opts.indexOf(correct),
                    hint = "Divide 24 by 2.",
                    explanation = "24 ÷ 2 = 12. Half a 24-frame walk cycle lands squarely on Frame 12!"
                )
            }
        }
    }
}
