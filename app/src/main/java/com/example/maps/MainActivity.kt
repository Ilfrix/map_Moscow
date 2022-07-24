package com.example.maps


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.room.Database
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.*
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.runtime.image.ImageProvider



class MainActivity : AppCompatActivity() {
    private lateinit var mapView: com.yandex.mapkit.mapview.MapView
    private lateinit var button: Button
    private lateinit var button_save: Button
    private lateinit var placeMark1: PlacemarkMapObject
    private lateinit var placeMark2: PlacemarkMapObject
    private lateinit var placeMark3: PlacemarkMapObject
    private lateinit var bufMark: PlacemarkMapObject
    private lateinit var Current_Path : ArrayList<Point> //current path
    private lateinit var data_base: DatabaseReference   //variable for firebase
    private val data_key : String = "USER'S_PATH"   //key for firebase
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
        button_save = findViewById(R.id.save_path)

        mapView = findViewById(R.id.mapview)
        //move camera to koordinations
        mapView.map.move(
            CameraPosition(Point(53.327300, 50.316413), 16.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f), null
        )

        //add mark in map
        mapView.map.mapObjects.addPlacemark(Point(53.327300, 50.316413))
        //mark for example first
        placeMark1 = mapView.map.mapObjects.addPlacemark(
            Point(53.327300, 50.316413),
            ImageProvider.fromResource(this, R.drawable.ic_launcher_foreground)
        )
        //mark for example second
        placeMark2 = mapView.map.mapObjects.addPlacemark(
            Point(53.327300, 50.416413),
            ImageProvider.fromResource(this, R.drawable.ic_launcher_foreground)
        )

        placeMark1.addTapListener(mapObjectTapListener)
        placeMark2.addTapListener(mapObjectTapListener)
        //create new mark
        button.setOnClickListener { make_new_marker() }
        button_save.setOnClickListener{ onClickSave_path(mapView)}


    }

    fun onClickSave_path(view: View) {
        data_base = FirebaseDatabase.getInstance().getReference(data_key)
        val tmp_point : Point = Point(53.327300, 50.316413)
        val tmp_point2: Point = Point(53.327300, 50.416413)
        Current_Path.add(tmp_point)
        Current_Path.add(tmp_point2)
        val id : String = data_base.getKey().toString()
        data_base.push().setValue(id, Current_Path)
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




