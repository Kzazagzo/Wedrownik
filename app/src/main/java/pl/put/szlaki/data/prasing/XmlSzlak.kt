package pl.put.szlaki.data.prasing

import pl.put.szlaki.domain.Punkt
import pl.put.szlaki.domain.Szlak

data class XmlSzlak(
    val metadata: Metadata?,
    val trk: Trk?,
) {
    data class Metadata(
        val link: Link?,
        val time: String?,
        val bounds: Bounds?,
    ) {
        data class Bounds(
            val maxlat: Double?,
            val maxlon: Double?,
            val minlat: Double?,
            val minlon: Double?,
        )
    }

    data class Trk(
        val name: String?,
        val link: Link?,
        val trkseg: Trkseg?,
    ) {
        data class Trkseg(
            val trkpt: List<Trkpt>?,
        ) {
            data class Trkpt(
                val lat: Double?,
                val lon: Double?,
                val ele: Double?,
                val time: String?,
            )
        }
    }

    data class Link(
        val href: String?,
        val text: String?,
    )

    fun asDomainModelToSzlak(name: String? = null): Szlak {
        val punkty = asDomainModelToPunkty(name)

        return Szlak(
            name = this.trk?.name ?: name!!,
            link =
                listOfNotNull(
                    this.metadata?.link?.href,
                    this.trk?.link?.href,
                ).maxByOrNull(String::length),
            creationTime = this.metadata?.time,
            bounds = this.calculateBounds(),
            stopWatchTime = 0L,
            punkty = punkty,
            szlakLength = punkty.sumOf { it.roadToNextPoint ?: 0.0 },
            selectedSzlakDificulty = "Normal",
        )
    }

    fun asDomainModelToPunkty(name: String? = null): List<Punkt> {
        val punkty = mutableListOf<Punkt>()
        var prevPunkt: Punkt? = null

        this.trk?.trkseg?.trkpt?.forEach { trkpt ->
            val lat = trkpt.lat
            val lon = trkpt.lon
            if (lat != null && lon != null) {
                val currentPunkt =
                    Punkt(
                        szlakName = this.trk.name ?: name!!,
                        pointNumber = punkty.size.toLong() + 1,
                        coordinates = Punkt.Coordinates(latitude = lat, longitude = lon),
                        elevation = trkpt.ele,
                        time = trkpt.time,
                        roadToNextPoint = null,
                        visited = false,
                    )
                currentPunkt.roadToNextPoint =
                    prevPunkt?.let { currentPunkt.calculateDistanceBetweenOtherPoint(it) }
                punkty.add(currentPunkt)
                prevPunkt = currentPunkt
            }
        }
        return punkty
    }

    private fun calculateBounds(): Szlak.Bounds {
        return this.metadata?.bounds?.let {
            Szlak.Bounds(
                maxlat = it.maxlat!!,
                maxlon = it.maxlon!!,
                minlat = it.minlat!!,
                minlon = it.minlon!!,
            )
        } ?: return Szlak.Bounds(
            maxlon = this.trk?.trkseg?.trkpt?.mapNotNull { it.lon }?.maxOrNull()!!,
            minlon = this.trk.trkseg.trkpt.mapNotNull { it.lon }.minOrNull()!!,
            maxlat = this.trk.trkseg.trkpt.mapNotNull { it.lat }.maxOrNull()!!,
            minlat = this.trk.trkseg.trkpt.mapNotNull { it.lat }.minOrNull()!!,
        )
    }
}
