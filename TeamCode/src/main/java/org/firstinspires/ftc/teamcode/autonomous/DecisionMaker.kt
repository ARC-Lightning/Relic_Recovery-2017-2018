package org.firstinspires.ftc.teamcode.autonomous

import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.io.Hardware
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Defines the strategic logic used by the robot to decide which task to accomplish in a given state.
 * Employs recursive Depth-First Search while making decisions.
 *
 * @author Michael Peng
 * For team: 4410
 *
 * FIRST - Gracious Professionalism
 */
class DecisionMaker(tasks: KClass<AutonomousBase.Tasks> = AutonomousBase.Tasks::class) {

    // CONFIGURATIONS
    companion object {
        /**
         * The discount factor ("urgency coefficient"), [[0, 1]], describes how the preference
         * factor of tasks decay when they are planned to be executed in the future.
         *
         * This lambda determines the discount factor, optionally dynamically with dependence on how
         * many seconds have passed since the Autonomous period began.
         */
        // The current lambda is linear, equivalent to f(x)=1 - x/30, where x is seconds elapsed
        val discountFactor = { timer: ElapsedTime -> 1.0 - (timer.seconds() / periodDuration) }

        /**
         * The duration of the Autonomous period in seconds.
         */
        private val periodDuration = 30.0
    }

    /**
     * Tasks available, immutable, reflective of all members of the given Tasks class that has the
     * Task annotation class.
     */
    private val options = tasks.members.filter { it.annotations.any { it is Task } }
            .map { it.name to it }
            .toMap()

    /**
     * Gets the Task annotation of the callable itself.
     *
     * @throws NullPointerException If the callable does not have a Task annotation
     */
    private val KCallable<*>.metadata: Task get() = findAnnotation()!!

    /**
     * A running timer, which is used for dynamic discountFactor determination.
     * The timer starts when the DecisionMaker is instantiated.
     */
    private val timer: ElapsedTime = ElapsedTime()

    /**
     * A to-do list of tasks that are not executed yet.
     * (Guaranteed to be a subset of options)
     */
    val nextTasks = options.keys.toMutableSet()

    /**
     * Whether there are remaining tasks in the to-do list.
     *
     * @see nextTasks
     */
    val isDone: Boolean get() = nextTasks.isEmpty()

    /**
     * Performs the task with the given name (of a member of the given `Tasks` class), passing it the
     * given instance of AutonomousMain as a parameter.
     *
     * @param name Name of the task the caller wishes to execute
     * @param opMode The instance to pass to the task method
     * @return `null` when name parameter not in options or when task threw an exception,
     *      otherwise whether the task succeeded
     */
    fun doTask(name: String, opMode: AutonomousBase): Boolean? {
        return if (nextTasks.contains(name)) {
            try {

                // Reflection requires casting; if name in nextTasks, then it's in options
                val result = options.getValue(name).call(AutonomousBase.Tasks, opMode) as Boolean

                // If the task was successful, then remove it from the set of remaining ones
                if (result)
                    nextTasks.remove(name)

                result
            } catch (exc: Exception) {
                Hardware.telemetry.error("doTask exception: ${exc.message}")
                null
            }
        } else null
    }

    // All possible states following the given state, analogous to the children of a tree node
    private fun nextStates(tasksPending: Set<String>) = tasksPending.map { tasksPending - it }

    private fun getMetadataFromName(name: String) = options[name]!!.metadata

    /**
     * Depending on the reliability and priority of each task, pick the next task to execute.
     *
     * @return `null` if there are no tasks remaining to do, otherwise the name of the chosen task
     */
    fun nextTask(): String? {
        fun value(state: Set<String>, depth: Int = 0): Double = when (state.size) {
        // This case should never be reached unless nextTasks is empty, which means everything
        //   has been accomplished and the robot is finished for autonomous.
        // Ideally it would be Double.MAX_VALUE, but due to suspected issues with overflowing
        //   while calculating the sum, it has been reduced to 10 million.
            0 -> 10_000_000.0
            1 -> {
                val nextAction = getMetadataFromName(state.first())

                nextAction.priority *
                        nextAction.reliability *
                        Math.pow(discountFactor(timer), depth.toDouble())
            }
            else -> nextStates(state).map { value(it, depth + 1) }.sum()
        }

        if (this.isDone)
            return null

        return nextTasks
                .maxBy { value(nextTasks - it) }
    }
}