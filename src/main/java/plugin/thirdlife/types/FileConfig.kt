package plugin.thirdlife.types

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import plugin.thirdlife.Main
import plugin.thirdlife.logger
import java.io.File
import java.io.IOException
class ConfigFile: FileConfig("config.yml")

abstract class FileConfig(name: String){
    var config: FileConfiguration = getConfig(name)
    var name = name

    private fun getConfig(name: String): FileConfiguration {
        val file = File(Main.getInstance().dataFolder, name)
        file.createNewFile()
        return YamlConfiguration.loadConfiguration(file)
    }

    fun set(path: String, value: Any?){
        config.set(path,value)
        config.saveFile(File(Main.getInstance().dataFolder, name))
    }
    fun get(path: String): Any?{
        return config.get(path)
    }
    fun getKeys(deep: Boolean): MutableSet<String>{
        return config.getKeys(deep)
    }
}


fun FileConfiguration.saveFile(file: File){
    try{
        this.save(file)
    }catch(exception: IOException){
        logger().warning(exception.toString())
        throw IOException("Error writing to disk")
    }
}