package de.danielmaile.lama.aether

import com.comphenix.protocol.ProtocolLibrary
import de.danielmaile.lama.aether.commands.CommandAether
import de.danielmaile.lama.aether.config.ConfigManager
import de.danielmaile.lama.aether.config.LanguageManager
import de.danielmaile.lama.aether.item.ListenerBlock
import de.danielmaile.lama.aether.item.ListenerConverter
import de.danielmaile.lama.aether.item.ListenerCrafting
import de.danielmaile.lama.aether.item.Recipes
import de.danielmaile.lama.aether.item.funtion.ListenerArmor
import de.danielmaile.lama.aether.item.funtion.ListenerItem
import de.danielmaile.lama.aether.item.funtion.magicwand.ListenerMagicWand
import de.danielmaile.lama.aether.item.funtion.particle.ListenerParticle
import de.danielmaile.lama.aether.item.funtion.particle.ParticleManager
import de.danielmaile.lama.aether.mobs.ListenerAetherMobs
import de.danielmaile.lama.aether.mobs.RideableLlama
import de.danielmaile.lama.aether.portal.ListenerPortal
import de.danielmaile.lama.aether.util.logError
import de.danielmaile.lama.aether.world.AetherWorld
import de.danielmaile.lama.aether.world.ListenerAetherWorld
import de.danielmaile.lama.aether.world.ObjectManager
import de.danielmaile.lama.aether.world.PrefabType
import de.danielmaile.lama.aether.world.cloud.CloudEffects
import de.danielmaile.lama.aether.world.dungeon.ListenerDungeon
import de.danielmaile.lama.lamaapi.LamaAPI
import de.danielmaile.lama.license.LicenseManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File


class Aether : JavaPlugin() {

    companion object {

        @JvmStatic
        lateinit var instance: Aether
            private set
    }

    lateinit var configManager: ConfigManager
        private set

    lateinit var lamaAPI: LamaAPI
        private set

    lateinit var particleManager: ParticleManager
        private set

    lateinit var aetherWorld: AetherWorld
        private set

    fun getLanguageManager(): LanguageManager {
        return configManager.languageManager
    }

    fun getObjectManager(): ObjectManager {
        return aetherWorld.objectManager
    }

    override fun onEnable() {
        instance = this
        lamaAPI = Bukkit.getServer().pluginManager.getPlugin("LamaAPI") as LamaAPI

        //Register listener and commands
        server.pluginManager.registerEvents(ListenerPortal(), this)
        server.pluginManager.registerEvents(ListenerBlock(), this)
        server.pluginManager.registerEvents(ListenerCrafting(), this)
        server.pluginManager.registerEvents(ListenerDungeon(), this)
        server.pluginManager.registerEvents(ListenerAetherMobs(), this)
        server.pluginManager.registerEvents(ListenerItem(), this)
        server.pluginManager.registerEvents(ListenerArmor(), this)
        server.pluginManager.registerEvents(ListenerParticle(), this)
        server.pluginManager.registerEvents(ListenerConverter(), this)
        server.pluginManager.registerEvents(ListenerMagicWand(), this)
        server.pluginManager.registerEvents(ListenerAetherWorld(), this)
        Bukkit.getPluginCommand("aether")?.setExecutor(CommandAether())

        //Create data folder
        val dataFolder = File(dataFolder.absolutePath + File.separator + "data")
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            logError("Creation of data folder (" + dataFolder.absolutePath + ") failed!")
        }

        //Create locales folder
        val localesFolder = File(getDataFolder().absolutePath + File.separator + "locales")
        if (!localesFolder.exists() && !localesFolder.mkdirs()) {
            logError("Creation of locales folder (" + dataFolder.absolutePath + ") failed!")
        }

        //Save default files
        saveDefaultConfig()
        val deFile = File(getDataFolder().absolutePath + File.separator + "locales" + File.separator + "de.yml")
        if (!deFile.exists()) {
            saveResource("locales/de.yml", false)
        }
        reloadConfig()

        //Recipes
        registerRecipes()

        //Protocol lib
        val protocolManager = ProtocolLibrary.getProtocolManager()
        RideableLlama(protocolManager)

        //Init world and prefabs
        aetherWorld = AetherWorld()
        PrefabType.loadPrefabs()

        //Particle manager
        particleManager = ParticleManager()

        //Cloud effects
        CloudEffects()

        //Validate license
        LicenseManager(this, "LamasNewAether")
    }

    override fun onDisable() {
        getObjectManager().save()
    }

    private fun registerRecipes() {
        for (recipe in Recipes.values()) {
            Bukkit.addRecipe(recipe.recipe)
        }
    }

    override fun reloadConfig() {
        super.reloadConfig()
        configManager = ConfigManager()
    }
}

fun inst(): Aether {
    return Aether.instance
}