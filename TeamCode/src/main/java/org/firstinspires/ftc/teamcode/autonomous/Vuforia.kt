package org.firstinspires.ftc.teamcode.autonomous

import android.content.Context
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables

/**
 * Includes necessary abstractions of the Vuforia API for use by AutonomousMain.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
class Vuforia constructor(opMode: OpMode) {
    companion object {
        // Setup procedures
        fun createLocalizer(context: Context, useCameraMonitor: Boolean): VuforiaLocalizer {

            // Initialize parameters for passing to the constructor of the localizer.
            val params = if (useCameraMonitor) {
                VuforiaLocalizer.Parameters(context.resources.getIdentifier(
                        "cameraMonitorViewId", "id", context.packageName)
                )
            } else {
                VuforiaLocalizer.Parameters()
            }
            params.vuforiaLicenseKey = "AbMQqsf/////AAAAGaPkhxQD4kw5s9Z8fi7zmCkf8bWukMiWXj1fDay0ukQ99WGt7m6apGGxRWFIrlX1ZQhhW4w3L//I9eNMcxJo5tmJufAAL07zp128UEtHHGNCfz349+M36iiyjanscpBwgktOxCDbIuJdg/PwPWBsVSiwCpGgtOc8ly/VJgCVbAMg9LLWZkpi2ejrVr0taXybw6BejzHkv3MJ8nvWPVPHbVxtMYo3AWa6Sl2PoTgjd8/pKwpIcgpUaLStc92tfigl1i/ZXemq7tkTcWIJkODajW6XeFklq/6U7fKXUbh1qzaRhBa0xpITjbfAeZlzspLWE/y8r2FABSWbJnQZ0/Phvi2aHlY/o0N8M8OGu8fqqQiu"
            params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK

            return ClassFactory.createVuforiaLocalizer(params)
        }

        fun loadTrackables(localizer: VuforiaLocalizer): VuforiaTrackables =
                localizer.loadTrackablesFromAsset("RelicVuMark")

        fun loadTemplate(trackables: VuforiaTrackables): VuforiaTrackable {
            val template = trackables[0]
            template.name = "RelicVuMarkTemplate"
            return template
        }
    }

    // Constructs (initializes) the Vuforia trackables & localizer
    val localizer = createLocalizer(opMode.hardwareMap.appContext, true)
    private val trackables = loadTrackables(localizer)
    private val template = loadTemplate(trackables)

    fun startTracking() =
            trackables.activate()

    fun readVuMark() =
            RelicRecoveryVuMark.from(template)

    fun stopTracking() =
            trackables.deactivate()
}