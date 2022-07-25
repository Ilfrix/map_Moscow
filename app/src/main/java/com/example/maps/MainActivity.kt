package com.example.maps


import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.room.Database
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
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
    private lateinit var bufMark: PlacemarkMapObject
    private val Current_Path : ArrayList<Path> = ArrayList() //current path
    private lateinit var data_base: DatabaseReference   //variable for firebase
    private val data_key : String = "USER'S_PATH"   //key for firebase
    private var mapObjectTapListener: MapObjectTapListener =
            MapObjectTapListener { mapObject, point ->
                val mark = mapObject as PlacemarkMapObject
                val ppoint: Point = Point(mark.geometry.latitude, mark.geometry.longitude)
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

        val name_path = "WAY"
        var listData : ArrayList<Path> = ArrayList()
        val tmp_point : Path = Path(name_path, 60.327301.toString(), 51.316413.toString())
        val tmp_point2: Path = Path(name_path, 65.327305.toString(), 51.416413.toString())
        Current_Path.clear()
        Current_Path.add(tmp_point)
        Current_Path.add(tmp_point2)

        //data_base.push().setValue(Current_Path)

        val vListener = object : ValueEventListener {
            override  fun onDataChange(snapshot: DataSnapshot){
                for (snapshot in snapshot.children){
                    print(snapshot)
                    val tmp = snapshot.value
                    val len = tmp.toString().length
                    var k : String = tmp.toString().slice(1..len-2)
                    val arr = k.split(",").toMutableList()
                    var cur_index = 0
                    var v = 0
                    var new_path : ArrayList<Path> = ArrayList()
                    for (c in arr){
                        if (cur_index == 0)
                            arr[cur_index + v] = arr[cur_index + v].replace("{x=","")
                        else if(cur_index == 1)
                            arr[cur_index + v] = arr[cur_index + v].replace(" y=","")
                        else if(cur_index == 2) {
                            arr[cur_index + v] = arr[cur_index + v].replace(" path_name=", "")
                            arr[cur_index + v] = arr[cur_index + v].replace("}", "")
                        }

                        cur_index++
                        if (cur_index == 3) {
                            cur_index = 0
                            v += 3
                            val p: Path = Path(arr[v-3], arr[v-2], arr[v - 1])
                            new_path.add(p)
                        }
                    }

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                val t = "pass"
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        Toast.makeText(this, "Успешно!", Toast.LENGTH_SHORT).show()
        data_base.addValueEventListener(vListener)


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



class Path{
    public var path_name: String
    public var x: String
    public var y: String

    constructor(id: String, x: String, y: String) {
        this.path_name = id
        this.x = x
        this.y = y
    }
    constructor(p : Path){
        this.path_name = p.path_name
        this.x = p.x
        this.y = p.y
    }
    constructor(){
        this.path_name = "NOT FOUND"
        this.x = "0"
        this.y = "0"
    }
}





