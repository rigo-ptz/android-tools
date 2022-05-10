package com.oxygen.ktx_ui.maps

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.PolyUtil

enum class MapZoomLevel(val zoomLevel: Int) {
    WORLD(1),
    CONTINENT(5),
    CITY(10),
    CITY_STREETS(12),
    STREETS(15),
    STREETS_BUILDINGS(17),
    BUILDINGS(20)
}

/**
 * A polygon that cover the whole map.
 * Useful for cases like showing the rest of the world as a non-operational zone and holes as operational zone.
 */
val WORLD_MAP_ZONE = listOf(
    LatLng(-89.999999999999, -180.0),
    LatLng(89.99999999999, -180.0),
    LatLng(89.99999999999, 179.99999999),
    LatLng(-89.99999999999, 179.99999999),
    LatLng(-89.99999999999, 0.0)
)

object MapUtil {
    fun createLatLngBounds(encodedPolylines: List<String>): LatLngBounds {
        val builder = LatLngBounds.Builder()
        encodedPolylines.forEach { polyline ->
            if (polyline.isNotEmpty()) {
                PolyUtil.decode(polyline).forEach { latLng -> builder.include(latLng) }
            }
        }
        return builder.build()
    }

    fun createLatLngBounds(encodedPolyline: String): LatLngBounds {
        val builder = LatLngBounds.Builder()
        if (encodedPolyline.isNotEmpty()) {
            val decodedPathLatLngList = PolyUtil.decode(encodedPolyline)
            for (latLng in decodedPathLatLngList) {
                builder.include(latLng)
            }
        }
        return builder.build()
    }

}

fun GoogleMap.zoomTo(position: LatLng, mapZoomLevel: MapZoomLevel, animate: Boolean = true) {
    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
        position, mapZoomLevel.zoomLevel.toFloat()
    )
    if (animate) {
        animateCamera(cameraUpdate)
    } else {
        moveCamera(cameraUpdate)
    }
}
