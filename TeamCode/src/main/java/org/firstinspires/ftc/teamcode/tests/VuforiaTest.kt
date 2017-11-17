package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.autonomous.IVuforia
import org.firstinspires.ftc.teamcode.autonomous.Vuforia

/**
 * Provides a basic demonstration / test of the capabilities of Vuforia at recognizing the VuMark.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
@TeleOp(name = "Vuforia Test", group = "Pragmaticos")
class VuforiaTest : OpMode() {

    // Quick reference for the unacquainted
    val reference = listOf(
            "Introducing" to "...",
            "THE VUFORIA™" to " TESTER",
            "-----" to "-----",
            "Just let the desired camera on the robot controller" to "see the VuMark",
            "And watch" to "the magic happen",
            "-----" to "-----",
            "Disclaimer" to "May be the most addictive test here",
            "-----" to "-----"
    )

    // Vuforia's wrapper
    var vuforia: IVuforia? = null

    override fun init() {
        reference.forEach { (fst, snd) ->
            telemetry.addData(fst, snd)
        }

        vuforia = Vuforia(this)
        vuforia!!.startTracking()
        telemetry.addData("Vuforia", "Initialized")

        telemetry.update()
    }

    override fun loop() {
        with(vuforia!!) {
            telemetry.addData("I'm seeing", readVuMark())
        }
        telemetry.update()
    }

    override fun stop() {
        vuforia!!.stopTracking()
    }
}