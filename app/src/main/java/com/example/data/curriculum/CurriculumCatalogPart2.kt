package com.example.data.curriculum

import com.example.data.model.*

internal object CurriculumCatalogPart2 {

    val departmentsPart2: List<Department> = listOf(
        Department(
            id = 7,
            title = "3D Animation",
            subtitle = "12 principles, keyframes, graph editor, walk cycles & lip sync",
            topics = listOf(
                "Animation Principles", "Keyframes & Timeline", "Graphs & Interpolation",
                "Walk & Run Cycles", "Jumping & Dancing", "Facial Expressions & Lip Sync", "Motion Capture Concepts"
            ),
            primarySoftware = "Prisma3D / Blender",
            accentColorHex = 0xFF22C55E,
            lessons = listOf(
                Lesson(
                    id = "d7_l1",
                    departmentId = 7,
                    orderIndex = 1,
                    title = "Keyframes, Timing & Ease Curves (Squash & Stretch)",
                    durationMinutes = 12,
                    xpReward = 45,
                    conceptSimple = "Animation is the illusion of life created by changing poses over time. You set Keyframes on important moments (e.g., ball at peak height on Frame 0, hitting the floor on Frame 12). The computer interpolates frames in between—if the curve is Linear, motion feels robotic; if it uses Ease In / Ease Out (Bezier curves), gravity and weight come alive! Add Squash & Stretch to give bouncy energy while preserving volume.",
                    diagramSpec = DiagramSpec(
                        type = DiagramType.KEYFRAME_CURVE,
                        title = "Interactive Keyframe & Easing Lab",
                        caption = "Switch to the Animation tab in 3D Lab to test 24fps vs 60fps, Linear vs Bounce easing, and Squash & Stretch!",
                        interactivePrimitive = "Cube"
                    ),
                    realExamples = listOf(
                        "At 24fps (cinema standard), a half-second action takes exactly 12 frames.",
                        "Before jumping upward, a character crouches down first—that's Anticipation."
                    ),
                    mobileWorkflowSteps = listOf(
                        WorkflowStep(1, "Prisma3D (Android Free)", "Switch to Animation tab, tap Frame 0 to record a start keyframe, scrub to Frame 24, move object, and tap Keyframe.", "Full timeline keyframing is free."),
                        WorkflowStep(2, "Prisma3D (Android Free)", "Adjust interpolation between keyframes from Linear to Smooth/Bezier for realistic weight.", "Graph easing saves robotic motion.")
                    ),
                    guidedTaskPrompt = "In 3D Lab's Animation tab, play the bounce at Linear vs Bounce easing and toggle Squash & Stretch.",
                    guidedTaskHint = "Notice how Linear bounces feel stiff, while Ease-In acceleration into impact feels heavy.",
                    guidedTaskChecklist = listOf(
                        "Calculate frames = seconds * FPS",
                        "Compare Linear vs Ease In-Out vs Bounce",
                        "Preserve volume when squashing on impact"
                    ),
                    masteryQuiz = listOf(
                        ExerciseItem(
                            id = "d7_l1_q1", departmentId = 7, topic = "Timing & FPS", difficulty = DifficultyTier.BASIC,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Convert real-time seconds into frames at 24fps",
                            prompt = "At standard film rate (24 fps), how many frames long is a 2-second jump?",
                            options = listOf("12 frames", "24 frames", "48 frames", "60 frames"),
                            correctOptionIndex = 2,
                            hint = "Multiply 24 frames per second by 2 seconds.",
                            explanation = "2 seconds × 24 fps = 48 frames."
                        ),
                        ExerciseItem(
                            id = "d7_l1_q2", departmentId = 7, topic = "Squash & Stretch", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Maintain volume during deformation",
                            prompt = "When a bouncing rubber ball squashes flat vertically (Y = 0.6) upon hitting the floor, what must happen to X and Z scale?",
                            options = listOf(
                                "They must expand outward (> 1.0) so total volume stays believable",
                                "They must also shrink to 0.6",
                                "They should rotate 180 degrees",
                                "Nothing—volume does not matter"
                            ),
                            correctOptionIndex = 0,
                            hint = "Squeezing a water balloon flat makes it bulge sideways.",
                            explanation = "Volume preservation keeps characters looking solid and elastic instead of deflating."
                        ),
                        ExerciseItem(
                            id = "d7_l1_q3", departmentId = 7, topic = "Interpolation", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.FIND_ERROR,
                            learningObjective = "Fix floaty/robotic motion via graph easing",
                            prompt = "A pendulum swings at constant speed from left to right and slams abruptly to a halt. Which interpolation mistake caused this?",
                            options = listOf(
                                "Keyframes are set to Linear interpolation instead of Ease-In/Ease-Out Bezier curves",
                                "The pendulum has too many polygons",
                                "The Fill light is too bright",
                                "IK is turned off"
                            ),
                            correctOptionIndex = 0,
                            hint = "Real pendulums slow down at the top of the arc and accelerate at the bottom.",
                            explanation = "Linear curves give constant unnatural speed; Bezier Ease-In/Ease-Out creates natural gravity arcs."
                        ),
                        ExerciseItem(
                            id = "d7_l1_q4", departmentId = 7, topic = "Walk Cycles", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Identify core poses of a walk cycle",
                            prompt = "In a classic 24-frame walk cycle, what are the four signature repeating poses per step?",
                            options = listOf(
                                "Contact, Down, Passing, High-Point",
                                "Vertex, Edge, Face, UV",
                                "Key, Fill, Rim, Ambient",
                                "Render, Export, Bake, Zip"
                            ),
                            correctOptionIndex = 0,
                            hint = "Heel hits ground, weight drops, back leg passes front leg, head reaches peak.",
                            explanation = "Contact -> Down -> Passing -> High-Point creates the bouncy figure-eight rhythm of walking."
                        )
                    ),
                    practicalProject = PracticalProjectSpec(
                        id = "proj_walk_cycle", title = "Bouncing Ball + Loopable Walk Cycle", departmentId = 7,
                        difficulty = DifficultyTier.INTERMEDIATE, recommendedSoftware = "Prisma3D or Blender",
                        freeVsPaidNote = "100% free in Prisma3D & Blender.",
                        description = "Animate a heavy bowling ball vs a light ping-pong ball bouncing across 48 frames, then graduate to a 24-frame looping robot walk cycle.",
                        deliverables = listOf("Video clip or 4 key pose screenshots (Contact, Down, Passing, High)"),
                        rubricChecklist = listOf("Distinct weight timing", "Clean ease curves at arc peaks", "Seamless loop from frame 24 to frame 0")
                    ),
                    revisionTips = listOf("Slow in, slow out at peaks; fast spacing near the ground.", "Anticipation prepares the audience's eye before big motion.")
                )
            )
        ),
        Department(
            id = 8,
            title = "Different Animation Styles",
            subtitle = "Cartoon, anime 3D, realistic, stylised, stop-motion & creature",
            topics = listOf(
                "Cartoon Animation", "Anime-Inspired 3D", "Realistic Animation",
                "Stylised Animation", "Stop-Motion-Inspired 3D", "Fantasy & Creature Motion"
            ),
            primarySoftware = "Blender / Prisma3D",
            accentColorHex = 0xFFEC4899,
            lessons = listOf(
                Lesson(
                    id = "d8_l1",
                    departmentId = 8,
                    orderIndex = 1,
                    title = "Animating on Twos, Stepped Curves & Stylized Timing",
                    durationMinutes = 10,
                    xpReward = 40,
                    conceptSimple = "Not every 3D film wants ultra-smooth 60fps realism! Spider-Verse and anime-inspired 3D use 'Animating on Twos' (holding poses for 2 frames at 24fps = 12 pose changes/sec) with Stepped/Constant interpolation for crisp, punchy comic-book energy, while realistic VFX uses smooth motion blur and subtle weight shifts.",
                    diagramSpec = DiagramSpec(
                        type = DiagramType.KEYFRAME_CURVE,
                        title = "Stepped vs Smooth Interpolation",
                        caption = "Compare snapping stepped poses (stop-motion/anime) against fluid continuous curves.",
                        interactivePrimitive = "Rocket"
                    ),
                    realExamples = listOf(
                        "Spider-Verse mixes characters on Twos with smooth 24fps cameras for energetic contrast.",
                        "Heavy fantasy creatures (dragons, giants) use slower, overlapping follow-through on tails and wings."
                    ),
                    mobileWorkflowSteps = listOf(
                        WorkflowStep(1, "Prisma3D (Android Free)", "Place strong held key poses every 2–4 frames with tight snap transitions to test cartoon smear timing.", "Keyframe spacing works freely in Prisma3D."),
                        WorkflowStep(2, "Blender (Desktop Free)", "Use Toon/Cel shaders + Constant/Stepped interpolation modifiers.", "Free shader & curve tools.")
                    ),
                    guidedTaskPrompt = "Compare a 12fps-feel stepped punch vs a 60fps smooth punch in your mental timing or 3D timeline.",
                    guidedTaskHint = "Fewer held frames make punches feel snappier and more graphic.",
                    guidedTaskChecklist = listOf(
                        "Understand Ones (24 poses/sec) vs Twos (12 poses/sec)",
                        "Match shader style to motion style",
                        "Use strong silhouette holds for cartoon readability"
                    ),
                    masteryQuiz = listOf(
                        ExerciseItem(
                            id = "d8_l1_q1", departmentId = 8, topic = "Anime & Stop-Motion 3D", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Define animating on Twos",
                            prompt = "What does 'animating on Twos' in a 24fps scene mean?",
                            options = listOf(
                                "Each distinct character pose is held for 2 frames (12 pose updates per second)",
                                "Two characters must always walk together",
                                "Using only 2 polygons per limb",
                                "Rendering at 2fps"
                            ),
                            correctOptionIndex = 0,
                            hint = "24 divided by 2 equals 12 changes per second.",
                            explanation = "Animating on Twos holds poses for 2 frames, giving snappy hand-crafted anime and stop-motion rhythm."
                        ),
                        ExerciseItem(
                            id = "d8_l1_q2", departmentId = 8, topic = "Cartoon Style", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Use exaggeration and holds effectively",
                            prompt = "Which technique sells a fast cartoon punch or dash across just 2–3 frames?",
                            options = listOf("Smear frames and strong anticipation", "Zero anticipation", "Linear slow drift", "Flat ambient lighting"),
                            correctOptionIndex = 0,
                            hint = "Stretched limbs bridge huge gaps between key poses.",
                            explanation = "Smear frames and crisp anticipation sell lightning-fast cartoon impact."
                        ),
                        ExerciseItem(
                            id = "d8_l1_q3", departmentId = 8, topic = "Creature Animation", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Apply weight & overlap to giant creatures",
                            prompt = "How should a 40-foot dragon's tail move when the dragon stops walking?",
                            options = listOf(
                                "Follow-through: the tail continues swinging and settles several frames after the hips stop",
                                "Freeze instantly on the exact same frame as the feet",
                                "Rotate in a rigid straight line",
                                "Disappear from the scene"
                            ),
                            correctOptionIndex = 0,
                            hint = "Heavy appendages carry momentum.",
                            explanation = "Overlapping action and follow-through convey realistic mass on tails, capes, and wings."
                        ),
                        ExerciseItem(
                            id = "d8_l1_q4", departmentId = 8, topic = "Style Consistency", difficulty = DifficultyTier.BASIC,
                            type = ExerciseType.FIND_ERROR,
                            learningObjective = "Avoid mixing clashing realism & cartoon physics unintentionally",
                            prompt = "An anime cel-shaded hero has ultra-slow, floaty motion-captured micro-jitters while standing still. Why does it look uncanny?",
                            options = listOf(
                                "Graphic cel-shaded designs clash with noisy high-frequency realism; clean held poses fit better",
                                "Cel shading forbids shadows",
                                "Motion capture cannot record arms",
                                "24fps is too fast"
                            ),
                            correctOptionIndex = 0,
                            hint = "Render style and motion style must agree.",
                            explanation = "Stylised shaders shine brightest with deliberate, posed silhouettes and controlled holds."
                        )
                    ),
                    practicalProject = PracticalProjectSpec(
                        id = "proj_style_study", title = "Same Jump, Two Styles (Cartoon vs Realistic)", departmentId = 8,
                        difficulty = DifficultyTier.DIFFICULT, recommendedSoftware = "Prisma3D or Blender",
                        freeVsPaidNote = "Free tools supported.",
                        description = "Animate a simple character or cube jumping a gap twice: once with exaggerated squash-stretch cartoon holds, once with realistic grounded weight.",
                        deliverables = listOf("Two side-by-side clips or pose sheets"),
                        rubricChecklist = listOf("Clear timing contrast", "Deliberate holds in cartoon take", "Subtle settle in realistic take")
                    ),
                    revisionTips = listOf("Motion style must match visual rendering style.", "On Twos = classic hand-crafted punch.")
                )
            )
        ),
        Department(
            id = 9,
            title = "Specialised Animation",
            subtitle = "Mechanical, vehicles, products, motion graphics, logos & game loops",
            topics = listOf(
                "Mechanical & Gear Animation", "Vehicle Suspension", "Product Turntables",
                "Motion Graphics & Logo Animation", "Game Animation Loops", "Architectural Walkthroughs", "Scientific Visualization"
            ),
            primarySoftware = "Blender / Prisma3D",
            accentColorHex = 0xFF06B6D4,
            lessons = listOf(
                Lesson(
                    id = "d9_l1",
                    departmentId = 9,
                    orderIndex = 1,
                    title = "Logo Motion Graphics, Product Reveals & Game Cycles",
                    durationMinutes = 11,
                    xpReward = 45,
                    conceptSimple = "Specialised 3D powers commercials, games, and explainers. Mechanical and logo animation relies on ultra-clean pivot placement, mathematical gear ratios, and snappy overshoot curves. Game animation requires zero-latency idle/attack/run loops clipped tightly at Frame 0 so players feel instant responsiveness.",
                    diagramSpec = DiagramSpec(
                        type = DiagramType.CAMERA_FOV,
                        title = "Product Turntable & Motion Graphics Framing",
                        caption = "Test smooth studio orbit and telephoto product lens framing in 3D Lab.",
                        interactivePrimitive = "Cube"
                    ),
                    realExamples = listOf(
                        "A smartphone commercial sweeps a smooth 85mm camera past glossy chamfered edges in 3 seconds.",
                        "A game attack animation skips long wind-ups after button press so combat feels snappy."
                    ),
                    mobileWorkflowSteps = listOf(
                        WorkflowStep(1, "Prisma3D (Android Free)", "Create 3D text/logo primitives, animate staggered Z-pop + overshoot scale.", "Text & primitive animation is free."),
                        WorkflowStep(2, "Blender (Desktop Free)", "Export clean FBX/GLTF game animation clips with NLA strips.", "Standard indie game pipeline.")
                    ),
                    guidedTaskPrompt = "Design a 3-second (72 frames @ 24fps) animated logo reveal plan using stagger + overshoot.",
                    guidedTaskHint = "Stagger each letter by 3 frames so the reveal ripples across the screen.",
                    guidedTaskChecklist = listOf(
                        "Place pivot at bottom edge for pop-up logos",
                        "Match wheel rotation circumference = 2*pi*r to forward distance",
                        "Keep game attack anticipation minimal for responsiveness"
                    ),
                    masteryQuiz = listOf(
                        ExerciseItem(
                            id = "d9_l1_q1", departmentId = 9, topic = "Game Animation", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Design responsive game animation timing",
                            prompt = "Why should a playable video game sword swing have much shorter anticipation than a movie cutscene sword swing?",
                            options = listOf(
                                "Long wind-ups cause sluggish input delay for the player",
                                "Games cannot render more than 10 frames",
                                "Movie swords have more textures",
                                "Game engines don't support Z coordinates"
                            ),
                            correctOptionIndex = 0,
                            hint = "Think of how a game controller button feels when pressed.",
                            explanation = "Players expect instant feedback on frame 1–3; excessive anticipation feels like lag."
                        ),
                        ExerciseItem(
                            id = "d9_l1_q2", departmentId = 9, topic = "Vehicle Animation", difficulty = DifficultyTier.DIFFICULT,
                            type = ExerciseType.FIND_ERROR,
                            learningObjective = "Fix wheel spin speed vs car travel distance",
                            prompt = "A 3D car drives 10 meters forward, yet its tires appear to spin backward or slide on ice. What caused this?",
                            options = listOf(
                                "Wheel rotation angle didn't match traveled distance (distance / wheel radius) or hit wagon-wheel aliasing",
                                "The car needs Rim lighting",
                                "Roughness is set to 0.5",
                                "The camera is at 50mm"
                            ),
                            correctOptionIndex = 0,
                            hint = "Rotation in radians equals distance divided by wheel radius.",
                            explanation = "Sync wheel rotation mathematically to chassis displacement so tires grip the road cleanly."
                        ),
                        ExerciseItem(
                            id = "d9_l1_q3", departmentId = 9, topic = "Logo Motion Graphics", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Use overshoot for polished motion graphics",
                            prompt = "A 3D logo scales from 0% -> 112% -> 100% as it pops onto screen. What is this principle called?",
                            options = listOf("Overshoot & Settle", "UV Unwrapping", "Weight Painting", "Bake Lighting"),
                            correctOptionIndex = 0,
                            hint = "It goes slightly past the target before settling back.",
                            explanation = "Overshoot gives UI & logo motion graphics elastic, high-end agency polish."
                        ),
                        ExerciseItem(
                            id = "d9_l1_q4", departmentId = 9, topic = "Architectural Walkthrough", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Maintain steady camera height in walkthroughs",
                            prompt = "In an architectural house walkthrough, what camera height feels most natural to viewers?",
                            options = listOf("Human eye level (~1.6m) with smooth level horizon", "Floor level (0.1m) tilted 45 degrees", "Spinning rapidly upside down", "Extreme 8mm fisheye"),
                            correctOptionIndex = 0,
                            hint = "Viewers want to feel like they are walking through the rooms.",
                            explanation = "Eye-level tracking with gentle dolly speeds makes spaces feel authentic and navigable."
                        )
                    ),
                    practicalProject = PracticalProjectSpec(
                        id = "proj_animated_logo", title = "3D Animated Brand Logo Reveal", departmentId = 9,
                        difficulty = DifficultyTier.INTERMEDIATE, recommendedSoftware = "Prisma3D or Blender",
                        freeVsPaidNote = "Free in Prisma3D & Blender.",
                        description = "Build a 3D emblem/text logo, animate an energetic staggered entrance with overshoot, and sweep a studio Rim light across it.",
                        deliverables = listOf("3-second 24fps clip or 3 keyframe stills"),
                        rubricChecklist = listOf("Clean bevelled 3D letterforms", "Staggered entrance timing", "Glinting specular light sweep")
                    ),
                    revisionTips = listOf("Motion graphics love strong ease-out curves and 2–4 frame staggers.", "Game loops must start and end seamlessly on frame 0/N.")
                )
            )
        ),
        Department(
            id = 10,
            title = "Visual Effects",
            subtitle = "Particles, smoke, fire, water, explosions, cloth, hair & compositing",
            topics = listOf(
                "Particle Systems", "Smoke & Fire", "Water & Fluid",
                "Explosions & Destruction", "Cloth Simulation", "Hair & Fur", "Compositing"
            ),
            primarySoftware = "Blender (Desktop Free) / Prisma3D Particles",
            accentColorHex = 0xFFEA580C,
            lessons = listOf(
                Lesson(
                    id = "d10_l1",
                    departmentId = 10,
                    orderIndex = 1,
                    title = "Particles, Physics Simulations & Compositing Passes",
                    durationMinutes = 12,
                    xpReward = 50,
                    conceptSimple = "Visual Effects (VFX) let physics engines animate thousands of pieces automatically! Particle systems emit sparks, rain, or dust using velocity and gravity. Cloth & Rigid Body simulations drape capes or shatter stone walls using collision margins and substeps. Finally, Compositing blends glowing bloom, motion blur, and color grading after rendering.",
                    diagramSpec = DiagramSpec(
                        type = DiagramType.PARTICLE_SIM,
                        title = "Particle Emitter & Physics Forces",
                        caption = "Understand emitter velocity, gravity drag, turbulence, and render passes.",
                        interactivePrimitive = "Pyramid"
                    ),
                    realExamples = listOf(
                        "A campfire uses three layered systems: rising embers (particles), curling smoke (volume), and flickering warm point light.",
                        "Cloth passes through a character's leg if Simulation Substeps are too low for fast running."
                    ),
                    mobileWorkflowSteps = listOf(
                        WorkflowStep(1, "Blender (Desktop Free)", "Use Quick Smoke / Cloth / Rigid Body Fracture on desktop for full physics caching.", "Full VFX suite is 100% free in Blender; heavy fluid/fire sims exceed mobile phone RAM."),
                        WorkflowStep(2, "Prisma3D (Android Free)", "Simulate stylized sparks, dust, or debris using lightweight billboard planes animated in bursts.", "Fake low-poly VFX keeps mobile renders fast.")
                    ),
                    guidedTaskPrompt = "Design a layered explosion breakdown (Flash -> Shockwave -> Debris -> Fireball -> Smoke).",
                    guidedTaskHint = "Real explosions aren't one sphere—they are 4–5 elements staggered by milliseconds.",
                    guidedTaskChecklist = listOf(
                        "Separate emitter velocity from gravity force",
                        "Increase substeps to stop fast cloth clipping",
                        "Layer glow/bloom in compositing instead of overexposing lights"
                    ),
                    masteryQuiz = listOf(
                        ExerciseItem(
                            id = "d10_l1_q1", departmentId = 10, topic = "Cloth Simulation", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.FIND_ERROR,
                            learningObjective = "Fix fast-moving cloth collision pass-through",
                            prompt = "A superhero sprints rapidly and their cape passes straight through their back mesh. Which simulation setting fixes this?",
                            options = listOf(
                                "Increase Simulation Substeps / Quality Steps and collision thickness",
                                "Set Metallic to 1.0 on the cape",
                                "Lower FPS to 12",
                                "Delete the Rim light"
                            ),
                            correctOptionIndex = 0,
                            hint = "The cloth moved too far in a single frame and skipped collision detection.",
                            explanation = "Higher substeps evaluate physics multiple times between frames so fast limbs don't poke through."
                        ),
                        ExerciseItem(
                            id = "d10_l1_q2", departmentId = 10, topic = "Compositing", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Understand the role of Compositing",
                            prompt = "What happens during the Compositing stage after raw 3D frames finish rendering?",
                            options = listOf(
                                "Combining render passes with glow/bloom, color grading, mist, and live-action plates",
                                "Rebuilding all mesh vertices from scratch",
                                "Creating the initial storyboard sketches",
                                "Deleting the skeleton rig"
                            ),
                            correctOptionIndex = 0,
                            hint = "Post-processing magic layered on top of rendered passes.",
                            explanation = "Compositing polishes renders with bloom, depth mist, lens flares, and color matching."
                        ),
                        ExerciseItem(
                            id = "d10_l1_q3", departmentId = 10, topic = "Particle Systems", difficulty = DifficultyTier.BASIC,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Control rising vs falling particles",
                            prompt = "To simulate floating campfire embers drifting upward, which force direction should you apply?",
                            options = listOf("Negative gravity / upward buoyancy (+Y)", "Downward acceleration (-Y = -9.8)", "Zero lifetime", "Locked X rotation"),
                            correctOptionIndex = 0,
                            hint = "Hot air rises.",
                            explanation = "Upward buoyancy (+Y) plus slight turbulence makes embers drift organically skyward."
                        ),
                        ExerciseItem(
                            id = "d10_l1_q4", departmentId = 10, topic = "Fluid & Smoke", difficulty = DifficultyTier.ADVANCED,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Balance voxel domain resolution with bake time",
                            prompt = "Doubling a 3D smoke domain's voxel resolution from 64 to 128 increases total voxel cells by how much?",
                            options = listOf("2x", "4x", "8x (2 × 2 × 2 in 3D)", "It stays the same"),
                            correctOptionIndex = 2,
                            hint = "Volume scales across all three dimensions: X, Y, and Z.",
                            explanation = "3D grids cube the scale factor: 2³ = 8× more voxels and memory."
                        )
                    ),
                    practicalProject = PracticalProjectSpec(
                        id = "proj_vfx_burst", title = "Stylised Magic Spell / Spark Burst", departmentId = 10,
                        difficulty = DifficultyTier.DIFFICULT, recommendedSoftware = "Blender (Free) or Prisma3D",
                        freeVsPaidNote = "Full physics simulations require desktop Blender (free); Prisma3D can animate stylized low-poly burst meshes.",
                        description = "Create a vibrant glowing spell burst with expanding shockwave ring, orbiting sparks, and fading smoke puffs.",
                        deliverables = listOf("3 progression frames (Ignite, Peak, Dissipate)"),
                        rubricChecklist = listOf("Clear timing pop", "Color gradient from hot core to cool smoke", "Readable silhouette against backdrop")
                    ),
                    revisionTips = listOf("Always cache/bake heavy physics before final render.", "3D domain resolution scales cubically—test low-res first!")
                )
            )
        ),
        Department(
            id = 11,
            title = "Rendering & Production",
            subtitle = "Render engines, optimization, storyboarding, sound sync & short films",
            topics = listOf(
                "Rendering Fundamentals", "Render Optimisation", "Materials & Lighting Optimisation",
                "Video Editing & Sound Sync", "Storyboarding & Scene Planning", "Short-Film Production"
            ),
            primarySoftware = "Prisma3D / Blender / CapCut / DaVinci Resolve",
            accentColorHex = 0xFF6366F1,
            lessons = listOf(
                Lesson(
                    id = "d11_l1",
                    departmentId = 11,
                    orderIndex = 1,
                    title = "Storyboarding, Image Sequences, Denoising & Sound Sync",
                    durationMinutes = 12,
                    xpReward = 50,
                    conceptSimple = "Rendering turns your 3D scene into pixels—either fast real-time rasterization (Eevee/Mobile) or photorealistic ray-traced paths (Cycles). Never render a long animation directly to MP4! Render an Image Sequence (PNG frames) so if your device crashes at Frame 400, you resume from Frame 401 instead of losing everything. Then assemble frames with music and sound effects in a video editor.",
                    diagramSpec = DiagramSpec(
                        type = DiagramType.CAMERA_FOV,
                        title = "Production Shot & Storyboard Pipeline",
                        caption = "Storyboard -> Animatic -> Layout -> Animation -> Lighting -> PNG Sequence -> Sound & Edit.",
                        interactivePrimitive = "Cube"
                    ),
                    realExamples = listOf(
                        "AI Denoising lets you render cleanly at 64 samples instead of 512 samples—saving 80% render time.",
                        "Sound effects (footsteps, whooshes) hit 1–2 frames BEFORE or ON visual impact for maximum punch."
                    ),
                    mobileWorkflowSteps = listOf(
                        WorkflowStep(1, "Prisma3D (Android Free)", "Use lower sample settings for test playblasts first, then export final frames cleanly.", "Full HD export free; 4K/no-watermark requires Pro."),
                        WorkflowStep(2, "Blender + Video Editor (Free)", "Render PNG sequence, then compile + sync audio tracks in Blender VSE or DaVinci Resolve Free.", "Zero cost production pipeline.")
                    ),
                    guidedTaskPrompt = "Sketch a 3-panel storyboard (Wide Establishing -> Medium Action -> Close-Up Reaction) for a 10-second short.",
                    guidedTaskHint = "Never start animating before knowing your camera cuts.",
                    guidedTaskChecklist = listOf(
                        "Use Image Sequences (PNG) for long renders",
                        "Enable Denoiser + Adaptive Sampling to cut render times",
                        "Sync sound effects tightly to visual impacts"
                    ),
                    masteryQuiz = listOf(
                        ExerciseItem(
                            id = "d11_l1_q1", departmentId = 11, topic = "Production Safety", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Explain why professionals render Image Sequences instead of MP4",
                            prompt = "Why should long 3D animations be rendered as PNG image sequences instead of a single MP4 video file?",
                            options = listOf(
                                "If power fails or the app crashes partway, already-rendered frames are safe and you resume without starting over",
                                "PNG files automatically add voice acting",
                                "MP4 files cannot store color",
                                "PNG skips lighting calculations"
                            ),
                            correctOptionIndex = 0,
                            hint = "Imagine crashing at frame 499 of a 500-frame render.",
                            explanation = "Image sequences preserve every completed frame independently and preserve lossless quality for editing."
                        ),
                        ExerciseItem(
                            id = "d11_l1_q2", departmentId = 11, topic = "Render Optimization", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Eliminate grainy render noise efficiently",
                            prompt = "Your path-traced render looks grainy in dark corners, and raising samples from 64 to 1024 makes it take 10 hours. What is a smarter speedup?",
                            options = listOf(
                                "Enable Denoising + Adaptive Sampling and use clean area lighting",
                                "Delete all materials",
                                "Double polygon count everywhere",
                                "Disable camera lens"
                            ),
                            correctOptionIndex = 0,
                            hint = "Modern denoisers clean smooth shadow gradients at moderate sample counts.",
                            explanation = "Denoising combined with smart light placement removes noise in a fraction of the render time."
                        ),
                        ExerciseItem(
                            id = "d11_l1_q3", departmentId = 11, topic = "Storyboarding", difficulty = DifficultyTier.BASIC,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Sequence production phases cleanly",
                            prompt = "When should you create a Storyboard & Animatic?",
                            options = listOf(
                                "At the very start before modelling and animating detailed assets",
                                "After rendering the final movie",
                                "Only for audio engineers",
                                "Never—just guess camera angles at the end"
                            ),
                            correctOptionIndex = 0,
                            hint = "Plan shots early so you never model props the camera never sees.",
                            explanation = "Storyboards and animatics lock timing and camera framing before expensive 3D production begins."
                        ),
                        ExerciseItem(
                            id = "d11_l1_q4", departmentId = 11, topic = "Sound Sync", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Align Foley audio with animation frames",
                            prompt = "During a walk cycle, when should each footstep sound effect peak?",
                            options = listOf(
                                "On the Contact frame when the heel strikes the ground",
                                "Halfway through the air at High-Point",
                                "5 seconds later",
                                "Only on frame 0"
                            ),
                            correctOptionIndex = 0,
                            hint = "Watch the sole touch the floor.",
                            explanation = "Landing Foley hits directly on (or 1 frame into) the Contact impact frame."
                        )
                    ),
                    practicalProject = PracticalProjectSpec(
                        id = "proj_short_film", title = "10-Second 3-Shot Short Animated Movie", departmentId = 11,
                        difficulty = DifficultyTier.ADVANCED, recommendedSoftware = "Prisma3D / Blender + Mobile Editor",
                        freeVsPaidNote = "Free tools throughout.",
                        description = "Combine a wide establishing shot, character action shot, and reaction close-up into a 10-second short film with music and Foley sound effects.",
                        deliverables = listOf("3-panel storyboard", "Edited video or sequential keyframes with audio plan"),
                        rubricChecklist = listOf("Clear beginning-middle-end micro story", "Consistent lighting across cuts", "Synced audio impacts")
                    ),
                    revisionTips = listOf("Always render PNG sequences for multi-second animations.", "Only detail what the camera actually sees!")
                )
            )
        ),
        Department(
            id = 12,
            title = "Advanced & Professional Workflows",
            subtitle = "Production pipelines, procedural nodes, optimization & portfolio",
            topics = listOf(
                "Advanced Topology & Poles", "Procedural Workflows", "Animation Pipelines",
                "Performance & LODs", "Asset Management", "Production Planning", "Portfolio & Showreel"
            ),
            primarySoftware = "Blender / Godot / Unreal / Unity",
            accentColorHex = 0xFF0F766E,
            lessons = listOf(
                Lesson(
                    id = "d12_l1",
                    departmentId = 12,
                    orderIndex = 1,
                    title = "Procedural Workflows, LOD Optimization & Studio Pipelines",
                    durationMinutes = 14,
                    xpReward = 60,
                    conceptSimple = "Professional studios link hundreds of artists using clean pipelines: Asset Naming Conventions (CHR_Hero_v03), Linked Libraries/Instancing (so editing one tree updates 500 forest trees at once), LODs (Level of Detail meshes that swap to simpler geometry far from the camera), and Procedural Node Workflows (generating bricks, foliage, and motion variations mathematically without hand-placing every piece).",
                    diagramSpec = DiagramSpec(
                        type = DiagramType.COORDINATE_AXES,
                        title = "Instancing, LOD & Pipeline Architecture",
                        caption = "Inspect origin pivot accuracy—crucial when linking modular assets across studio scenes.",
                        interactivePrimitive = "Octahedron"
                    ),
                    realExamples = listOf(
                        "Games swap a 15,000-triangle LOD0 hero crate at 2m to a 300-triangle LOD2 crate at 40m.",
                        "A great 60-second demo reel puts your absolute strongest shot in the first 5 seconds."
                    ),
                    mobileWorkflowSteps = listOf(
                        WorkflowStep(1, "Blender Geometry Nodes (Desktop Free)", "Build non-destructive procedural fences, rocks, or scatter fields with node graphs.", "Free industry-grade procedural system."),
                        WorkflowStep(2, "Pomana Portfolio (In-App)", "Curate your 3–5 best finished renders & breakdowns with software tags and clear role credits.", "Keep only your strongest, cleanly lit work.")
                    ),
                    guidedTaskPrompt = "Audit one of your saved projects: check naming, triangle count, pivot origin, and lighting polish.",
                    guidedTaskHint = "Recruiters judge a portfolio by its weakest piece—keep it focused and crisp.",
                    guidedTaskChecklist = listOf(
                        "Use consistent naming (PROP_Chair_LOD0)",
                        "Explain LODs and Instancing memory savings",
                        "Lead portfolio/showreel with strongest finished work"
                    ),
                    masteryQuiz = listOf(
                        ExerciseItem(
                            id = "d12_l1_q1", departmentId = 12, topic = "Performance Optimization", difficulty = DifficultyTier.ADVANCED,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Use LODs and Instancing to hit frame-rate targets",
                            prompt = "A huge forest scene drops from 60fps to 12fps. Which two pipeline optimizations keep visual quality high while restoring 60fps?",
                            options = listOf(
                                "Mesh Instancing (sharing one mesh data block) + LODs (simpler meshes at distance)",
                                "Duplicating unique meshes 5,000 times and increasing texture resolution to 8K",
                                "Removing all UV maps",
                                "Animating on Ones at 120fps"
                            ),
                            correctOptionIndex = 0,
                            hint = "Don't load 5,000 unique copies of the same tree or render 50,000 leaves at 200m distance.",
                            explanation = "Instancing saves RAM/VRAM and LODs slash distant triangle rendering cost."
                        ),
                        ExerciseItem(
                            id = "d12_l1_q2", departmentId = 12, topic = "Procedural Workflows", difficulty = DifficultyTier.ADVANCED,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Understand non-destructive procedural benefits",
                            prompt = "What is the biggest advantage of a Procedural (Node-Based) city or foliage generator over manual placement?",
                            options = listOf(
                                "You can change street width, density, or building height non-destructively at any time via parameters",
                                "It prevents rendering shadows",
                                "It disables animation",
                                "It only works in 2D"
                            ),
                            correctOptionIndex = 0,
                            hint = "Change a single slider and 1,000 objects adapt automatically.",
                            explanation = "Procedural graphs stay editable and scale effortlessly across large productions."
                        ),
                        ExerciseItem(
                            id = "d12_l1_q3", departmentId = 12, topic = "Portfolio Development", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Curate a competitive 3D portfolio",
                            prompt = "When preparing a 3D animation portfolio or demo reel for studios, what is the golden rule?",
                            options = listOf(
                                "Show only your top 3–6 strongest pieces, starting with your absolute best work in the first 5 seconds",
                                "Include every rough half-finished test you ever made to make it 15 minutes long",
                                "Hide your name and software breakdown",
                                "Use shaky unlit viewport screenshots only"
                            ),
                            correctOptionIndex = 0,
                            hint = "Quality beats quantity every single time.",
                            explanation = "Studios review reels quickly—lead with your strongest shot and curate ruthlessly."
                        ),
                        ExerciseItem(
                            id = "d12_l1_q4", departmentId = 12, topic = "Pipeline Hygiene", difficulty = DifficultyTier.EXPERT,
                            type = ExerciseType.FIND_ERROR,
                            learningObjective = "Enforce frozen transforms and clean origin pivots before rigging/export",
                            prompt = "An animator links a sword prop to a character's hand, but the sword scales weirdly into a stretched rectangle when rotated. What pipeline step was skipped before export?",
                            options = listOf(
                                "Applying/Freezing non-uniform Scale transforms (so Scale = 1, 1, 1) and centering the pivot at the grip",
                                "Turning off compositing bloom",
                                "Lowering camera focal length",
                                "Adding more subdivisions"
                            ),
                            correctOptionIndex = 0,
                            hint = "Unapplied non-uniform parent scale skews child rotations.",
                            explanation = "Always Apply/Freeze Scale to (1,1,1) before rigging, parenting, or exporting assets."
                        )
                    ),
                    practicalProject = PracticalProjectSpec(
                        id = "proj_master_reel", title = "Capstone Production Pack & Portfolio Breakdown", departmentId = 12,
                        difficulty = DifficultyTier.EXPERT, recommendedSoftware = "Blender + Pomana Portfolio",
                        freeVsPaidNote = "100% open-source workflow.",
                        description = "Package a hero prop or animated character shot with wireframe topology breakdown, UV/material sheet, lighting setup, and final polished render.",
                        deliverables = listOf("Final polished render", "Wireframe + Shader breakdown uploaded to Portfolio"),
                        rubricChecklist = listOf("Frozen (1,1,1) transforms", "Clean quad edge flow", "Studio-grade 3-point lighting & presentation")
                    ),
                    revisionTips = listOf("Freeze/Apply transforms before rigging or exporting!", "Curate your portfolio ruthlessly—quality over quantity.")
                )
            )
        )
    )

    val softwareGuides: List<SoftwareToolGuide> = listOf(
        SoftwareToolGuide(
            id = "prisma3d",
            name = "Prisma3D",
            platforms = "Android & iOS",
            costModel = "Free Core (Optional Pro for 4K / no-watermark export)",
            bestFor = "Beginners learning 3D coordinates, primitive modeling, rigging, and keyframe animation directly on a phone.",
            paidLimitations = "Free tier includes full modeling, bone rigging, lights, cameras, and timeline animation; Pro removes render watermark and unlocks higher export presets.",
            quickStartSteps = listOf(
                "Tap '+' -> Cube/Sphere to spawn primitives at (0,0,0).",
                "Use Move / Rotate / Scale gizmo handles (RGB = XYZ).",
                "Switch to Modeling mode at top to extrude faces and add loop cuts.",
                "Switch to Animation mode to set keyframes along the bottom timeline."
            )
        ),
        SoftwareToolGuide(
            id = "tinkercad",
            name = "Tinkercad",
            platforms = "Android Tablet / iPad / Web Browser",
            costModel = "100% Free (Autodesk account)",
            bestFor = "Absolute beginners learning 3D space, primitive combining, boolean holes, architecture, and 3D-printable props.",
            paidLimitations = "No paid paywall—completely free! Does not support skeletal rigging or keyframe character animation.",
            quickStartSteps = listOf(
                "Drag geometric primitives onto the blue Workplane.",
                "Turn any shape into a 'Hole' and Group (Ctrl+G) to carve clean cutouts.",
                "Use Align tool to center parts accurately before exporting STL/OBJ."
            )
        ),
        SoftwareToolGuide(
            id = "blockbench",
            name = "Blockbench",
            platforms = "Android / Web / Windows / Mac / Linux",
            costModel = "100% Free & Open Source",
            bestFor = "Low-poly game characters, stylized voxel props, automatic UV unwrapping, texture painting, and simple bone animations.",
            paidLimitations = "Zero paid limits. Focused on low-poly & cuboid styles rather than high-poly organic sculpting.",
            quickStartSteps = listOf(
                "Choose Generic Model or Low-Poly template.",
                "Build cleanly with cubes & pivot groups, then click 'Generate Texture' for instant UVs.",
                "Switch to Paint & Animate tabs inside the same lightweight app."
            )
        ),
        SoftwareToolGuide(
            id = "sculpt_nomad",
            name = "Sculpt+ & Nomad Sculpt",
            platforms = "Android & iOS",
            costModel = "Sculpt+: Free (ads) | Nomad: Free Tryout, Paid (~$15–20 one-time) to Save/Export",
            bestFor = "Organic character heads, creatures, clay brushing, and anatomical blocking on touchscreens.",
            paidLimitations = "Nomad Sculpt's free trial lets you practice all brushes, symmetry, and lighting, but saving/exporting requires one-time purchase. Sculpt+ allows free saves.",
            quickStartSteps = listOf(
                "Enable X-Symmetry before touching the starting sphere.",
                "Use Move/Grab at low resolution for silhouette, then Clay & Crease for details.",
                "Keep MatCap lighting active to read surface planes cleanly."
            )
        ),
        SoftwareToolGuide(
            id = "blender",
            name = "Blender",
            platforms = "Windows, Mac & Linux",
            costModel = "100% Free & Open Source Forever",
            bestFor = "Complete professional 3D pipeline: sub-D modeling, sculpting, UV unwrapping, shader nodes, rigging, Graph Editor, VFX simulations, and Cycles/Eevee rendering.",
            paidLimitations = "Zero paywalls! Requires a computer/laptop (mouse + keyboard recommended).",
            quickStartSteps = listOf(
                "Learn G (Grab/Move), R (Rotate), S (Scale) + X/Y/Z axis locks.",
                "Use Tab for Edit Mode and I (Insert Keyframe) on the timeline.",
                "Render animations as PNG Image Sequences before video editing."
            )
        )
    )
}

object CurriculumCatalog {
    val allDepartments: List<Department> =
        CurriculumCatalogPart1.departmentsPart1 + CurriculumCatalogPart2.departmentsPart2

    val allSoftwareGuides: List<SoftwareToolGuide> = CurriculumCatalogPart2.softwareGuides

    fun getDepartment(id: Int): Department? = allDepartments.find { it.id == id }

    fun getLesson(lessonId: String): Lesson? =
        allDepartments.flatMap { it.lessons }.find { it.id == lessonId }

    val allProjects: List<PracticalProjectSpec> =
        allDepartments.flatMap { it.lessons }.map { it.practicalProject } + listOf(
            PracticalProjectSpec(
                id = "proj_extra_furniture_chair", title = "Modern Lounge Armchair", departmentId = 2,
                difficulty = DifficultyTier.EASY, recommendedSoftware = "Prisma3D / Tinkercad",
                freeVsPaidNote = "100% free.",
                description = "Model curved wooden armrests and plush bevelled cushions with distinct wood vs fabric roughness.",
                deliverables = listOf("3/4 render with floor shadow"),
                rubricChecklist = listOf("Proportions fit seated scale", "Wood glossy vs fabric matte")
            ),
            PracticalProjectSpec(
                id = "proj_extra_cartoon_hero", title = "Cartoon Explorer Character", departmentId = 8,
                difficulty = DifficultyTier.ADVANCED, recommendedSoftware = "Blockbench / Blender",
                freeVsPaidNote = "Free tools supported.",
                description = "Design an expressive cartoon mascot with oversized boots, clean quad loops, and 3 facial expression blendshapes.",
                deliverables = listOf("Turnaround sheet + posed expression render"),
                rubricChecklist = listOf("Appealing big-medium-small proportions", "Readable facial expression")
            )
        )
}
