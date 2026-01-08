package com.example.myfirebase.repositori

import android.util.Log
import com.example.myfirebase.modeldata.Siswa
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface RepositorySiswa {
    suspend fun getDataSiswa(): List<Siswa>
    suspend fun postDataSiswa(siswa: Siswa)
    suspend fun getSatuSiswa(id: Long): Siswa?
    suspend fun editSatuSiswa(id: Long, siswa: Siswa)
    suspend fun hapusSatuSiswa(id: Long)
}

class FirebaseRepositorySiswa : RepositorySiswa {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("siswa")

    override suspend fun getDataSiswa(): List<Siswa> {
        return try {
            collection.get().await().documents.map { doc ->
                val id = doc.getLong("id") ?: doc.id.hashCode().toLong()
                Siswa(
                    id = id,
                    nama = doc.getString("nama") ?: "",
                    alamat = doc.getString("alamat") ?: "",
                    telpon = doc.getString("telpon") ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("REPO", "Error get data: ${e.message}")
            emptyList()
        }
    }

    override suspend fun postDataSiswa(siswa: Siswa) {

        val docRef = collection.document()

        val idBaru = docRef.id.hashCode().toLong()

        Log.d("REPO", "Menyimpan data: $idBaru, ${siswa.nama}")

        val data = hashMapOf(
            "id" to idBaru,
            "nama" to siswa.nama,
            "alamat" to siswa.alamat,
            "telpon" to siswa.telpon
        )

        docRef.set(data).await()
    }

    override suspend fun getSatuSiswa(id: Long): Siswa? {
        return try {
            val query = collection.whereEqualTo("id", id).get().await()
            val doc = query.documents.firstOrNull()
            doc?.let {
                Siswa(
                    id = it.getLong("id") ?: id,
                    nama = it.getString("nama") ?: "",
                    alamat = it.getString("alamat") ?: "",
                    telpon = it.getString("telpon") ?: ""
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun editSatuSiswa(id: Long, siswa: Siswa) {
        val query = collection.whereEqualTo("id", id).get().await()
        val doc = query.documents.firstOrNull()
        doc?.reference?.set(
            mapOf(
                "id" to id,
                "nama" to siswa.nama,
                "alamat" to siswa.alamat,
                "telpon" to siswa.telpon
            )
        )?.await()
    }

  