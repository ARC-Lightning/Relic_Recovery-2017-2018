package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.ColorSensor

/**
 * Created by michael on 11/26/17.
 * TODO document
 */
@TeleOp(name = "ColorTest", group = "Pragmaticos")
class ColorTest : OpMode() {

    // The sensor
    lateinit var sensor: ColorSensor

    override fun init() {
        sensor = hardwareMap.colorSensor["JewelSensor"]
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