package com.lucagiorgetti.surprix.utility.dao

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lucagiorgetti.surprix.SurprixApplication
import com.lucagiorgetti.surprix.listenerInterfaces.CallbackInterface
import com.lucagiorgetti.surprix.listenerInterfaces.FirebaseListCallback
import com.lucagiorgetti.surprix.model.Set
import com.lucagiorgetti.surprix.model.Year

object YearDao {
    private val years = SurprixApplication.instance.databaseReference!!.child("years")
    fun getYearById(yearId: String?, listen: CallbackInterface<Year>) {
        years.child(yearId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val y = snapshot.getValue(Year::class.java)!!
                    listen.onSuccess(y)
                }
                listen.onFailure()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                listen.onFailure()
            }
        })
    }

    fun getYearSets(yearId: String?, listen: FirebaseListCallback<Set>) {
        listen.onStart()
        val sets = ArrayList<Set>()
        years.child(yearId!!).child("sets").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (d in dataSnapshot.children) {
                        SetDao.getSetById(d.key, object : CallbackInterface<Set> {
                            override fun onStart() {}
                            override fun onSuccess(item: Set) {
                                sets.add(item)
                                listen.onSuccess(sets)
                            }

                            override fun onFailure() {}
                        })
                    }
                } else {
                    listen.onSuccess(sets)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}