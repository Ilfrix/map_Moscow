package com.example.maps

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Pedometr{
    var user_id : String
    var date : String
    var steps_value : Long
    var distance : Double
    //var route_id

    constructor(pedometr: Pedometr){
        this.user_id = pedometr.user_id
        this.date = pedometr.date
        this.steps_value = pedometr.steps_value
        this.distance = pedometr.distance
    }
    constructor(user_id : String, date : String, steps_value : Long, distance : Double){
        this.user_id = user_id
        this.date = date
        this.steps_value = steps_value
        this.distance = distance
    }
    constructor(){
        this.user_id = "NONE"
        this.date = "01.01.01"
        this.steps_value = 0
        this.distance = 0.0
    }
    fun write(){
        val data_key = "Pedometr"
        val data_base = FirebaseDatabase.getInstance().getReference(data_key)
        data_base.push().setValue(this)
    }

    fun load():Pedometr?{
        //mapBox

        val data_key = "Pedometr"
        var new_data : Pedometr? = null
        val data_base = FirebaseDatabase.getInstance().getReference(data_key)
        val vListener = object : ValueEventListener {
            override  fun onDataChange(snapshot: DataSnapshot){
                for (snapshot in snapshot.children) {
                    if (snapshot != null) {
                        val snap_val = snapshot.value
                        val new_date: String? =
                            (snap_val as? HashMap<String, Any>)?.get("date")?.toString()
                        val new_user_id: String? =
                            (snap_val as? HashMap<String, Any>)?.get("user_id")?.toString()
                        val new_steps_value: Long? =
                            (snap_val as? HashMap<String, Any>)?.get("steps_value") as Long?
                        val new_distance: Double? =
                            (snap_val as? HashMap<String, Any>)?.get("distance") as Double?
                        if (new_user_id != null && new_date != null && new_steps_value != null && new_distance != null) {
                            new_data =
                                Pedometr(new_user_id, new_date, new_steps_value, new_distance)
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                val t = "pass"
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        data_base.addValueEventListener(vListener)
        print(new_data)
        return new_data
    }

}