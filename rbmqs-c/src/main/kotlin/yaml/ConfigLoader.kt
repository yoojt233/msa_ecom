package service.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

object ConfigLoader {
    val config: AppConfig

    init {
        val mapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule.Builder().build())
        val configFileStream = this::class.java.classLoader.getResourceAsStream("application.yml")

        config = mapper.readValue(configFileStream)
    }
}