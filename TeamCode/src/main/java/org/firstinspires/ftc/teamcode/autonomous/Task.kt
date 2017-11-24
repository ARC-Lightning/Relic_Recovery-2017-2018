package org.firstinspires.ftc.teamcode.autonomous

/**
 * An annotation class that allows tasks (functions) to have metadata, aiding decisions.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class Task(
        /**
         * The magnitude of the benefit as a result of executing this task successfully.
         * Value should be in range of [0, 1].
         *
         * This value is used extensively in the decision-making process as the immediate reward.
         */
        val priority: Double,

        /**
         * The risk for failure in executing this task, both detected and undetected
         * (regardless of return value)
         * Value should be in range of [0, 1].
         *
         * This value should derive from the outcomes of test sessions to thoroughly optimize the
         * decisions that the robot makes in Autonomous.
         *
         * This value is used extensively in the decision-making process as the probability value.
         */
        val reliability: Double
)