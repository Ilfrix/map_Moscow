package com.example.maps


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.*
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.runtime.image.ImageProvider



class MainActivity : AppCompatActivity() {
    private lateinit var mapView: com.yandex.mapkit.mapview.MapView
    private lateinit var button: Button
    private lateinit var placeMark1: PlacemarkMapObject
    private lateinit var placeMark2: PlacemarkMapObject
    private lateinit var placeMark3: PlacemarkMapObject
    private lateinit var bufMark: PlacemarkMapObject

    private var mapObjectTapListener: MapObjectTapListener =
        MapObjectTapListener { mapObject, point ->
            val mark = mapObject as PlacemarkMapObject
            var ppoint: Point = Point(mark.geometry.latitude, mark.geometry.longitude)
            mapView.map.move(
                CameraPosition(ppoint, 16.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 2f), null
            )
            return@MapObjectTapListener true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("f727989a-ecd4-4f05-a90d-f923d9179f62")
        MapKitFactory.initialize(this)

        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button)

        mapView = findViewById(R.id.mapview)
        mapView.map.move(
            CameraPosition(Point(53.327300, 50.316413), 16.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f), null
        )


        mapView.map.mapObjects.addPlacemark(Point(53.327300, 50.316413))

        placeMark1 = mapView.map.mapObjects.addPlacemark(
            Point(53.327300, 50.316413),
            ImageProvider.fromResource(this, R.drawable.ic_launcher_foreground)
        )
        placeMark2 = mapView.map.mapObjects.addPlacemark(
            Point(53.327300, 50.416413),
            ImageProvider.fromResource(this, R.drawable.ic_launcher_foreground)
        )

        placeMark1.addTapListener(mapObjectTapListener)
        placeMark2.addTapListener(mapObjectTapListener)

        button.setOnClickListener { make_new_marker() }


    }

    private fun make_new_marker() {
        mapView.map.mapObjects.addPlacemark(mapView.map.cameraPosition.target)
    }

    private fun clickMarker(mark: PlacemarkMapObject) {
        bufMark = mark
        bufMark.addTapListener(mapObjectTapListener)
    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }


    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}




