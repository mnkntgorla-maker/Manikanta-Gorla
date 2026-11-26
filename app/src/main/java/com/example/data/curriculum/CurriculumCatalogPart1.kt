package com.example.data.curriculum

import com.example.data.model.*

internal object CurriculumCatalogPart1 {

    val departmentsPart1: List<Department> = listOf(
        Department(
            id = 1,
            title = "3D Fundamentals",
            subtitle = "Space, coordinates, meshes, and viewport navigation",
            topics = listOf(
                "2D vs 3D Space", "X, Y & Z Coordinates", "Vertices, Edges & Faces",
                "Meshes & Topology", "Transformations", "3D Workspace Navigation"
            ),
            primarySoftware = "Prisma3D / Tinkercad (Free)",
            accentColorHex = 0xFF16A34A,
            lessons = listOf(
                Lesson(
                    id = "d1_l1",
                    departmentId = 1,
                    orderIndex = 1,
                    title = "X, Y & Z Coordinates and 3D Space",
                    durationMinutes = 8,
                    xpReward = 30,
                    conceptSimple = "A flat drawing only has Width (X) and Height (Y). Adding Depth (Z) turns flat shapes into 3D objects you can walk around. Think of X as left-right, Y as up-down (in Prisma3D/Unity/Maya), and Z as forward-backward toward your camera.",
                    diagramSpec = DiagramSpec(
                        type = DiagramType.COORDINATE_AXES,
                        title = "Interactive 3D Coordinate Gizmo",
                        caption = "Red = X (Left/Right), Green = Y (Up/Down), Blue = Z (Depth). Drag the viewport to orbit around origin (0, 0, 0).",
                        interactivePrimitive = "Cube"
                    ),
                    realExamples = listOf(
                        "A movie poster on a wall is 2D (X, Y); a toy figurine on your desk is 3D (X, Y, Z).",
                        "In mobile 3D games, moving a character forward changes its Z coordinate while jumping raises Y."
                    ),
                    mobileWorkflowSteps = listOf(
                        WorkflowStep(1, "Prisma3D (Android Free)", "Open a new project and tap the '+' cube icon to spawn a Cube at (0, 0, 0).", "Free core modelling & viewport controls; watermark removal in pro export."),
                        WorkflowStep(2, "Prisma3D (Android Free)", "Drag the green Y arrow upward to lift the cube off the grid floor.", "All transform gizmos are 100% free."),
                        WorkflowStep(3, "Tinkercad (Browser/Tablet)", "Orbit with one-finger drag outside the shape and pinch to zoom.", "Completely free Autodesk web/iPad tool.")
                    ),
                    guidedTaskPrompt = "Open the 3D Lab or Prisma3D, place a cube at origin (0,0,0), then raise it +2 units on Y and shift +1 unit on X.",
                    guidedTaskHint = "Green arrow controls Y (height); Red arrow controls X (horizontal).",
                    guidedTaskChecklist = listOf(
                        "Identify Red (X), Green (Y), and Blue (Z) arrows",
                        "Orbit 360 degrees around the object without losing sight of the floor grid",
                        "Move object strictly along one axis at a time"
                    ),
                    masteryQuiz = listOf(
                        ExerciseItem(
                            id = "d1_l1_q1", departmentId = 1, topic = "Coordinates", difficulty = DifficultyTier.BASIC,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Distinguish 3D axes by color and direction",
                            prompt = "In standard 3D gizmos like Prisma3D, which axis color usually controls vertical height (Y)?",
                            options = listOf("Red", "Green", "Blue", "Yellow"),
                            correctOptionIndex = 1,
                            hint = "Remember RGB maps to XYZ: Red=X, Green=Y, Blue=Z.",
                            explanation = "RGB corresponds alphabetically to XYZ: Red = X, Green = Y (vertical up in Y-up apps), Blue = Z."
                        ),
                        ExerciseItem(
                            id = "d1_l1_q2", departmentId = 1, topic = "Coordinates", difficulty = DifficultyTier.BASIC,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Calculate resulting 3D position after translation",
                            prompt = "A sphere starts at (2, 1, -3). You move it +4 on X and -1 on Y. Where is it now?",
                            options = listOf("(6, 0, -3)", "(-2, 2, -3)", "(6, 2, 1)", "(2, 5, -4)"),
                            correctOptionIndex = 0,
                            hint = "Add +4 to the first number and subtract 1 from the second.",
                            explanation = "(2+4, 1-1, -3) = (6, 0, -3). Z stays unchanged."
                        ),
                        ExerciseItem(
                            id = "d1_l1_q3", departmentId = 1, topic = "Navigation", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.FIND_ERROR,
                            learningObjective = "Prevent accidental off-plane object placement",
                            prompt = "A student drags the center white circle of the gizmo instead of an axis arrow and wonders why their chair floated behind the house. What went wrong?",
                            options = listOf(
                                "Dragging the center moves freely parallel to the screen view across all 3 axes at once",
                                "The cube had too many polygons",
                                "Lighting was turned off",
                                "The camera FOV was set to 50mm"
                            ),
                            correctOptionIndex = 0,
                            hint = "Screen-space free drag changes X, Y, and Z simultaneously.",
                            explanation = "Always pull individual axis arrows (X, Y, or Z) for accurate 3D placement instead of free-dragging."
                        ),
                        ExerciseItem(
                            id = "d1_l1_q4", departmentId = 1, topic = "Transforms", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Understand Scale vs Translate vs Rotate",
                            prompt = "Which transformation stretches a cube into a tall skyscraper without moving its base center?",
                            options = listOf("Translate on Y", "Rotate on X", "Scale on Y", "Orbit camera"),
                            correctOptionIndex = 2,
                            hint = "Resizing along a single axis changes proportions.",
                            explanation = "Scaling along the Y axis increases height while keeping the pivot center stationary."
                        )
                    ),
                    practicalProject = PracticalProjectSpec(
                        id = "proj_cube_sphere", title = "3D Cube & Sphere Pedestal", departmentId = 1,
                        difficulty = DifficultyTier.BASIC, recommendedSoftware = "Prisma3D or Tinkercad",
                        freeVsPaidNote = "100% doable on free tier of Prisma3D or Tinkercad.",
                        description = "Build a balanced 3D pedestal by stacking a scaled cube base, a thin pillar, and a sphere resting cleanly on top with zero floating gaps.",
                        deliverables = listOf("Viewport screenshot from front and 3/4 angle", "Verify sphere Y bottom touches pillar Y top"),
                        rubricChecklist = listOf("Base centered near (0,0,0)", "No floating or intersecting parts", "Clean 3/4 camera framing")
                    ),
                    revisionTips = listOf("Always inspect your model from two angles (front + side) before assuming two parts touch.", "Use RGB = XYZ to never forget axis colors.")
                ),
                Lesson(
                    id = "d1_l2",
                    departmentId = 1,
                    orderIndex = 2,
                    title = "Vertices, Edges, Faces & Clean Topology",
                    durationMinutes = 10,
                    xpReward = 35,
                    conceptSimple = "Every 3D mesh is a net made of three building blocks: Vertices (single corner dots), Edges (straight lines connecting 2 vertices), and Faces/Polygons (flat surfaces stretched between 3 or 4 edges). Good topology uses clean 4-sided quads so models bend smoothly during animation.",
                    diagramSpec = DiagramSpec(
                        type = DiagramType.MESH_ANATOMY,
                        title = "Mesh Anatomy Inspector",
                        caption = "Switch to Wireframe + Vertices in the 3D Lab to inspect corners (vertices), lines (edges), and surfaces (faces).",
                        interactivePrimitive = "Cube"
                    ),
                    realExamples = listOf(
                        "A simple dice cube has 8 vertices, 12 edges, and 6 square faces.",
                        "Pixar-style cartoon faces arrange loops of 4-sided quads around the eyes and mouth so smiles deform cleanly."
                    ),
                    mobileWorkflowSteps = listOf(
                        WorkflowStep(1, "Prisma3D (Android Free)", "Select a Cube and switch to Modeling Mode (vertex/edge/face icons at top).", "Mesh edit mode is free in Prisma3D."),
                        WorkflowStep(2, "Prisma3D (Android Free)", "Select a top Face and tap Extrude to pull out a second story.", "Extrude, inset, and loop cut are core free tools."),
                        WorkflowStep(3, "Blender (Desktop Free)", "Press Tab for Edit Mode and 1/2/3 to toggle Vertex, Edge, Face.", "100% open-source & free forever.")
                    ),
                    guidedTaskPrompt = "Inspect a cube and an octahedron in the 3D Lab Wireframe mode, counting visible vertices and faces.",
                    guidedTaskHint = "A cube has 8 corners; an octahedron has 6 corners and 8 triangular faces.",
                    guidedTaskChecklist = listOf(
                        "Toggle Wireframe + Vertices mode",
                        "Count vertices on a Cube vs Pyramid",
                        "Understand why 4-sided quads are preferred over N-gons"
                    ),
                    masteryQuiz = listOf(
                        ExerciseItem(
                            id = "d1_l2_q1", departmentId = 1, topic = "Mesh Anatomy", difficulty = DifficultyTier.BASIC,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Count mesh components on a cube",
                            prompt = "How many vertices and edges does a standard un-subdivided 3D cube have?",
                            options = listOf("6 vertices, 8 edges", "8 vertices, 12 edges", "12 vertices, 6 edges", "4 vertices, 6 edges"),
                            correctOptionIndex = 1,
                            hint = "Count the corners (4 on top, 4 on bottom) and edges connecting them.",
                            explanation = "A standard cube has 8 vertices (corners), 12 edges, and 6 quad faces."
                        ),
                        ExerciseItem(
                            id = "d1_l2_q2", departmentId = 1, topic = "Topology", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Choose optimal polygon type for deformation",
                            prompt = "Why do character animators prefer 4-sided polygons (quads) over 5+ sided N-gons?",
                            options = listOf(
                                "Quads subdivide cleanly into neat edge loops and bend predictably at joints",
                                "Quads require zero texture memory",
                                "N-gons cannot be rendered in color",
                                "Quads automatically add skeletal bones"
                            ),
                            correctOptionIndex = 0,
                            hint = "Think about how knees, elbows, and smiles crease when animated.",
                            explanation = "Quads preserve predictable edge flow and subdivide smoothly without pinching artifacts."
                        ),
                        ExerciseItem(
                            id = "d1_l2_q3", departmentId = 1, topic = "Topology", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.FIND_ERROR,
                            learningObjective = "Detect non-manifold or overlapping geometry",
                            prompt = "When rotating the camera, two faces on a wall flicker rapidly in stripes (Z-fighting). What is the cause?",
                            options = listOf(
                                "Two duplicated faces sit at the exact same 3D coordinates overlapping each other",
                                "The vertex count is too low",
                                "The camera is too far away from the sun",
                                "The rig has IK enabled"
                            ),
                            correctOptionIndex = 0,
                            hint = "Two coplanar surfaces fight for the depth buffer.",
                            explanation = "Duplicate overlapping geometry causes Z-fighting. Delete or offset one face cleanly."
                        ),
                        ExerciseItem(
                            id = "d1_l2_q4", departmentId = 1, topic = "Mesh Anatomy", difficulty = DifficultyTier.BASIC,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Define an Edge in 3D geometry",
                            prompt = "What connects exactly two vertices in a 3D mesh?",
                            options = listOf("A shader", "An edge", "A keyframe", "A normal map"),
                            correctOptionIndex = 1,
                            hint = "Two dots connected by a straight line.",
                            explanation = "An edge is the straight segment linking two vertices."
                        )
                    ),
                    practicalProject = PracticalProjectSpec(
                        id = "proj_furniture", title = "Stylised Study Desk & Chair", departmentId = 1,
                        difficulty = DifficultyTier.EASY, recommendedSoftware = "Prisma3D or Tinkercad",
                        freeVsPaidNote = "Free tools only.",
                        description = "Model a minimalist desk and chair using clean extruded cubes and bevelled edges with tidy topology.",
                        deliverables = listOf("Screenshot in shaded + wireframe view", "Clean object hierarchy names (DeskTop, Leg_FL, ChairSeat)"),
                        rubricChecklist = listOf("Consistent proportions", "No overlapping duplicate faces", "Cleanly grounded at Y=0")
                    ),
                    revisionTips = listOf("V-E+F = 2 (Euler's formula for closed meshes: 8 - 12 + 6 = 2).", "Avoid N-gons on curved or deforming surfaces.")
                )
            )
        ),
        Department(
            id = 2,
            title = "3D Modelling",
            subtitle = "Primitives, hard-surface, low-poly, environments & characters",
            topics = listOf(
                "Primitive Modelling", "Hard-Surface Modelling", "Organic Modelling",
                "Low-Poly vs High-Poly", "Character & Robot Blocking", "Environments, Vehicles & Architecture"
            ),
            primarySoftware = "Prisma3D / Tinkercad / Blockbench",
            accentColorHex = 0xFF0EA5E9,
            lessons = listOf(
                Lesson(
                    id = "d2_l1",
                    departmentId = 2,
                    orderIndex = 1,
                    title = "Hard-Surface Extrusion & Low-Poly Blockouts",
                    durationMinutes = 12,
                    xpReward = 40,
                    conceptSimple = "Before sculpting tiny bolts or wrinkles, professionals block out large silhouettes using simple primitives (cubes, cylinders, spheres) and hard-surface operations like Extrude, Bevel, and Loop Cut. Low-poly keeps polygon counts lean for mobile games; high-poly adds bevels and curved details for close-up film shots.",
                    diagramSpec = DiagramSpec(
                        type = DiagramType.MESH_ANATOMY,
                        title = "Low-Poly Rocket Blockout",
                        caption = "Inspect the Low-Poly Rocket in 3D Lab to see how cylinders, cones, and fins combine cleanly.",
                        interactivePrimitive = "Rocket"
                    ),
                    realExamples = listOf(
                        "Game props like crates, sci-fi doors, and low-poly vehicles start as 20-polygon primitive blockouts.",
                        "Adding a small Bevel to sharp hard-surface edges catches specular highlights and makes models look realistic."
                    ),
                    mobileWorkflowSteps = listOf(
                        WorkflowStep(1, "Prisma3D (Android Free)", "Block out a robot torso with a scaled Cube, then use Loop Cut to divide chest & waist.", "Loop Cut & Extrude are free."),
                        WorkflowStep(2, "Blockbench (Android/Web Free)", "Great for crisp low-poly game characters, voxel props, and UV-ready cubes.", "100% free & open source."),
                        WorkflowStep(3, "Tinkercad (Tablet/Web Free)", "Use Hole shapes + Group to carve clean architectural windows and vehicle wheel arches.", "Boolean Group/Hole is free.")
                    ),
                    guidedTaskPrompt = "Select the Rocket in 3D Lab, view Wireframe mode, and note how few faces are needed for a readable silhouette.",
                    guidedTaskHint = "Readability comes from big primary shapes first, not tiny noise.",
                    guidedTaskChecklist = listOf(
                        "Block out Primary -> Secondary -> Tertiary shapes",
                        "Keep triangle budget light for mobile targets",
                        "Bevel sharp edges so light catches the silhouette"
                    ),
                    masteryQuiz = listOf(
                        ExerciseItem(
                            id = "d2_l1_q1", departmentId = 2, topic = "Hard-Surface", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Understand why bevels improve realism",
                            prompt = "Why do hard-surface artists add slight bevels (chamfers) to 90-degree metal edges?",
                            options = listOf(
                                "Real-world objects rarely have razor-sharp edges; bevels catch light highlights",
                                "Bevels delete all UV maps",
                                "Bevels convert meshes into bone rigs",
                                "Bevels lower frame rate to 12fps"
                            ),
                            correctOptionIndex = 0,
                            hint = "Look at the edge of your phone or desk—light catches a tiny curved rim.",
                            explanation = "Bevelled edges create natural specular rim highlights that immediately sell solid form."
                        ),
                        ExerciseItem(
                            id = "d2_l1_q2", departmentId = 2, topic = "Modelling Workflow", difficulty = DifficultyTier.BASIC,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Follow proper silhouette-first blocking order",
                            prompt = "What is the best order when modelling a sci-fi vehicle?",
                            options = listOf(
                                "Model tiny bolts first, then build tires",
                                "Block out major chassis proportions first, then medium panels, then small details",
                                "Paint textures before creating any geometry",
                                "Animate the wheels before shaping the body"
                            ),
                            correctOptionIndex = 1,
                            hint = "Primary -> Secondary -> Tertiary.",
                            explanation = "Always nail big proportions first so you don't waste hours detailing a crooked silhouette."
                        ),
                        ExerciseItem(
                            id = "d2_l1_q3", departmentId = 2, topic = "Low-Poly vs High-Poly", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Choose polygon budgets appropriately",
                            prompt = "Which project needs strict low-poly optimization?",
                            options = listOf(
                                "A static printed poster",
                                "A real-time mobile 60fps multiplayer game character",
                                "A single offline 4K still render",
                                "A 2D hand-drawn storyboard"
                            ),
                            correctOptionIndex = 1,
                            hint = "Mobile GPUs must redraw every frame in under 16 milliseconds.",
                            explanation = "Real-time mobile games require lean low-poly geometry to maintain smooth 60fps performance."
                        ),
                        ExerciseItem(
                            id = "d2_l1_q4", departmentId = 2, topic = "Operations", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Identify the Extrude modelling operation",
                            prompt = "Which tool pulls a selected polygon face outward to create new connected side walls?",
                            options = listOf("Extrude", "Merge Vertices", "Flip Normals", "Bake Ambient Occlusion"),
                            correctOptionIndex = 0,
                            hint = "Think of pushing clay out through a mold.",
                            explanation = "Extrude generates new depth and side faces from an existing face or edge selection."
                        )
                    ),
                    practicalProject = PracticalProjectSpec(
                        id = "proj_house_vehicle", title = "Cozy Low-Poly House & Mini Vehicle", departmentId = 2,
                        difficulty = DifficultyTier.INTERMEDIATE, recommendedSoftware = "Prisma3D / Blockbench / Tinkercad",
                        freeVsPaidNote = "Free tools cover all primitive, boolean, and extrusion steps.",
                        description = "Create a stylized low-poly cottage (pitched roof, chimney, doorway) and park a compact stylized car or robot beside it.",
                        deliverables = listOf("Turntable render or 2 viewport angles", "Under 3,000 triangles total"),
                        rubricChecklist = listOf("Readable silhouette", "Clean window/door insets", "Proper scale between door and vehicle")
                    ),
                    revisionTips = listOf("Zoom out to thumbnail size—if the silhouette reads clearly, your blockout works!", "Use mirror symmetry for vehicles and characters.")
                )
            )
        ),
        Department(
            id = 3,
            title = "Digital Sculpting",
            subtitle = "Organic forms, human & creature anatomy, detailing workflows",
            topics = listOf(
                "Sculpting Fundamentals", "Human Anatomy", "Face & Body Sculpting",
                "Creature Sculpting", "Character Detailing", "Sculpting & Retopo Workflows"
            ),
            primarySoftware = "Nomad Sculpt (Free tier) / Sculpt+ / Blender",
            accentColorHex = 0xFFF43F5E,
            lessons = listOf(
                Lesson(
                    id = "d3_l1",
                    departmentId = 3,
                    orderIndex = 1,
                    title = "Clay Brushes, Anatomy Planes & Retopology",
                    durationMinutes = 11,
                    xpReward = 40,
                    conceptSimple = "Digital sculpting feels like shaping virtual clay with pressure-sensitive brushes (Clay, Move, Crease, Smooth). You work from low subdivision levels for big anatomical landmarks (skull, ribcage, pelvis) before raising resolution for pores and scales—then retopologize into clean quads for animation.",
                    diagramSpec = DiagramSpec(
                        type = DiagramType.MESH_ANATOMY,
                        title = "Sculpting Subdivision Hierarchy",
                        caption = "Notice how faceted low-poly forms establish clear planes before smoothing.",
                        interactivePrimitive = "Octahedron"
                    ),
                    realExamples = listOf(
                        "Creature horns and dragon scales are sculpted at high subdivision, then baked into a Normal Map onto a low-poly game mesh.",
                        "Sculpting a human head starts with the jaw wedge and eye sockets—never eyelids first."
                    ),
                    mobileWorkflowSteps = listOf(
                        WorkflowStep(1, "Sculpt+ / Nomad Sculpt (Android)", "Start with a Sphere, turn on X-Symmetry, and use the Move brush to block the cranium and jawline.", "Sculpt+ is free with ads; Nomad Sculpt trial lets you practice all brushes (paid unlock needed for project save/export)."),
                        WorkflowStep(2, "Blender Sculpt Mode (Desktop Free)", "Use Clay Strips + Grab brushes with Dyntopo or Voxel Remesh.", "Completely free & unrestricted.")
                    ),
                    guidedTaskPrompt = "Trace the 3 major body masses (head, ribcage, pelvis) and practice planar blocking before smoothing.",
                    guidedTaskHint = "Use Symmetry (Mirror X) when sculpting faces and creatures.",
                    guidedTaskChecklist = listOf("Enable X-axis mirror symmetry", "Block big anatomical planes at low resolution", "Understand why high-poly sculpts need retopology before rigging")
                    ,
                    masteryQuiz = listOf(
                        ExerciseItem(
                            id = "d3_l1_q1", departmentId = 3, topic = "Sculpting Workflow", difficulty = DifficultyTier.BASIC,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Order sculpting detail levels correctly",
                            prompt = "Why should you block out major forms at low polygon resolution before adding fine skin wrinkles?",
                            options = listOf(
                                "Low-resolution clay moves smoothly for big proportions without lumpy surface noise",
                                "Wrinkles cannot be erased",
                                "Symmetry only works on wrinkles",
                                "High resolution disables lighting"
                            ),
                            correctOptionIndex = 0,
                            hint = "Large smooth strokes shape big forms cleanly.",
                            explanation = "Working low-to-high prevents lumpy anatomy and keeps silhouette edits effortless."
                        ),
                        ExerciseItem(
                            id = "d3_l1_q2", departmentId = 3, topic = "Retopology", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Explain why raw sculpts must be retopologized for animation",
                            prompt = "You sculpted a 1.5 million triangle monster. Why shouldn't you rig and animate that raw sculpt directly?",
                            options = listOf(
                                "Dense disorganized triangles lag the viewport and pinch badly when bent",
                                "Monsters cannot have skeletons",
                                "Triangles cannot receive shadows",
                                "It will turn the mesh invisible"
                            ),
                            correctOptionIndex = 0,
                            hint = "Think of performance and clean joint edge loops.",
                            explanation = "Animation requires a light, quad-based retopologized mesh with baked normal maps carrying the sculpt detail."
                        ),
                        ExerciseItem(
                            id = "d3_l1_q3", departmentId = 3, topic = "Brushes", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Identify core sculpting brushes",
                            prompt = "Which brush sharpens crisp folds like eyelids, clothing creases, or dragon scales?",
                            options = listOf("Smooth", "Crease / Pinch", "Inflate", "Flatten"),
                            correctOptionIndex = 1,
                            hint = "Think of carving a crisp groove into clay.",
                            explanation = "Crease carves sharp valleys while pinching neighboring vertices together."
                        ),
                        ExerciseItem(
                            id = "d3_l1_q4", departmentId = 3, topic = "Anatomy", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.FIND_ERROR,
                            learningObjective = "Recognize symmetry and proportion issues",
                            prompt = "A sculptor forgets to enable X-Mirror symmetry while shaping a character's eyes, causing mismatched eye sockets. What is the fastest fix?",
                            options = listOf(
                                "Mirror/symmetrize the good side across the X axis and keep X-Mirror active",
                                "Delete the whole computer",
                                "Turn down key light intensity",
                                "Increase FPS from 24 to 60"
                            ),
                            correctOptionIndex = 0,
                            hint = "Most sculpting apps include a Symmetrize tool.",
                            explanation = "Use Symmetrize (-X to +X or vice versa) to restore symmetry instantly, then keep Mirror active."
                        )
                    ),
                    practicalProject = PracticalProjectSpec(
                        id = "proj_creature_bust", title = "Stylised Fantasy Creature Bust", departmentId = 3,
                        difficulty = DifficultyTier.INTERMEDIATE, recommendedSoftware = "Sculpt+ (Free) or Blender (Free)",
                        freeVsPaidNote = "Nomad Sculpt Android free version restricts export/saving; use Sculpt+ or desktop Blender for full free export.",
                        description = "Sculpt an expressive creature bust focusing on strong brow ridge, snout/jaw planes, and asymmetrical horn accents added at the very end.",
                        deliverables = listOf("Front + 3/4 matcap screenshot", "Clear primary & secondary forms"),
                        rubricChecklist = listOf("Clean planar silhouettes", "No lumpy clay artifacts", "Readable eye & jaw landmarks")
                    ),
                    revisionTips = listOf("Check your sculpt as a flat dark silhouette—good anatomy reads even without shading!", "Always keep anatomy landmarks anchored to bone structure.")
                )
            )
        ),
        Department(
            id = 4,
            title = "Materials & Texturing",
            subtitle = "PBR shaders, UV unwrapping, roughness, metalness & styles",
            topics = listOf(
                "Material Properties", "Colours & Shaders", "UV Mapping",
                "Texture Painting", "Metal, Glass, Wood & Fabric", "Realistic vs Stylised Materials"
            ),
            primarySoftware = "Prisma3D / Blender / Texture Paint",
            accentColorHex = 0xFF8B5CF6,
            lessons = listOf(
                Lesson(
                    id = "d4_l1",
                    departmentId = 4,
                    orderIndex = 1,
                    title = "PBR Shaders (Roughness & Metallic) + UV Mapping",
                    durationMinutes = 11,
                    xpReward = 40,
                    conceptSimple = "Modern 3D uses PBR (Physically Based Rendering). Two sliders control most realism: Metallic (0 for plastic/wood/skin, 1 for gold/steel) and Roughness (0 = mirror-glossy, 1 = chalky matte). UV Mapping is like peeling an orange flat onto a 2D paper sheet so painted textures wrap smoothly around your 3D mesh.",
                    diagramSpec = DiagramSpec(
                        type = DiagramType.UV_UNWRAP,
                        title = "Material Roughness & Metallic Tester",
                        caption = "Experiment with Roughness & Metallic sliders in the 3D Lab to see highlights tighten or soften in real time!",
                        interactivePrimitive = "Cube"
                    ),
                    realExamples = listOf(
                        "Brushed aluminum: Metallic = 1.0, Roughness = 0.35.",
                        "Clay figurine: Metallic = 0.0, Roughness = 0.75.",
                        "UV seams are hidden under arms or inside trouser legs just like real shirt stitching."
                    ),
                    mobileWorkflowSteps = listOf(
                        WorkflowStep(1, "Prisma3D (Android Free)", "Open Material settings on any mesh: adjust Base Color, Metallic, and Roughness.", "Basic PBR materials & color textures are free."),
                        WorkflowStep(2, "Blockbench (Free)", "Auto-generate a clean 2D UV texture template and paint pixel or brush textures directly on the 3D model.", "100% free 3D texture painting on mobile/web.")
                    ),
                    guidedTaskPrompt = "In the 3D Lab, set Roughness to 0.15 (glossy) vs 0.90 (matte) and toggle Metallic to observe highlight changes.",
                    guidedTaskHint = "Low roughness creates tight bright specular hotspots; high roughness spreads light softly.",
                    guidedTaskChecklist = listOf(
                        "Test Roughness 0.1 vs 0.85",
                        "Compare Metallic 0 (dielectric) vs 1 (conductor)",
                        "Understand UV seams as 2D unwrap cuts"
                    ),
                    masteryQuiz = listOf(
                        ExerciseItem(
                            id = "d4_l1_q1", departmentId = 4, topic = "PBR Materials", difficulty = DifficultyTier.BASIC,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Understand Roughness parameter behavior",
                            prompt = "Which Roughness value makes a wooden table look polished and glossy?",
                            options = listOf("Roughness = 0.95", "Roughness = 0.15", "Metallic = 1.0", "UV = 0.0"),
                            correctOptionIndex = 1,
                            hint = "Lower roughness means smoother microscopic surface.",
                            explanation = "Low roughness (around 0.1–0.25) creates sharp glossy reflections; high roughness looks chalky matte."
                        ),
                        ExerciseItem(
                            id = "d4_l1_q2", departmentId = 4, topic = "UV Mapping", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Define UV mapping purpose",
                            prompt = "What is the purpose of UV unwrapping a 3D character?",
                            options = listOf(
                                "To flatten the 3D surface into 2D coordinates so image textures align cleanly without stretching",
                                "To add gravity physics to hair",
                                "To double polygon count",
                                "To speed up keyframe playback"
                            ),
                            correctOptionIndex = 0,
                            hint = "Think of unwrapping a candy bar wrapper flat on a table.",
                            explanation = "UV coordinates map 2D image pixels (U, V) onto 3D mesh faces."
                        ),
                        ExerciseItem(
                            id = "d4_l1_q3", departmentId = 4, topic = "Materials", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.FIND_ERROR,
                            learningObjective = "Catch incorrect Metallic value on non-metal",
                            prompt = "A student sets Metallic = 1.0 on a cartoon character's skin shader, making the character look like a chrome statue. What should Metallic be for skin, wood, or fabric?",
                            options = listOf("0.0 (dielectric / non-metal)", "0.75", "1.5", "2.0"),
                            correctOptionIndex = 0,
                            hint = "In PBR, materials are almost always binary: either 0 (non-metal) or 1 (metal).",
                            explanation = "Non-conductors like skin, wood, plastic, and cloth should have Metallic = 0.0."
                        ),
                        ExerciseItem(
                            id = "d4_l1_q4", departmentId = 4, topic = "Shaders", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Choose appropriate texture map for surface bumps",
                            prompt = "Which texture map fakes fine surface bumps like brick grain or fabric weave without adding real polygons?",
                            options = listOf("Normal / Bump Map", "Timeline Graph", "IK Pole Target", "Audio Track"),
                            correctOptionIndex = 0,
                            hint = "RGB purple-blue map that bends surface light normals.",
                            explanation = "Normal maps perturb surface normals per pixel to render crisp micro-detail cheaply."
                        )
                    ),
                    practicalProject = PracticalProjectSpec(
                        id = "proj_material_balls", title = "4-Shader Material Study (Wood, Gold, Glass, Fabric)", departmentId = 4,
                        difficulty = DifficultyTier.EASY, recommendedSoftware = "Prisma3D or Blender",
                        freeVsPaidNote = "Prisma3D supports free color/metallic/roughness; transparency/refraction glass works best in Blender (free).",
                        description = "Shade 4 spheres side by side representing matte fabric, warm satin wood, polished gold, and translucent glass.",
                        deliverables = listOf("Single lit screenshot showing all 4 spheres", "List of Roughness & Metallic values used"),
                        rubricChecklist = listOf("Clear visual contrast between matte and glossy", "Metallic=1 only on metal", "Visible specular reflections")
                    ),
                    revisionTips = listOf("Non-metals: Metallic = 0. Pure metals: Metallic = 1.", "Place UV seams where viewers rarely look (underarms, back corners).")
                )
            )
        ),
        Department(
            id = 5,
            title = "Lighting & Cameras",
            subtitle = "Three-point lighting, shadows, composition, FOV & depth of field",
            topics = listOf(
                "Light Types", "Soft vs Hard Shadows", "Three-Point Lighting",
                "Cinematic Lighting", "Camera Controls", "Composition & Rule of Thirds",
                "Camera Movement", "Depth of Field"
            ),
            primarySoftware = "Prisma3D / Blender",
            accentColorHex = 0xFFF59E0B,
            lessons = listOf(
                Lesson(
                    id = "d5_l1",
                    departmentId = 5,
                    orderIndex = 1,
                    title = "Three-Point Lighting & Cinematic Camera Framing",
                    durationMinutes = 10,
                    xpReward = 40,
                    conceptSimple = "Even a masterpiece model looks flat under a single front flash. Three-Point Lighting uses: 1) Key Light (main bright side light creating form), 2) Fill Light (softer opposite light lifting harsh black shadows), and 3) Rim/Back Light (a crisp halo separating the character from the background). Pair this with intentional camera Focal Length (35mm wide for action, 85mm telephoto for flattering portraits) and Rule-of-Thirds composition.",
                    diagramSpec = DiagramSpec(
                        type = DiagramType.THREE_POINT_LIGHTING,
                        title = "Interactive 3-Point Light & Camera Studio",
                        caption = "Toggle Key, Fill, and Rim lights in the 3D Lab, rotate Key azimuth, and switch Rule-of-Thirds + Focal Length!",
                        interactivePrimitive = "Rocket"
                    ),
                    realExamples = listOf(
                        "Warm orange Key Light + cool blue Fill Light creates instant Pixar-grade color contrast.",
                        "A wide 24mm lens exaggerates perspective near the camera; an 85mm lens compresses background nicely."
                    ),
                    mobileWorkflowSteps = listOf(
                        WorkflowStep(1, "Prisma3D (Android Free)", "Add 3 Light objects: Spot/Directional as Key (1.0 intensity), Point as Fill (0.35), Spot behind as Rim.", "Multiple lights & shadows are free."),
                        WorkflowStep(2, "Prisma3D (Android Free)", "Add a Camera object, tap 'Look Through', and keyframe smooth dolly/orbit moves.", "Camera keyframing is free.")
                    ),
                    guidedTaskPrompt = "Open 3D Lab -> Lighting & Camera tabs. Turn off Fill and Rim first, then enable them one by one to see form emerge.",
                    guidedTaskHint = "Watch how the dark edge pops out when Rim Light is turned on.",
                    guidedTaskChecklist = listOf(
                        "Compare 1 light vs 3-point lighting",
                        "Rotate Key Light angle across 0-360 degrees",
                        "Toggle Rule of Thirds & adjust Camera FOV"
                    ),
                    masteryQuiz = listOf(
                        ExerciseItem(
                            id = "d5_l1_q1", departmentId = 5, topic = "Three-Point Lighting", difficulty = DifficultyTier.BASIC,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Identify the role of the Rim light",
                            prompt = "What is the main job of the Rim (Back) light in three-point lighting?",
                            options = listOf(
                                "Create a bright edge outline that separates the subject from the background",
                                "Replace all shadows completely",
                                "Animate the character's walk cycle",
                                "Decrease polygon count"
                            ),
                            correctOptionIndex = 0,
                            hint = "Think of a glowing halo along the shoulders and hair.",
                            explanation = "The Rim light sits behind the subject and traces its silhouette so it doesn't blend into dark backgrounds."
                        ),
                        ExerciseItem(
                            id = "d5_l1_q2", departmentId = 5, topic = "Camera Lenses", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Select focal length for character portraits vs wide action",
                            prompt = "Which camera focal length gives natural, flattering proportions for a character close-up portrait without fish-eye distortion?",
                            options = listOf("14mm ultra-wide", "85mm portrait telephoto", "8mm fisheye", "2mm pinhole"),
                            correctOptionIndex = 1,
                            hint = "Longer focal lengths reduce nose/chin perspective distortion.",
                            explanation = "50mm–85mm lenses compress perspective naturally and avoid wide-angle facial distortion."
                        ),
                        ExerciseItem(
                            id = "d5_l1_q3", departmentId = 5, topic = "Shadows", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Control shadow softness via light source size",
                            prompt = "How do you make shadows softer and gentler in 3D lighting?",
                            options = listOf(
                                "Increase the physical size/radius of the area light",
                                "Shrink the light to an infinitely tiny point",
                                "Set Roughness to 0",
                                "Increase FPS to 60"
                            ),
                            correctOptionIndex = 0,
                            hint = "Think of an overcast cloudy sky vs harsh midday sun.",
                            explanation = "Larger light emitters wrap around surfaces and produce soft, diffused shadow edges."
                        ),
                        ExerciseItem(
                            id = "d5_l1_q4", departmentId = 5, topic = "Lighting Error", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.FIND_ERROR,
                            learningObjective = "Diagnose flat washed-out lighting",
                            prompt = "A scene has Key Light = 1.0 and Fill Light = 1.0 at equal brightness from opposite sides, making a character sphere look totally flat like a 2D circle. Why?",
                            options = listOf(
                                "There is no light-to-shadow ratio contrast; Fill should usually be 25–45% of Key",
                                "The sphere has too many triangles",
                                "The camera needs Depth of Field",
                                "Fill light must be green"
                            ),
                            correctOptionIndex = 0,
                            hint = "Shadows create the illusion of 3D volume.",
                            explanation = "Equal Key and Fill erase shadows. Keep Fill weaker (25–45% of Key) to preserve sculptural depth."
                        )
                    ),
                    practicalProject = PracticalProjectSpec(
                        id = "proj_cinematic_scene", title = "Dramatic Three-Point Character Showcase", departmentId = 5,
                        difficulty = DifficultyTier.INTERMEDIATE, recommendedSoftware = "Prisma3D or Blender",
                        freeVsPaidNote = "Fully achievable in Prisma3D free.",
                        description = "Light your earlier robot or creature bust with warm Key, cool Fill, bright Rim, and frame it using Rule of Thirds.",
                        deliverables = listOf("Final camera render", "Lighting breakdown top-view screenshot"),
                        rubricChecklist = listOf("Clear Key/Fill contrast ratio", "Visible Rim separation", "Subject placed on Rule-of-Thirds intersection")
                    ),
                    revisionTips = listOf("Warm Key + Cool Fill = instant cinematic depth.", "Bigger light source = softer shadows.")
                )
            )
        ),
        Department(
            id = 6,
            title = "Rigging",
            subtitle = "Skeletons, bones, IK vs FK, skinning, weight painting & face rigs",
            topics = listOf(
                "Skeleton Systems", "Bones & Joints", "IK & FK Switching",
                "Skinning & Binding", "Weight Painting", "Facial Rigging", "Character Controllers"
            ),
            primarySoftware = "Prisma3D / Blender",
            accentColorHex = 0xFF10B981,
            lessons = listOf(
                Lesson(
                    id = "d6_l1",
                    departmentId = 6,
                    orderIndex = 1,
                    title = "Skeleton Hierarchies, FK vs IK & Weight Painting",
                    durationMinutes = 12,
                    xpReward = 45,
                    conceptSimple = "Rigging turns a static 3D statue into a movable puppet. Bones form a parent-child hierarchy (Pelvis -> Spine -> Chest -> UpperArm -> Forearm -> Hand). With FK (Forward Kinematics), you rotate each joint down the chain—great for sweeping arm arcs. With IK (Inverse Kinematics), you move the hand or foot goal and the elbow/knee bends automatically—essential for keeping feet planted on the ground! Weight painting defines how strongly each bone pulls nearby vertices.",
                    diagramSpec = DiagramSpec(
                        type = DiagramType.IK_FK_RIG,
                        title = "Joint Chain & Pivot Hierarchy",
                        caption = "Understand how parent rotation moves child bones while IK anchors end effectors.",
                        interactivePrimitive = "Cylinder"
                    ),
                    realExamples = listOf(
                        "Walking feet ALWAYS use IK so feet stick solidly to the floor without sliding.",
                        "Swinging arms during a run or waving goodbye often use FK for smooth natural arcs."
                    ),
                    mobileWorkflowSteps = listOf(
                        WorkflowStep(1, "Prisma3D (Android Free)", "Create a Bone chain from Pelvis up to Head, then branch Left/Right arms and legs.", "Skeleton creation & skinning are free in Prisma3D."),
                        WorkflowStep(2, "Prisma3D (Android Free)", "Bind mesh to skeleton and test rotating the Shoulder bone; adjust bone weights if torso vertices pull weirdly.", "Weight adjustment works directly on mobile.")
                    ),
                    guidedTaskPrompt = "Trace a human arm hierarchy and decide when to switch between FK and IK.",
                    guidedTaskHint = "Planting hands on a table = IK; throwing a ball arc = FK.",
                    guidedTaskChecklist = listOf(
                        "Identify root bone (Pelvis/Hips)",
                        "Contrast FK (rotate parent->child) vs IK (position end goal)",
                        "Check weight normalization (vertex weights sum to 1.0)"
                    ),
                    masteryQuiz = listOf(
                        ExerciseItem(
                            id = "d6_l1_q1", departmentId = 6, topic = "IK vs FK", difficulty = DifficultyTier.EASY,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Choose IK vs FK for grounded locomotion",
                            prompt = "When animating a character doing push-ups with hands locked onto the floor, which rig mode should you use for the arms?",
                            options = listOf("IK (Inverse Kinematics)", "FK (Forward Kinematics)", "UV Unwrapping", "Vertex Extrusion"),
                            correctOptionIndex = 0,
                            hint = "The hands must stay pinned to a fixed world position while the chest moves.",
                            explanation = "IK pins the hands at the floor target and solves shoulder/elbow angles automatically."
                        ),
                        ExerciseItem(
                            id = "d6_l1_q2", departmentId = 6, topic = "Weight Painting", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.FIND_ERROR,
                            learningObjective = "Diagnose stray vertex weights",
                            prompt = "When the character lifts their right arm, part of their ribcage stretches out toward the elbow like chewing gum. What needs fixing?",
                            options = listOf(
                                "Weight painting: remove stray UpperArm_R weight from the ribcage vertices and assign them to Spine",
                                "Increase camera FOV",
                                "Turn off Rim light",
                                "Export at 60fps"
                            ),
                            correctOptionIndex = 0,
                            hint = "The arm bone is influencing vertices that belong to the torso.",
                            explanation = "Clean up weight painting so torso vertices are weighted 100% to spine bones, not arm bones."
                        ),
                        ExerciseItem(
                            id = "d6_l1_q3", departmentId = 6, topic = "Skeleton Hierarchy", difficulty = DifficultyTier.BASIC,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Identify the root bone of a biped rig",
                            prompt = "Which bone is normally the parent root of a human character skeleton so moving it shifts the entire body?",
                            options = listOf("Index Finger", "Pelvis / Hips", "Left Eyelid", "Toe Tip"),
                            correctOptionIndex = 1,
                            hint = "Your center of gravity sits between your legs and spine.",
                            explanation = "The Pelvis/Hips bone sits at the center of mass and drives both spine and leg hierarchies."
                        ),
                        ExerciseItem(
                            id = "d6_l1_q4", departmentId = 6, topic = "Facial Rigging", difficulty = DifficultyTier.INTERMEDIATE,
                            type = ExerciseType.MULTIPLE_CHOICE,
                            learningObjective = "Understand Blendshapes / Shape Keys for expressions",
                            prompt = "What technique morphs a character's neutral face mesh smoothly into pre-sculpted smile, blink, and phoneme poses?",
                            options = listOf("Blendshapes / Shape Keys", "Boolean Difference", "Ambient Occlusion", "Voxel Remesh"),
                            correctOptionIndex = 0,
                            hint = "Morph targets interpolate vertex positions from 0% to 100%.",
                            explanation = "Blendshapes (Shape Keys) blend cleanly between sculpted facial expressions and lip-sync shapes."
                        )
                    ),
                    practicalProject = PracticalProjectSpec(
                        id = "proj_robot_rig", title = "Articulated Robot Rig & Action Pose", departmentId = 6,
                        difficulty = DifficultyTier.INTERMEDIATE, recommendedSoftware = "Prisma3D or Blender",
                        freeVsPaidNote = "Free in both Prisma3D and Blender.",
                        description = "Rig a segmented robot with a root hip bone, spine, head, and 2-bone arms/legs, then pose it in a heroic landing stance.",
                        deliverables = listOf("Bone hierarchy screenshot", "Heroic posed render without mesh tearing"),
                        rubricChecklist = listOf("Clean Pelvis root", "Pivots centered on real mechanical hinges", "Zero stray stretching vertices")
                    ),
                    revisionTips = listOf("Place joint pivots strictly inside the anatomical bend center.", "IK for planted feet/hands; FK for sweeping arcs.")
                )
            )
        )
    )
}
