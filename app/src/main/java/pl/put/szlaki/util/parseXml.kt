package pl.put.szlaki.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

inline fun <reified T> parseFile(xmlText: String?): T? {
    val xmlDeserializer =
        XmlMapper(
            JacksonXmlModule().apply {
                setDefaultUseWrapper(false)
            },
        ).registerKotlinModule()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
    return xmlDeserializer.readValue(xmlText, T::class.java)
}
