package com.example.data.model

enum class DiagramType {
    COORDINATE_AXES,
    MESH_ANATOMY,
    UV_UNWRAP,
    THREE_POINT_LIGHTING,
    IK_FK_RIG,
    KEYFRAME_CURVE,
    CAMERA_FOV,
    PARTICLE_SIM
}

data class DiagramSpec(
    val type: DiagramType,
    val title: String,
    val caption: String,
    val interactivePrimitive: String = "Cube"
)

data class WorkflowStep(
    val stepNumber: Int,
    val software: String,
    val instruction: String,
    val freeNote: String
)

data class PracticalProjectSpec(
    val id: String,
    val title: String,
    val departmentId: Int,
    val difficulty: DifficultyTier,
    val recommendedSoftware: String,
    val freeVsPaidNote: String,
    val description: String,
    val deliverables: List<String>,
    val rubricChecklist: List<String>
)

data class Lesson(
    val id: String,
    val departmentId: Int,
    val orderIndex: Int,
    val title: String,
    val durationMinutes: Int,
    val xpReward: Int,
    val conceptSimple: String,
    val diagramSpec: DiagramSpec,
    val realExamples: List<String>,
    val mobileWorkflowSteps: List<WorkflowStep>,
    val guidedTaskPrompt: String,
    val guidedTaskHint: String,
    val guidedTaskChecklist: List<String>,
    val masteryQuiz: List<ExerciseItem>,
    val practicalProject: PracticalProjectSpec,
    val revisionTips: List<String>
)

data class Department(
    val id: Int,
    val title: String,
    val subtitle: String,
    val topics: List<String>,
    val primarySoftware: String,
    val accentColorHex: Long,
    val lessons: List<Lesson>
)

data class SoftwareToolGuide(
    val id: String,
    val name: String,
    val platforms: String,
    val costModel: String,
    val bestFor: String,
    val paidLimitations: String,
    val quickStartSteps: List<String>
)
