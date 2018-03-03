package org.firstinspires.ftc.teamcode.config

/**
 * Describes a customized type that allows individual modules to use configuration.
 * Upon extension, the following format is recommended:
 * ```
 *   class Config : ConfigUser("myfile.properties") {
 *     val customItem = file.getDouble("CustomItem");
 *     ...
 *   }
 * ```
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
open class ConfigUser(filename: String) {
    val file = ConfigFile(filename)

    // All other fields shall be defined in classes extending this
}