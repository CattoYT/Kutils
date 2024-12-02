package dev.hikari.kutils.client.utils
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.util.Base64
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.io.encoding.ExperimentalEncodingApi

class Encryption {
    fun getSystemInfo(command: String): String {
        val process = Runtime.getRuntime().exec(command)
        return process.inputStream.bufferedReader().use(BufferedReader::readText).trim()
            .lines()
            .drop(1) // Skip the first line (header)
            .joinToString("") // Combine the remaining lines
            .trim() // Trim any extra whitespace
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun generateHardwareKey(): String {

        //if this fails, don't dm me i dont care
        
        val osArch = System.getProperty("os.arch") ?: "unknown_arch"
        
        val osName = System.getProperty("os.name") ?: "unknown_os"
        println(osName)
        var cpuId: String = ""
        var serialNumber: String = ""
        var gpuIds: String = ""
        var availableProcessors: String = Runtime.getRuntime().availableProcessors().toString()
        
        if (osName.contains("Linux", ignoreCase = true)) {
            cpuId = getSystemInfo("cat /proc/cpuinfo | grep 'processor' | wc -l") // Linux
            serialNumber = getSystemInfo("sudo dmidecode -s system-serial-number") // Linux
            gpuIds = getSystemInfo("lspci -v | grep VGA") // Linux
        } else if (osName.contains("Windows", ignoreCase = true)) {
            // Check for Windows 10 or any other Windows version

            cpuId = getSystemInfo("wmic cpu get ProcessorId") // Windows

            serialNumber = getSystemInfo("wmic bios get SerialNumber") // Windows

            gpuIds = getSystemInfo("wmic PATH Win32_VideoController GET PNPDeviceID\n") // Windows
            availableProcessors = Runtime.getRuntime().availableProcessors().toString()

        } else {
            cpuId = getSystemInfo("system_profiler SPHardwareDataType | grep 'Processor Name'") // Mac OS
            serialNumber = getSystemInfo("system_profiler SPHardwareDataType | grep 'Serial Number'") // Mac OS
            gpuIds = getSystemInfo("system_profiler SPDisplaysDataType | grep 'Chipset Model'") // Mac OS
        }



        // Combine attributes into a consistent string and hash it
        val hardwareInfo = (cpuId + serialNumber + gpuIds + availableProcessors + osName + osArch).trim().toByteArray()
        val hardwareInfoHash = MessageDigest.getInstance("SHA-256").digest(hardwareInfo)
        
        // Use a deterministic salt (derived from a stable property)
        val salt = MessageDigest.getInstance("SHA-256").digest("fixed_salt".toByteArray())

        // Derive a 256-bit key using PBKDF2
        val keySpec = PBEKeySpec(String(hardwareInfoHash).toCharArray(), salt, 500_000, 256)
        
        val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        
        val keyBytes = keyFactory.generateSecret(keySpec).encoded
        
        // Return the key as a Base64 string
        println("Finished encrypting")
        return Base64.getEncoder().encodeToString(keyBytes)
    }

    fun encrypt(data: String): Pair<String, String> {
        val key = generateHardwareKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        try {
            // Generate a random IV (Initialization Vector)
            val iv = ByteArray(12).apply { SecureRandom().nextBytes(this) }
            val gcmSpec = GCMParameterSpec(128, iv)

            // Set up AES key
            val secretKey = SecretKeySpec(Base64.getDecoder().decode(key), "AES")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

            // Perform encryption
            val encryptedData = cipher.doFinal(data.toByteArray())
            
            // Return IV and encrypted data as Base64 strings
            return Base64.getEncoder().encodeToString(encryptedData) to Base64.getEncoder().encodeToString(iv)

        } catch (e: Exception) {
            // Handle encryption failure and log the error
            println("Error during encryption: ${e.message}")
            throw RuntimeException("Encryption failed", e)
        }
    }

    fun decrypt(encryptedData: String, iv: String): String {
        val key = generateHardwareKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(128, Base64.getDecoder().decode(iv))

        // Set up AES key
        val secretKey = SecretKeySpec(Base64.getDecoder().decode(key), "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

        // Perform decryption
        val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData))
        return String(decryptedBytes)
    }

}