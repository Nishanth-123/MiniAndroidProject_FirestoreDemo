package com.nishanth.firestoredemo

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TITLE = "Title"
        private const val DESCRIPTION = "Description"
    }

    lateinit var title: EditText
    lateinit var description: EditText
    lateinit var result: TextView
    private val db = FirebaseFirestore.getInstance()
    private val noteRef = db.document("Notebook/First Note")
    private val collectionRef = db.collection("Notebook")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = findViewById(R.id.title)
        description = findViewById(R.id.text)
        result = findViewById(R.id.result)
    }

    override fun onStart() {
        super.onStart()
        noteRef.addSnapshotListener(this) { documentSnapshot, e ->
            //exception may arise when accessing the doc is not possible
            if (e != null) {
                result.setText(e.message.toString())
                return@addSnapshotListener;
            }
            if (documentSnapshot!!.exists()) {
                val note:Note = documentSnapshot.toObject(Note.class)
                result.setText ("Title : ${note.title}\n" + "Title : ${note.getString(DESCRIPTION)}\n")
                //result.setText("Title : ${documentSnapshot!!.getString(TITLE)}\n" + "Title : ${documentSnapshot.getString(DESCRIPTION)}\n")
            } else {
                result.setText("")
            }
        }
    }

    fun saveData(view: View) {
        val note = Note(title.text.toString(), description.text.toString())
        /*
        val note = HashMap<String, Any>()
        note[TITLE] = title.text.toString()
        note[DESCRIPTION] = description.text.toString()

         */
        noteRef.set(note)
                .addOnSuccessListener {
                    Toast.makeText(this, "saved successfully", LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message.toString(), LENGTH_SHORT).show()
                }
        /*db.collection("Notebook").document("First Note").set(note)
                .addOnSuccessListener {
                    Toast.makeText(this, "saved successfully", LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message.toString(), LENGTH_SHORT).show()
                }*/
    }

    fun fetchData(view: View) {
        noteRef.get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        //it.data returns the map we had passed
                        //result.setText("Title : ${it.getString(TITLE)}\n" + "Title : ${it.getString(DESCRIPTION)}\n")
                        val note:Note = it.toObject(Note::class.java)
                        result . setText ("Title : ${note.title}\n" + "Title : ${note.getString(DESCRIPTION)}\n")
                    } else {
                        result.setText("Document does not exist")
                    }
                }
                .addOnFailureListener {
                    result.setText(it.message.toString())
                }
    }

    fun deleteDescription(view: View) {
        /*val note = HashMap<String, Any>()
        note[DESCRIPTION] = description.text.toString()
        noteRef.update(DESCRIPTION, FieldValue.delete())*/
        noteRef.update(DESCRIPTION, FieldValue.delete())
    }

    fun deleteNotes(view: View) {
        noteRef.delete()
    }
}