package org.firstinspires.ftc.teamcode.config

import android.util.Log
import android.util.NoSuchPropertyException
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.util.*

/**
 * Provides abstraction of a singular configuration file in internal storage.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
class ConfigFile(val filename: String) {
    companion object {
        // The location of configuration files
        const val CONFIG_PATH = "/storage/self/primary/FIRST/config"
    }

    val properties = Properties()

    init {
        // Populate Properties

        try {
            FileReader(File(CONFIG_PATH, filename)).use { file ->
                properties.load(file)
            }

            // Hardware is not initialized at this point.
        } catch (ferrno: FileNotFoundException) {
            Log.e("ConfigFile error!", "Internal storage file $filename not found")
            throw ferrno

        } catch (io: IOException) {
            Log.e("ConfigFile error!",
                    "Internal storage file $filename cannot be accessed (${io.message}")
            throw io
        }
    }

    operator fun get(key: String): String =
            properties.getProperty(key) ?:
                    throw NoSuchPropertyException("$key in $this")

    // Typed Get

    /**
     * Retrieves a double-precision floating point number from the file with the given property key.
     *
     * @param key The key of the requested value
     * @returns The requested value
     * @throws NumberFormatException If the value is not a valid double
     */
    @Throws(NumberFormatException::class)
    fun getDouble(key: String): Double =
            this[key].toDouble()

    /**
     * Retrieves an integer from the file with the given property key.
     *
     * @param key The key of the requested value
     * @returns The requested value
     * @throws NumberFormatException If the value is not a valid integer
     */
    @Throws(NumberFormatException::class)
    fun getInteger(key: String): Int =
            this[key].toInt()

    /**
     * Retrieves a boolean from the file with the given property key.
     *
     * Only two values are valid:
     * - `"true"` (true)
     * - `"false"` (false)
     *
     * @param key The key of the requested value
     * @returns The requested value
     * @throws InvalidPropertiesFormatException If the value is not a valid boolean representation
     */
    @Throws(InvalidPropertiesFormatException::class)
    fun getBoolean(key: String): Boolean = when (this[key]) {
        "true" -> true
        "false" -> false
        else -> throw InvalidPropertiesFormatException(
                "getBoolean called on non-boolean ConfigFile value '${this[key]}'")
    }

    override fun toString(): String =
            "[ConfigFile $filename (${properties.size} pairs)]"
}
