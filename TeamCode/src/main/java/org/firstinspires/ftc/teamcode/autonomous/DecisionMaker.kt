package org.firstinspires.ftc.teamcode.autonomous

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
class DecisionMaker(tasks: KClass<AutonomousMain.Tasks> = AutonomousMain.Tasks::class) {

    // CONFIGURATIONS
    companion object {
        /**
         * The "urgency coefficient", [[0, 1]], which describes how the preference factor of tasks
         * decay when they are executed in the future.
         *
         * A discount factor of 1 means the millionth task executed has the same preference as the
         * same task executed right now.
         *
         * A discount factor of 0 means the second task executed has a preference of 0.
         */
        val discountFactor = 0.8
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
    fun doTask(name: String, opMode: AutonomousMain): Boolean? {
        return if (nextTasks.contains(name)) {
            try {

                // Reflection requires casting; if name in nextTasks, then it's in options
                val result = options.getValue(name).call(opMode) as Boolean

                // If the task was successful, then remove it from the set of remaining ones
                if (result)
                    nextTasks.remove(name)

                result
            } catch (exc: Exception) {
                opMode.hardware.telemetry.error("doTask exception: ${exc.message}")
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
        //   while calculating the average, it has been reduced to 10 million.
            0 -> 10_000_000.0
            1 -> {
                val nextAction = getMetadataFromName(state.first())

                nextAction.priority *
                        nextAction.reliability *
                        Math.pow(discountFactor, depth.toDouble())
            }
            else -> nextStates(state).map { value(it, depth + 1) }.average()
        }

        if (this.isDone)
            return null

        return nextTasks
                .maxBy { value(nextTasks - it) }
    }
}