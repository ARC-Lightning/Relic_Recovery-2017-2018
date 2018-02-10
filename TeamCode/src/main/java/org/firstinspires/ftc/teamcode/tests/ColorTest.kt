package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.ColorSensor

/**
 * A utility program that reads color values from the sensor and writes them to telemetry.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
@TeleOp(name = "ColorTest", group = "Pragmaticos")
class ColorTest : OpMode() {

    // The sensor
    lateinit var sensor: ColorSensor

    override fun init() {
        sensor = hardwareMap.get(ColorSensor::class.java, "JewelSensor")
        // Set the LED in the beginning
        sensor.enableLed(true)
    }

    override fun loop() {
        with(telemetry) {
            addData("Red", sensor.red())
            addData("Green", sensor.green())
            addData("Blue", sensor.blue())
            addData("Alpha", sensor.alpha())

            update()
        }
    }
}