package org.firstinspires.ftc.teamcode.autonomous

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Defines the strategic logic used by the robot to decide which task to accomplish in a given state.
 * Roughly models the Markov Decision Process (MDP).
 *
 * @author Michael Peng
 * For team: 4410
 *
 * FIRST - Gracious Professionalism
 */
class DecisionMaker(tasks: KClass<AutonomousMain.Tasks> = AutonomousMain.Tasks::class) {

    // CONFIGURATIONS
    companion object {
        val discountFactor = 0.8
    }

    /**
     * Tasks available, immutable
     */
    val options = tasks.members.filter { it.annotations.any { it is Task } }
            .map { it.name to it }
            .toMap()

    private val KCallable<*>.metadata: Task get() = findAnnotation()!!

    /**
     * Guaranteed to be a subset of options
     */
    val nextTasks = options.keys.toMutableSet()

    val isDone: Boolean get() = nextTasks.isEmpty()

    /**
     * Returns null when:
     *  - Name parameter not in options
     */
    fun doTask(name: String, opMode: AutonomousMain): Boolean? {
        return if (nextTasks.contains(name)) {
            try {

                // Reflection requires casting
                val result = options[name]!!.call(opMode) as Boolean

                // If the task was successful, then remove it from pending ones
                if (result)
                    nextTasks.remove(name)

                result
            } catch (exc: Exception) {
                opMode.hardware.telemetry.error("doTask exception: ${exc.message}")
                null
            }
        } else null
    }

    private fun nextStates(tasksPending: Set<String>) = tasksPending.map { tasksPending - it }

    fun nextTask(): String? {
        fun value(state: Set<String>, depth: Int = 0): Double = when (state.size) {
        // This case should never be reached unless nextTasks is empty, which means everything
        //   has been accomplished and the robot is finished for autonomous.
            0 -> 10_000_000.0
            1 -> {
                val nextAction = options[state.first()]!!.metadata
                nextAction.priority * nextAction.reliability * Math.pow(discountFactor, depth.toDouble())
            }
            else -> nextStates(state).map { value(it, depth + 1) }.average()
        }

        if (this.isDone)
            return null

        return nextTasks.map { it to value(nextTasks - it) }.maxBy { it.second }!!.first
    }
}